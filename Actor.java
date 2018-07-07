// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Actor - is a general actor that moves around the level and is displayed in Frame

import java.util.*;
import javax.swing.*;
import java.awt.*;

public abstract class Actor extends JPanel
{
    private double xPos;
    private double yPos;
    private double xV;
    private double yV;
    private double xA;
    private double yA;

    private Color mapColor;

    private double[] screenLocation;

    private String filePath;
    private ImageIcon image;

    /* precondition : filePath defines the location of the display image relative to resources/actor_gifs/
     *          col defines the color of the icon on the map, should it be displayed on the map
     */         
    // postcondition: constructs the actor that moves around the screen
    public Actor(double xPos, double yPos, double xV, double yV, String filePath, Color col)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xV = xV;
        this.yV = yV;
        xA = 0;
        yA = 0;
        this.filePath = "resources/actor_gifs/"+filePath;
        image = new ImageIcon(this.filePath);
        this.mapColor = col;
    }

    // precondition : none
    // postcondition: returns the filePath, and variables concerning movement in a string
    public String toString()
    {
        return "filePath="+filePath+", xPos="+xPos+", yPos="+yPos+", xV="+xV+", yV="+yV;
    }

    // precondition : none
    // postcondition: returns the x position of the center of actor in terms of the level coordinates
    public double getXPos()
    {
        return xPos;
    }

    // precondition : recalculateRelativeScreenLocation(Camera) has been called
    // postcondition: returns the x position of the center of actor in terms of frame coordinates
    public double getScreenX()
    {
        return screenLocation[0];
    }

    // precondition : none
    // postcondition: returns the y position of the center of the actor in terms of the level coordinates
    public double getYPos()
    {
        return yPos;
    }

    // precondition : none
    // postcondition: returns the y position of the center of actor in terms of frame coordinates
    public double getScreenY()
    {
        return screenLocation[1];
    }

    // precondition : none
    // postcondition: sets the x and y position of the actor in frame coordinates
    public void setPosition(double x, double y)
    {
        this.xPos = x;
        this.yPos = y;
    }

    // precondition : none
    // postcondition: sets the x and y velocities of the actor
    public void setVelocity(double x, double y)
    {
        this.xV = x;
        this.yV = y;
    }

    // precondition : none
    // postcondition: returns the x velocity of the actor
    public double getXV()
    {
        return this.xV;
    }

    // precondition : none
    // postcondition: returns the y velocity of the actor
    public double getYV()
    {
        return this.yV;
    }

    // precondition : none
    // postcondition: returns the x acceleration of the actor
    public double getXA()
    {
        return this.xA;
    }

    // precondition : none
    // postcondition: returns the y acceleration of the actor
    public double getYA()
    {
        return this.yA;
    }

    // precondition : recalculateRelativeScreenLocation(Camera) has been called
    // postcondition: returns the screen location of the center of actor in frame coordinates in format {x,y}
    public double[] getScreenLocation()
    {
        return screenLocation;
    }

    // precondition : loc is in format {x,y}
    // postcondition: sets the screen location of the center of actor in frame coordinates
    public void setScreenLocation(double[] loc)
    {
        screenLocation = loc;
    }

    // precondition : none
    // postcondition: recalculates the relative screen location of center of actor and updates those variables in actor using setScreenLocation(double[])
    public abstract void recalculateRelativeScreenLocation(Camera c);

    // precondition : none
    // postcondition: returns the color of the icon on the map
    public Color getMapColor()
    {
        return this.mapColor;
    }

    // precondition : none
    // postcondition: sets the color of the icon on the map
    public void setMapColor(Color col)
    {
        this.mapColor = col;
    }

    // precondition : none
    // postcondition: returns the ImageIcon that is used to display the Actor on frame
    public ImageIcon getImage()
    {
        return this.image;
    }

    // precondition : acc contains {xAcceleration, yAcceleration}
    // postcondition: sets the acceleration of the actor
    public void changeAcceleration(double[] acc)
    {
        this.xA += acc[0];
        this.yA += acc[1];
    }

    // precondition : none
    // postcondition: returns the distance between the center of this and the x and y coordinates provided
    public double getDistance(double otherX, double otherY)
    {
        double xSquared = Math.pow((this.xPos - otherX),2);
        double ySquared = Math.pow((this.yPos - otherY),2);
        return Math.pow( xSquared+ySquared, 0.5);
    }

    // precondition : none
    // postcondition: returns the distance between this's location and other's location
    public double getDistance(Actor other)
    {
        return getDistance(other.getX(), other.getY());
    }

    // precondition : none
    // postcondition: paints the component on the frame
    public abstract void paintComponent(Graphics g);

    // precondition : none
    // postcondition: recalculates acceleration by summing the individual accelerations from each planet
    public void recalculateAcceleration(ArrayList<Planet> planets)
    {
        xA = 0;
        yA = 0;

        for(Planet p: planets)
        {
            //if(this != p)
            {
                double distance = this.getDistance(p);
                double mass = p.getMass();
                double gravity = Driver.G_CONSTANT;

                double acceleration = gravity*mass/Math.pow(distance,2);

                xA += acceleration * (p.getXPos() - this.getXPos())/distance;
                yA += acceleration * (p.getYPos() - this.getYPos())/distance;
            }
        }
    }

    // precondition : time is in milliseconds since last time the velocity was updated
    // postcondition: recalculates the velocity 
    public void recalculateVelocity(long time)
    {
        xV += xA * time/1000;
        yV += yA * time/1000;
    }

    // precondition : time is in milliseconds since last time the position was updated
    // postcondition: recalculates the position
    public void recalculatePosition(long time)
    {
        xPos += xV * time/1000;
        yPos += yV * time/1000;
    }
}