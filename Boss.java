// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Boss - Creates a boss entity that moves along the surface of a planet

import java.util.*;
import javax.swing.*;
import java.awt.*;

import java.io.File;

public class Boss extends Entity
{
    public final static double[] HITBOX_X_POINTS = new double[]{0.35, 0.35, -0.35, -0.35};  //expressed as a ratio of width
    public final static double[] HITBOX_Y_POINTS = new double[]{-0.5, 0, 0, -0.5};  //expressed as a ratio of height

    public static final double BOSS_HEALTH = 500;
    public static final double BOSS_DAMAGE = 75 / 1000.0;                   //damage per second
    public static final Color BOSS_COLOR = Color.RED;
    public static final int ROTATIONAL_VELOCITY_CHANGE = 2000;
    public static final double MAX_ROTATIONAL_VELOCITY = Math.PI / 1000.0;     //half revolution per sec
    
    public static final File bossHurtSound = new File("resources/sounds/boss_hurt.wav");
    public static final File bossDeathSound = new File("resources/sounds/boss_death.wav");

    private Planet bossPlanet;
    private long lastRotatedTime;
    private double rotationalVelocity;
    private ImageIcon mapIcon;

    // precondition : planet contains the planet the Boss will be travelling around
    // postcondition: creates a boss entity that moves along the surface of the planet
    public Boss(Planet planet)
    {
        super(planet.getXPos(), planet.getYPos(), 0, 0, 2*planet.getRadius(), 2*planet.getRadius(), BOSS_HEALTH, "boss.gif", BOSS_COLOR, BOSS_DAMAGE, bossHurtSound, bossDeathSound);
        lastRotatedTime = System.currentTimeMillis();
        rotationalVelocity = 0;
        bossPlanet = planet;
        mapIcon = new ImageIcon("resources/actor_gifs/entity_gifs/boss_icon.gif");

        int[] hitboxXPt = new int[HITBOX_X_POINTS.length];
        int[] hitboxYPt = new int[HITBOX_Y_POINTS.length];
        for(int i=0; i<HITBOX_X_POINTS.length; i++)
        {
            hitboxXPt[i] = (int)(getEntityWidth()  * HITBOX_X_POINTS[i]);
            hitboxYPt[i] = (int)(getEntityHeight() * HITBOX_Y_POINTS[i]);
        }
        setHitBox( new HitBox(hitboxXPt, hitboxYPt, this));
    }

    // precondition : angle is properly defined in the Entity class and recalculate screen location in entity class was called
    // postcondition: paints the Boss Entity around the planet
    public void paintComponent(Graphics g)
    {
        // rotates Graphics g 'angle' radians around center of the planet it is on
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(getAngle(), Driver.scale*(this.getScreenX()+this.getEntityWidth()/2), 
            Driver.scale*(this.getScreenY()+this.getEntityHeight()/2));
        // the coordinates ^ are the screen coordinates the graphics object rotates around

        // paints the component vertically with respect to the rotated Graphics frame
        paintComponentVertical(g2);

        // unrotates Graphics g 'angle' radians around center of the planet it is on
        g2.rotate(-getAngle(), Driver.scale*(this.getScreenX()+this.getEntityWidth()/2), 
            Driver.scale*(this.getScreenY()+this.getEntityHeight()/2));

        paintHealthBar(g);
    }

    // precondition : recalculate screen location in entity class was called
    // postcondition: paints the Boss Entity vertically on the frame
    public void paintComponentVertical(Graphics g)
    {
        double[] location = getScreenLocation();
        g.drawImage(getImage().getImage(), (int)(Driver.scale*location[0]), (int)(Driver.scale*location[1]),
            (int)(Driver.scale*getEntityWidth()), (int)(Driver.scale*getEntityHeight()), this);

        // troubleshooting:
        // printDrawCall();
    }

    // precondition : none
    // postcondition: does nothing as boss follows the planet
    public void recalculateAcceleration(ArrayList<Planet> planets)
    {}

    // precondition : none
    // postcondition: Every so often (as defined as the static variable ROTATIONAL_VELOCITY_CHANGE), the boss will change its rotational velocity
    public void recalculateVelocity(long time)
    {
        recalculateRotationalVelocity();
    }

    // precondition : time is in milliseconds since last calculated
    // postcondition: updates the position of the boss based on the position of the planet, and rotates the boss
    public void recalculatePosition(long time)
    {
        rotate(rotationalVelocity * time);

        double angleRespectToHorizontal = Math.PI/2 - getAngle();
        double x = bossPlanet.getXPos() + bossPlanet.getRadius() * Math.cos(angleRespectToHorizontal);
        double y = bossPlanet.getYPos() - bossPlanet.getRadius() * Math.sin(angleRespectToHorizontal);

        setPosition(x, y);
    }

    // precondition : none
    // postcondition: Every so often (as defined as the static variable ROTATIONAL_VELOCITY_CHANGE), the boss will change its rotational velocity
    public void recalculateRotationalVelocity()
    {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastRotatedTime > ROTATIONAL_VELOCITY_CHANGE)
        {
            lastRotatedTime = System.currentTimeMillis();
            rotationalVelocity =  (Math.random() * 2 * MAX_ROTATIONAL_VELOCITY) - MAX_ROTATIONAL_VELOCITY;
        }
    }

    // precondition : none
    // postcondition: returns the Boss's map icon
    public ImageIcon getMapIcon()
    {
        return this.mapIcon;
    }
}