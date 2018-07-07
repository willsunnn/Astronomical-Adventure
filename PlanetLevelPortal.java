// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// PlanetLevelPortal - creates a portal for the next level when the player collides with planet

public class PlanetLevelPortal extends LevelPortal
{
    private double additionalCollisionRadius;
    private Planet planet;

    // precondition : radius >= 0 and levelNum is between 0 and TOTAL_NUM_OF_LEVELS or -1
    // postcondition: creates a level portal at the planet
    public PlanetLevelPortal(int levelNum, Planet p, double radius)
    {
        super(levelNum);
        this.planet = p;
        this.additionalCollisionRadius = radius;
    }

    // precondition : none
    // postcondition: checks if entity collides with level portal
    public boolean checkCollide(Entity e)
    {
        double dx = e.getXPos() - planet.getXPos();
        double dy = e.getYPos() - planet.getYPos();
        double distance = Math.pow( Math.pow(dx,2) + Math.pow(dy,2) ,0.5 );
        return distance <= planet.getRadius() + additionalCollisionRadius;
    }

    // precondition : none
    // postcondition: returns the string with planet
    public String toString()
    {
        return super.toString() + ", Planet: " + planet.toString();
    }

    // precondition : none
    // postcondition: returns 0, 0 because default spawn is used when entering a planet
    public double[] getNextSpawnLocation()
    {
        double x = 0;//planet.getXPos() + planet.getRadius() + additionalCollisionRadius;
        double y = 0;//planet.getYPos() + planet.getRadius() + additionalCollisionRadius;
        return new double[] {x,y};
    }
}