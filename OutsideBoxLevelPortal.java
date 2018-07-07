// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// OutsideBoxLevelPortal - creates a level portal outside of the level bounds

public class OutsideBoxLevelPortal extends LevelPortal
{
    private int maxX, maxY;
    private int indexOfNextLevelPlanet;

    private FileReader fileReader;

    // precondition : levelNum is between 0 and TOTAL_NUM_OF_LEVELS
    // postcondition: creates a level portal outside of the level bounds
    public OutsideBoxLevelPortal(int levelNum, int nextPlanet, int levelWidth, int levelHeight, FileReader file)
    {
        super(levelNum);
        indexOfNextLevelPlanet = nextPlanet;
        maxX = levelWidth/2;
        maxY = levelHeight/2;
        this.fileReader = file;
    }

    // precondition : none
    // postcondition: checks if there is a collision with an entity
    public boolean checkCollide(Entity e)
    {
        return (Math.abs(e.getXPos()) >= maxX || Math.abs(e.getYPos()) >= maxY);
    }

    // precondition : none
    // postcondition: returns string defining level portal characterisitcs
    public String toString()
    {
        return super.toString() + " levelPortal outside Level";
    }

    // precondition : none
    // postcondition: returns the next level spawn location in (x, y) format
    public double[] getNextSpawnLocation()
    {
        try
        {
            Planet planet = fileReader.getLevel(getNextLevel()).getPlanet(indexOfNextLevelPlanet);
            double x = planet.getXPos() + planet.getRadius();
            double y = planet.getYPos() + planet.getRadius();
            return new double[] {x,y};
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return new double[]{0,0};
        }
    }
}