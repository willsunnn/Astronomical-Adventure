// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Player - creates a player ship that can shoot

import java.util.*;
import javax.swing.*;
import java.awt.*;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.applet.*;

public class Player extends Entity
{
    public static final int PLAYER_HEALTH = 100;
    public static final double PLAYER_WIDTH = 30.0;
    public static final double PLAYER_HEIGHT = 60.0;

    public static final double DEFAULT_TURRET_WIDTH_RATIO = 0.4;
    public static final double DEFAULT_CROSSHAIR_PIXEL_WIDTH = 10.0;

    public static final double DEFAULT_BULLET_WIDTH = 5.0;
    public static final double DEFAULT_BULLET_HEIGHT = DEFAULT_BULLET_WIDTH * 5.0;
    public static final Color DEFAULT_BULLET_MAPCOLOR = Color.BLACK;
    public static final double DEFAULT_BULLET_VELOCITY = 1000;   //travels 100 pixels per s
    public static final double PLAYER_BULLET_DESPAWN_DISTANCE = 1500;

    public static final double DEFAULT_PLAYER_DAMAGE = 100.0 / 1000.0;  //deals 100 damage per second

    public static final double rocketAcceleration = 200000 / 1000.0;    // accelerates 200000 p/s / 1000millis    //this is in pixels/ms^2
    public static final double rotationalVelocity = 2*Math.PI / 1000.0;     // rotates 2 pi radian / 1000millis      //this is in radians/ms

    public static final File shootSound = new File("resources/sounds/shoot.wav");
    public static final File playerHurtSound = new File("resources/sounds/entity_hurt.wav");
    public static final File playerDeathSound = new File("resources/sounds/entity_death.wav");

    private double mouseXPos;       //position of mouse in frame
    private double mouseYPos;
    private double mouseAngle;

    private boolean wPress;
    private boolean aPress;
    private boolean sPress;
    private boolean dPress;

    private ImageIcon turretBaseIcon;
    private ImageIcon turretTopIcon;
    private ImageIcon crosshairIcon;

    // precondition : variables are defined as in FileReader
    // postcondition: creates a player with those variables
    public Player(double xPos, double yPos, double xV, double yV, double width, double height, Color mapCol)
    {
        super(xPos, yPos, xV, yV, width, height, PLAYER_HEALTH, "player.gif", mapCol, DEFAULT_PLAYER_DAMAGE, playerHurtSound, playerDeathSound);
        wPress = aPress = sPress = dPress = false;
        mouseXPos = mouseYPos = 0;
        setAngle(0);

        turretBaseIcon = new ImageIcon("resources/actor_gifs/entity_gifs/turret_base.gif");
        turretTopIcon = new ImageIcon("resources/actor_gifs/entity_gifs/turret_top.gif");
        crosshairIcon = new ImageIcon("resources/actor_gifs/entity_gifs/crosshair.gif");
    }

    // precondition : none
    // postcondition: creates a bullet based on direction of cursor
    public Bullet shoot()
    {
        playSound(shootSound);
        Bullet bullet = new Bullet(getXPos(), getYPos(), DEFAULT_BULLET_VELOCITY, mouseAngle, 
                DEFAULT_BULLET_WIDTH, DEFAULT_BULLET_HEIGHT, "player",  DEFAULT_BULLET_MAPCOLOR);
        return bullet;
    }

    // precondition : none
    // postcondition: gets the mouse location based on camera and mouse motion listener
    public void updateMouseLocation(int x, int y, Camera c)
    {
        mouseXPos = x;
        mouseYPos = y;

        double mouseFrameX = (mouseXPos/Driver.scale - Driver.defaultWidth/2) + c.getXPos() ;     // position of mouse in level
        double mouseFrameY = (mouseYPos/Driver.scale - Driver.defaultHeight/2) + c.getYPos();     // 
        double dY = mouseFrameY - this.getYPos();
        double dX = mouseFrameX - this.getXPos();

        mouseAngle = calculateAngle( dX, dY);
    }

    // precondition : none
    // postcondition: sets the key presses to influence movement
    public void setPress(char key, boolean press)
    {
        if(key == 'w')
        {
            wPress = press;
        }
        else if(key == 'a')
        {
            aPress = press;
        }
        else if(key == 's')
        {
            sPress = press;
        }
        else if(key == 'd')
        {
            dPress = press;
        }
    }

    // precondition : variables are defined in level
    // postcondition: the player is updated to the spawn location of each level
    public void setVariables(double xPos, double yPos, double xV, double yV, double width, double height, Color col)
    {
        setPosition(xPos,yPos);
        setVelocity(xV, yV);
        setDimensions(width, height);
        setMapColor(col);
    }

    // precondition : none
    // postcondition: recalculates acceleration based on planets and based on key presses
    public void recalculateAcceleration(ArrayList<Planet> p)
    {
        super.recalculateAcceleration(p);

        if(wPress)
        {
            double xComponent = rocketAcceleration * Math.cos(Math.PI/2 - getAngle());
            double yComponent = - rocketAcceleration * Math.sin(Math.PI/2 - getAngle());   //negative because up is negative y
            double[] acc = new double[] {xComponent, yComponent};
            changeAcceleration(acc);
        }
    }

    // precondition : time is in milliseconds since last calculated
    // postcondition: velocity is updated and orientation is updated
    public void recalculateVelocity(long time)
    {
        super.recalculateVelocity(time);

        // calculates Rocket Angle
        if(aPress)
        {
            rotate( - rotationalVelocity * time);
        }
        if(dPress)
        {
            rotate(   rotationalVelocity * time);
        }
    }

    // precondition : none
    // postcondition: paints component of the rocket ship after rotation
    public void paintComponent(Graphics g)
    {
        // paints the player
        super.paintComponent(g);

        // paints the 
        paintCrossHairs(g);
    }

    // precondition : none
    // postcondition: paints rocket ship with added turret
    public void paintComponentVertical(Graphics g)
    {
        super.paintComponentVertical(g);
        paintTurretBase(g);
        paintTurretTop(g);
    }

    // precondition : none
    // postcondition: paints the turret base
    public void paintTurretBase(Graphics g)
    {
        double turretWidth = DEFAULT_TURRET_WIDTH_RATIO * getEntityWidth();
        double[] playerScreenLoc = getScreenLocation(); 
        double turretX = playerScreenLoc[0] + getEntityWidth()/2 - turretWidth/2;
        double turretY = playerScreenLoc[1] + getEntityHeight()/2 - turretWidth/2;
        g.drawImage(turretBaseIcon.getImage(), (int)(Driver.scale*turretX), (int)(Driver.scale*turretY),
            (int)(Driver.scale*turretWidth), (int)(Driver.scale*turretWidth), this);
    }

    // precondition : none
    // postcondition: paints the turret top
    public void paintTurretTop(Graphics g)
    {
        double turretWidth = 3*DEFAULT_TURRET_WIDTH_RATIO * getEntityWidth();
        // the turret_top.gif is 3 times the size of turret_base.gif
        double[] playerScreenLoc = getScreenLocation(); 
        double turretX = playerScreenLoc[0] + getEntityWidth()/2 - turretWidth/2;
        double turretY = playerScreenLoc[1] + getEntityHeight()/2 - turretWidth/2;
        double rotateAngle = mouseAngle - getAngle();

        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(rotateAngle, Driver.scale*(turretX+turretWidth/2), 
            Driver.scale*(turretY+turretWidth/2));

        g.drawImage(turretTopIcon.getImage(), (int)(Driver.scale*turretX), (int)(Driver.scale*turretY),
            (int)(Driver.scale*turretWidth), (int)(Driver.scale*turretWidth), this);

        g2.rotate(-rotateAngle, Driver.scale*(turretX+turretWidth/2), 
            Driver.scale*(turretY+turretWidth/2));
    }

    // precondition : none
    // postcondition: paints the crosshairs that follows mouse
    public void paintCrossHairs(Graphics g)
    {
        double imgWidth = DEFAULT_CROSSHAIR_PIXEL_WIDTH;
        g.drawImage(crosshairIcon.getImage(), (int)(mouseXPos-imgWidth/2), (int)(mouseYPos-imgWidth/2),
            (int)(Driver.scale*imgWidth), (int)(Driver.scale*imgWidth), this);
    }
}