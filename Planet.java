// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Planet - a circular actor with a radius amd mass

import java.util.*;
import javax.swing.*;
import java.awt.*;

import java.awt.Color;

public abstract class Planet extends Actor
{
    private int mass;
    private int radius;

    /* precondition : filePath is the ImageIcon, relative to "resources/actor_gifs/planet_gifs/"
     *                mapCol is the color of the planet's icon on the map
     * postcondition: constructs a circular actor
     */
    public Planet(double xPos, double yPos, double xV, double yV, int mass, int radius, String filePath, Color mapCol)
    {
        super(xPos, yPos, xV, yV, "planet_gifs/"+filePath, mapCol);
        this.mass = mass;
        this.radius = radius;
    }

    // precondition : none
    // postcondition: returns a string with the planet's positional variables and its mass and radius
    public String toString()
    {
        return super.toString() + ", mass="+mass+", radius="+radius;
    }

    // precondition : recalculateRelativeScreenLocation(Camera) has been called
    // postcondition: paints the image of the planet on the frame
    public void paintComponent(Graphics g)
    {
        double[] location = getScreenLocation();
        g.drawImage(getImage().getImage(), (int)(Driver.scale*location[0]), (int)(Driver.scale*location[1]),
            (int)(Driver.scale*2*radius), (int)(Driver.scale*2*radius), this);
    }

    // precondition : Camera's location has been updated
    // postcondition: updates the location of the planet in frame coordinates relative to the camera
    public void recalculateRelativeScreenLocation(Camera c)
    {
        double relativeXLocation = Driver.defaultWidth/2 + (getXPos() - c.getXPos() - radius);
        double relativeYLocation = Driver.defaultHeight/2 + (getYPos() - c.getYPos() - radius);
        // subtract width/2 and height/2 because it prints it at top left location
        double [] location = new double[] {relativeXLocation, relativeYLocation};
        setScreenLocation(location);
    }

    // precondition : none
    // postcondition: returns the mass of the planet
    public int getMass()
    {
        return this.mass;
    }

    // precondition : none
    // postcondition: returns the radius of the planet
    public int getRadius()
    {
        return this.radius;
    }
}