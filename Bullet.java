// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Bullet - A rectangular actor that despawns after travelling some distance, and isn't affected by gravity

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Bullet extends Actor
{
    public final int ENTITY_BULLET_DESPAWN_DISTANCE = 500;
    public final static double[] HITBOX_X_POINTS = new double[]{0.5, 0.5, -0.5, -0.5};      //expressed as a ratio of width
    public final static double[] HITBOX_Y_POINTS = new double[]{0.5, -0.5, -0.5, 0.5};  //expressed as a ratio of height
    public final static int DEFAULT_BULLET_DAMAGE = 10;

    private double bulletWidth;
    private double bulletHeight;
    private double angle;

    private HitBox hitBox;

    private double spawnX;
    private double spawnY;
    private double despawnDistance;

    // precondition : "entityName"_bullet.gif is the name of the bullet icon used to display the bullet icon
    // postcondition: creates a bullet that is unaffected by gravity
    public Bullet(double xPos, double yPos, double velocity, double angle, double width, double height, String entityName, Color col)
    {
        super(xPos, yPos, velocity * Math.cos(Math.PI/2 - angle), 
            - velocity * Math.sin(Math.PI/2 - angle), 
            "bullet_gifs/"+entityName+"_bullet.gif", col);
        this.angle = angle;
        this.bulletWidth = width;
        this.bulletHeight= height;

        /* creates a hitbox. 
         * HITBOX_X_POINTS and HITBOX_Y_POINTS are doubles that express the 
         * bounding box of the hitbox as a fraction of the entity's width and height
         */ 
        int[] hitboxXPt = new int[HITBOX_X_POINTS.length];
        int[] hitboxYPt = new int[HITBOX_Y_POINTS.length];
        /* this for loop creates a new set of points that define the position of the 
         * coordinates of the bounding hit box relative to the center of the bullet 
         */
        for(int i=0; i<HITBOX_X_POINTS.length; i++)
        {
            hitboxXPt[i] = (int)(bulletWidth  * HITBOX_X_POINTS[i]);
            hitboxYPt[i] = (int)(bulletHeight * HITBOX_Y_POINTS[i]);
        }
        // creates the hitbox using those points
        this.hitBox = new HitBox(hitboxXPt, hitboxYPt, this);

        this.spawnX = xPos;
        this.spawnY = yPos;

        if(entityName == "player")
        {
            despawnDistance = Player.PLAYER_BULLET_DESPAWN_DISTANCE;
        }
        else
        {
            despawnDistance = ENTITY_BULLET_DESPAWN_DISTANCE;
        }
    }

    // precondition : none
    // postcondition: recalculates the location of the center of the bullet in terms of frame coordinates
    public void recalculateRelativeScreenLocation(Camera c)
    {
        double relativeXLocation = Driver.defaultWidth/2 + (getXPos() - c.getXPos() - this.bulletWidth/2);
        double relativeYLocation = Driver.defaultHeight/2 + (getYPos() - c.getYPos() - this.bulletHeight/2);
        // subtract width/2 and height/2 because it prints it at top left location
        double [] location = new double[] {relativeXLocation, relativeYLocation};
        setScreenLocation(location);
    }

    // precondition : none
    // postcondition: returns the hit box that checks for collisions
    public HitBox getHitBox()
    {
        return this.hitBox;
    }

    // precondition : none
    // postcondition: gets the bullet's angle
    public double getAngle()
    {
        return this.angle;
    }

    // precondition : none
    // postcondition: gets the bullet's damage
    public int getDamage()
    {
        return DEFAULT_BULLET_DAMAGE;
    }

    // precondition : none
    // postcondition: does nothing, overrides actor method as bullets aren't influenced by gravity
    public void recalculateAcceleration(ArrayList<Planet> planets)
    {}

    // precondition : none
    // postcondition: does nothing, overrides actor method as bullets velocity doesn't change
    public void recalculateVelocity(long time)
    {}

    // precondition : angle is the angle the bullet is travelling
    // postcondition: rotates the graphics frame, paints the bullet, and unrotates the graphics frame
    public void paintComponent(Graphics g)
    {
        // rotates Graphics g 'angle' radians around center of entity
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(angle, Driver.scale*(this.getScreenX()+this.bulletWidth/2), 
            Driver.scale*(this.getScreenY()+this.bulletHeight/2));
        // the coordinates ^ are the screen coordinates the graphics object rotates around

        // paints the component vertically with respect to the rotated Graphics frame
        paintComponentVertical(g2);

        // unrotates Graphics g 'angle' radians around center of entity
        g2.rotate(-angle, Driver.scale*(this.getScreenX()+this.bulletWidth/2), 
            Driver.scale*(this.getScreenY()+this.bulletHeight/2));
    }

    // precondition : none
    // postcondition: pains the bullet vertially with respect to Graphics frame
    public void paintComponentVertical(Graphics g)
    {
        double[] location = getScreenLocation();
        //g.fillRect((int)location[0], (int)location[1], 10, 10);
        g.drawImage(getImage().getImage(), (int)(Driver.scale*location[0]), (int)(Driver.scale*location[1]),
            (int)(Driver.scale*bulletWidth), (int)(Driver.scale*bulletHeight), this);
    }

    // precondition : none
    // postcondition: checks if the bullet should despawn based on distance travelled
    public boolean checkDespawn()
    {
        return this.getDistance(spawnX, spawnY)>=despawnDistance;
    }
}