// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// LevelPortal - returns the next level the player should go to

public abstract class LevelPortal
{
    private int nextLevel;

    // precondition : nextLevel is between 0 and TOTAL_NUM_OF_LEVELS or -1
    // postcondition: creates a level portal with the next level
    public LevelPortal(int nextLevel)
    {
        this.nextLevel = nextLevel;
    }

    // precondition : none
    // postcondition: checks if there is a collision with the entity
    public abstract boolean checkCollide(Entity e);

    // precondition : nones
    // postcondition: returns the index of the next level the player should go to 
    public int getNextLevel()
    {
        return nextLevel;
    }

    // precondition : none
    // postcondition: returns a string with its next level
    public String toString()
    {
        return "LevelPortal: nextLevel="+nextLevel;
    }

    // precondition : none
    // postcondition: returns the next level spawn location, if any
    public abstract double[] getNextSpawnLocation();
}