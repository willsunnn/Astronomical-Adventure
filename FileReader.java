// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// FileReader - creates a class that reads text files and initializes levels from them

import java.util.*;
import java.io.*;
import java.awt.Color;

public class FileReader
{
    public final static int TOTAL_NUM_OF_LEVELS = 5;

    private ArrayList<Level> levels;
    private Player player;

    // precondition : none
    // postcondition: creates levels
    public FileReader(Player player)
    {
        this.player = player;
        analyzeLevelFiles();
    }

    // precondition : player has been initialized
    // postcondition: reads the text files and passes them to createLevel(File, int)
    public void analyzeLevelFiles()
    {
        levels = new ArrayList<Level>();
        for(int i=0; i<TOTAL_NUM_OF_LEVELS; i++)
        {
            String filePath = "resources/levels/level"+i+".txt";
            File file = new File(filePath);
            levels.add(createLevel(file, i));
        }
    }

    // precondition : levelNum is between 0 and NUM_OF_LEVELS as defined in FileReader
    // postcondition: reads the text file and initializes all the entities as defined in the text file, then initializes the level and returns it
    public Level createLevel(File file, int levelNum)
    {

        ArrayList<Planet> planets = new ArrayList<Planet>();
        ArrayList<Entity> enemies = new ArrayList<Entity>();
        ArrayList<LevelPortal> levelPortals = new ArrayList<LevelPortal>();

        int levelWidth = 0;
        int levelHeight = 0;

        Color defaultPlayerMapCol = Color.BLACK;
        double[] defaultPlayerSpawn = new double[]{0};

        try
        {
            Scanner input = new Scanner(file);

            levelWidth = input.nextInt();
            levelHeight = input.nextInt();
            int nextLevel = input.nextInt();
            int nextPlanet = input.nextInt();

            OutsideBoxLevelPortal boundingBox = new OutsideBoxLevelPortal(nextLevel, nextPlanet, levelWidth, levelHeight, this);
            levelPortals.add( boundingBox );

            while(input.hasNext())
            {
                String str = input.nextLine();
                if(str.indexOf("//") != -1) // if there is a / in the string
                {
                    int type = input.nextInt();
                    if(type == 0)           //if it is a player
                    {
                        double xPos = input.nextDouble();
                        double yPos = input.nextDouble();
                        double xV = input.nextDouble();
                        double yV = input.nextDouble();
                        double width = input.nextDouble();
                        double height = input.nextDouble();

                        defaultPlayerMapCol = new Color(input.nextInt(), input.nextInt(), input.nextInt());
                        defaultPlayerSpawn = new double[]{xPos, yPos, xV, yV, width, height};
                    }
                    else if (type == 1)     //if it is an enemy
                    {
                        double xPos = input.nextDouble();
                        double yPos = input.nextDouble();
                        double xV = input.nextDouble();
                        double yV = input.nextDouble();
                        double width = input.nextDouble();
                        double height = input.nextDouble();
                        Color col = new Color(input.nextInt(), input.nextInt(), input.nextInt());

                        enemies.add(new Enemy(xPos, yPos, xV, yV, width, height, col));
                    }
                    else if (type == 2)     //if it is a moving planet
                    {
                        double xPos = input.nextDouble();
                        double yPos = input.nextDouble();
                        double xV = input.nextDouble();
                        double yV = input.nextDouble();
                        int mass = input.nextInt();
                        int radius = input.nextInt();
                        Color col = new Color(input.nextInt(), input.nextInt(), input.nextInt());

                        String line = input.nextLine();
                        String fileName = line.substring(line.indexOf("{")+1,line.indexOf("}"));

                        planets.add(new MovingPlanet(xPos, yPos, xV, yV, mass, radius, fileName, col));
                    }
                    else if (type == 3)     //if it is a stationary planet
                    {
                        double xPos = input.nextDouble();
                        double yPos = input.nextDouble();
                        int mass = input.nextInt();
                        int radius = input.nextInt();
                        Color col = new Color(input.nextInt(), input.nextInt(), input.nextInt());

                        String line = input.nextLine();
                        String fileName = line.substring(line.indexOf("{")+1,line.indexOf("}"));

                        planets.add(new StationaryPlanet(xPos, yPos, mass, radius, fileName, col));
                    }
                    else if (type == 4)     //if it is a PlanetLevelPortal
                    {       //creates a levelPortal for the last planet created
                        int nextLevelNum = input.nextInt();
                        double radius = input.nextDouble();
                        Planet p = planets.get(planets.size()-1);

                        levelPortals.add( new PlanetLevelPortal( nextLevelNum, p, radius));
                    }
                    else if (type == 5)
                    {
                        Planet p = planets.get(planets.size()-1);
                        enemies.add(new Boss(p));
                    }
                }
            }
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }

        //for testing purposes, print out the components of the level
        /*
        for(Planet planet: planets)
        {
        System.out.println(planet);
        }
        for(Entity e: enemies)
        {
        System.out.println(e);
        }
        System.out.println(p);
         */

        return new Level(this.player, planets, enemies, levelPortals, defaultPlayerSpawn, defaultPlayerMapCol, levelNum, levelWidth, levelHeight);
    }

    // precondition : n is between 0 and TOTAL_NUM_OF_LEVELS as defined in FileReader
    // postcondition: returns the level
    public Level getLevel(int n)
    {
        return levels.get(n);
    }
}