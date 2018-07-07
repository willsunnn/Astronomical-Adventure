// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Entity - creates an entity object

import java.util.*;
import javax.swing.*;
import java.awt.*;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public abstract class Entity extends Actor
{
    public final static double[] HITBOX_X_POINTS = new double[]{0.5, -0.5, 0};      //expressed as a ratio of width
    public final static double[] HITBOX_Y_POINTS = new double[]{0.25, 0.25, -0.5};  //expressed as a ratio of height

    public final static int healthBarLength = 40;
    public final static int healthBarHeight = 5;
    public final static int additionalSpaceForHealth = 5;

    private double entityWidth;
    private double entityHeight;
    private double angle;

    private HitBox hitBox;

    private final double maxHealth;
    private double currentHealth;
    private double damage;      //health per millesecond

    private File hurtSound;
    private File deathSound;

    // precondition : variables are as defined as in filereader
    // postcondition: initializes an entity with those variables
    public Entity(double xPos, double yPos, double xV, double yV, double width, double height, double health, String filePath, Color mapCol, double damage, File hurtSound, File deathSound)
    {
        super(xPos, yPos, xV, yV, "entity_gifs/"+filePath, mapCol);
        entityWidth = width;
        entityHeight = height;

        if(yV==0 || xV==0)
        {
            angle = 0;
        }
        else
        {
            this.angle = calculateAngle(xV, yV);
        }

        int[] hitboxXPt = new int[HITBOX_X_POINTS.length];
        int[] hitboxYPt = new int[HITBOX_Y_POINTS.length];
        for(int i=0; i<HITBOX_X_POINTS.length; i++)
        {
            hitboxXPt[i] = (int)(entityWidth  * HITBOX_X_POINTS[i]);
            hitboxYPt[i] = (int)(entityHeight * HITBOX_Y_POINTS[i]);
        }
        this.hitBox = new HitBox(hitboxXPt, hitboxYPt, this);

        maxHealth = health;
        currentHealth = health;
        this.damage = damage;

        this.hurtSound = hurtSound;
        this.deathSound = deathSound;
    }

    // precondition : dX is not 0
    // postcondition: calculates the angle using math
    public static double calculateAngle(double dX, double dY)
    {
        double angleRespectToHorizontal = Math.atan( - dY/ dX);  //negative because -Y is up
        if(dX>0)    // if V vector Q1 or Q4
        {
            angleRespectToHorizontal += Math.PI;
        }
        double angleRespectToVertical = - Math.PI/2 - angleRespectToHorizontal;
        return angleRespectToVertical;
    }

    // precondition : none
    // postcondition: rotates angle delta radians
    public void rotate(double delta)
    {
        this.angle += delta;
        angle = checkAngle(angle);
    }

    // precondition : none
    // postcondition: sets the hitbox
    public void setHitBox(HitBox hb)
    {
        this.hitBox = hb;
    }

    // precondition : none
    // postcondition: returns the entities width
    public double getEntityWidth()
    {
        return this.entityWidth;
    }

    // precondition : none
    // postcondition: returns the entities height
    public double getEntityHeight()
    {
        return this.entityHeight;
    }

    // precondition : none
    // postcondition: sets the dimensions of an entity and updates the hitbox
    public void setDimensions(double width, double height)
    {
        this.entityWidth = width;
        this.entityHeight = height;

        int[] hitboxXPt = new int[HITBOX_X_POINTS.length];
        int[] hitboxYPt = new int[HITBOX_Y_POINTS.length];
        for(int i=0; i<HITBOX_X_POINTS.length; i++)
        {
            hitboxXPt[i] = (int)(entityWidth  * HITBOX_X_POINTS[i]);
            hitboxYPt[i] = (int)(entityHeight * HITBOX_Y_POINTS[i]);
        }
        this.hitBox = new HitBox(hitboxXPt, hitboxYPt, this);
    }

    // precondition : none
    // postcondition: returns the angle in radians counterclockwise from vertical
    public double getAngle()
    {
        return this.angle;
    }

    // precondition : none
    // postcondition: returns hitbox
    public HitBox getHitBox()
    {
        return this.hitBox;
    }

    // precondition : none
    // postcondition: returns the current health of the enttiy
    public double getCurrentHealth()
    {
        return this.currentHealth;
    }

    // precondition : none
    // postcondition: sets the angle of the entity
    public void setAngle(double theta)
    {
        angle = checkAngle(theta);
    }

    // precondition : none
    // postcondition: returns the angle in radians between 0 and 2pi
    public static double checkAngle( double angle)
    {
        while(angle < 0)
        {
            angle += 2 * Math.PI;
        }
        if(angle >= 2*Math.PI)
        {
            angle %= (2*Math.PI);
        }
        return angle;
    }

    // precondition : none
    // postcondition: paints the entity rotated by angle theta
    public void paintComponent(Graphics g)
    {
        // rotates Graphics g 'angle' radians around center of entity
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(angle, Driver.scale*(this.getScreenX()+this.entityWidth/2), 
            Driver.scale*(this.getScreenY()+this.entityHeight/2));
        // the coordinates ^ are the screen coordinates the graphics object rotates around

        // paints the component vertically with respect to the rotated Graphics frame
        paintComponentVertical(g2);

        // unrotates Graphics g 'angle' radians around center of entity
        g2.rotate(-angle, Driver.scale*(this.getScreenX()+this.entityWidth/2), 
            Driver.scale*(this.getScreenY()+this.entityHeight/2));

        paintHealthBar(g);
    }

    // precondition : none
    // postcondition: paints the entity vertically with respect to g
    public void paintComponentVertical(Graphics g)
    {
        double[] location = getScreenLocation();
        g.drawImage(getImage().getImage(), (int)(Driver.scale*location[0]), (int)(Driver.scale*location[1]),
            (int)(Driver.scale*entityWidth), (int)(Driver.scale*entityHeight), this);

        // troubleshooting:
        // printDrawCall();
    }

    // precondition : none
    // postcondition: paints the healthbar of the entity
    public void paintHealthBar(Graphics g)
    {
        double[] location = getScreenLocation();
        double topLeftX = Driver.scale*(location[0] + entityWidth/2 - healthBarLength/2); 
        double topLeftY = Driver.scale*(location[1] - healthBarHeight - additionalSpaceForHealth);
        g.setColor(Color.RED);
        g.fillRect((int)topLeftX, (int)topLeftY, (int)(Driver.scale*healthBarLength), (int)(Driver.scale*healthBarHeight));
        g.setColor(Color.GREEN);
        double fractionHealth = currentHealth / maxHealth;
        g.fillRect((int)topLeftX, (int)topLeftY, (int)(Driver.scale*fractionHealth*healthBarLength), (int)(Driver.scale*healthBarHeight));
    }

    // precondition : none
    // postcondition: recalculates the location on screen based on camera
    public void recalculateRelativeScreenLocation(Camera c)
    {
        double relativeXLocation = Driver.defaultWidth/2 + (getXPos() - c.getXPos() - this.entityWidth/2);
        double relativeYLocation = Driver.defaultHeight/2 + (getYPos() - c.getYPos() - this.entityHeight/2);
        // subtract width/2 and height/2 because it prints it at top left location
        double [] location = new double[] {relativeXLocation, relativeYLocation};
        setScreenLocation(location);
    }

    // precondition : none
    // postcondition: returns string with width and height
    public String toString()
    {
        return super.toString() + ", width="+entityWidth+", height="+entityHeight;
    }

    // precondition : none
    // postcondition: checks if this's hitbox collides with other's hitbox
    public boolean checkCollision(Entity other)
    {
        return this.hitBox.checkCollision(other.hitBox);
    }

    // precondition : none
    // postcondition: checks if this's hitbox collides with bullet's hitbox
    public boolean checkCollision(Bullet bullet)
    {
        return this.hitBox.checkCollision(bullet.getHitBox());
    }

    // precondition : time is in millisecond
    // postcondition: causes the entity to take damage and returns true if entity dies
    public boolean takeDamage(Entity other, long time)  //returns true if dead
    {
        this.currentHealth -= other.damage*time;
        if(this.currentHealth <= 0)
        {
            playSound(deathSound);
            return true;
        }
        return false; 
    }

    // precondition : none
    // postcondition: causes entity to take damage and returns true if entity dies
    public boolean takeBulletDamage(double damage)
    {
        this.currentHealth -= damage;
        if(this.currentHealth <= 0)
        {
            playSound(deathSound);
            return true;
        }
        else
        {
            playSound(hurtSound);
            return false;
        }
    }

    // precondition : file contains audio file in format .wav
    // postcondition: plays file
    public static void playSound(File file)
    {
        try
        {
            AudioInputStream sound = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(sound);
            clip.start();
        }
        catch(Exception e)
        {
            System.out.println("playSound(File) error");
        }
    }

    // precondition : none
    // postcondition: prints method call of drawImage()
    public void printDrawCall()
    {
        double[] location = getScreenLocation();
        System.out.println("g.drawImage(image, "+location[0]+", "+location[1]+", "+entityWidth+", "+entityHeight+");");
    }
}