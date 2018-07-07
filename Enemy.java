// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Enemy - creates a default enemy

import java.util.*;
import javax.swing.*;
import java.awt.*;

import java.io.File;

public class Enemy extends Entity
{
    public static final double ENEMY_HEALTH = 20;
    public static final double DEFAULT_ENEMY_DAMAGE = 20/1000.0;    //deals 20 damage per second

    public static final File enemyHurtSound = new File("resources/sounds/entity_hurt.wav");
    public static final File enemyDeathSound = new File("resources/sounds/entity_death.wav");

    // precondition : variables defined in FileReader
    // postcondition: creates an enemy with those variables
    public Enemy(double xPos, double yPos, double xV, double yV, double width, double height, Color mapCol)
    {
        super(xPos, yPos, xV, yV, width, height, ENEMY_HEALTH, "enemy.gif", mapCol, DEFAULT_ENEMY_DAMAGE, enemyHurtSound, enemyDeathSound);
    }

    // precondition : none
    // postcondition: none
    public void shoot(Camera c)
    {}

    // precondition : time is in milliseconds since last calculated
    // postcondition: recalculates the velocity and changes the angle of the ship based on that velocity
    public void recalculateVelocity (long time)
    {
        super.recalculateVelocity(time);
        setAngle(calculateAngle(getXV(), getYV()));
    }
}