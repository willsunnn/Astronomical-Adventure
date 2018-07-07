// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Level - creates a level that contains all actors and draws the background and grid

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Level extends JPanel
{
    public final static int spaceBetweenGrid = 150;
    public final static int gridLineWidth = 1;

    private int levelWidth;
    private int levelHeight;
    private int levelNum;

    private ArrayList<LevelPortal> levelPortals;

    private ArrayList<Planet> planets;
    private ArrayList<Entity> enemies;
    private Player player;
    private Camera camera;

    private ArrayList<Bullet> playerBullets;
    private ArrayList<Bullet> enemyBullets;

    private Map map;

    private ImageIcon backgroundImage;

    private double defaultSpawnX;
    private double defaultSpawnY;
    private double defaultXVelocity;
    private double defaultYVelocity;
    private double defaultWidth;
    private double defaultHeight;
    private Color defaultPlayerMapColor;

    private double nextLevelX;
    private double nextLevelY;

    /* precondition : all actors, levelPortals are initialized. 
     * playerSpawn defines the characteristics of the player at the start of the level. 
     * playerMapCol defines the color of the player on the map. 
     * levelNum is between 0 and TOTAL_NUM_OF_LEVELS as defined in FileReader
     * levelWidth and levelHeight are not negative
     */
    // postcondition: creates the level
    public Level(Player p, ArrayList<Planet> planets, ArrayList<Entity> enemies, ArrayList<LevelPortal> levelPortals, double[] playerSpawn, Color playerMapCol, int levelNum, int levelWidth, int levelHeight)
    {
        this.player = p;

        this.planets = planets;
        this.enemies = enemies;
        this.levelPortals = levelPortals;

        this.backgroundImage = new ImageIcon("resources/background_gifs/level"+levelNum+".gif");
        this.camera = new Camera(levelWidth, levelHeight);

        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
        this.levelNum = levelNum;

        this.playerBullets = new ArrayList<Bullet>();
        this.enemyBullets = new ArrayList<Bullet>();

        this.map = new Map(levelWidth, levelHeight, planets, enemies, player, camera);

        defaultSpawnX = playerSpawn[0];
        defaultSpawnY= playerSpawn[1];
        defaultXVelocity = playerSpawn[2];
        defaultYVelocity = playerSpawn[3];
        defaultWidth = playerSpawn[4];
        defaultHeight = playerSpawn[5];
        defaultPlayerMapColor = playerMapCol;
    }

    /* precondition : changeLocation determines whether or not the player should spawn in a spot other than default spawn
     * if changeLocation is true, set playerLocation to (xSpawn, ySpawn)
     */
    // postcondition: runs the level and returns the index of the next level, return -1 if there are no more levels
    public int play(boolean changeLocation, double xSpawn, double ySpawn)
    {
        player.setVariables(defaultSpawnX, defaultSpawnY, defaultXVelocity, defaultYVelocity, defaultWidth, defaultHeight, defaultPlayerMapColor);
        if(changeLocation)
        {
            player.setPosition(xSpawn, ySpawn);
        }
        this.playerBullets = new ArrayList<Bullet>();
        this.enemyBullets = new ArrayList<Bullet>();

        boolean done = false;
        long lastCalculatedTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();

        while(!done)
        {
            currentTime = System.currentTimeMillis();
            long difference = currentTime - lastCalculatedTime;
            if(difference >= Driver.time)
            {
                recalculateAccelerations();
                recalculateVelocities(difference);
                recalculatePositions(difference);
                recalculateHitBoxes();

                checkEntityBulletCollisions();
                checkPlayerEntityCollisions(difference);       //time is passed for damage purposes
                checkBulletDespawn();

                // checks if player should go to next level
                for(LevelPortal lp: levelPortals)
                {
                    if(lp.checkCollide(player))
                    {
                        nextLevelX = lp.getNextSpawnLocation()[0];
                        nextLevelY = lp.getNextSpawnLocation()[1];
                        return lp.getNextLevel();
                    }
                }
                if(! alive(player))
                {
                    return -1;
                }

                recalculateScreenLocations();
                repaint();
                //printPositions();

                //reset clock counter
                lastCalculatedTime = System.currentTimeMillis();
            }
        }
        repaint();
        return -1;      //this should return the next level to go to. returns -1 if there are no more levels
    }

    // precondition : none
    // postcondition: recalculates accelerations of all actors influenced by gravity
    public void recalculateAccelerations()
    {
        for(Planet p: planets)
        {
            p.recalculateAcceleration(planets);
        }
        for(Entity e: enemies)
        {
            e.recalculateAcceleration(planets);
        }
        player.recalculateAcceleration(planets);
    }

    // precondition : difference is time in milliseconds since last recalulated
    // postcondition: recalculates velocities of all accelerating entities
    public void recalculateVelocities(long difference)
    {
        for(Planet p: planets)
        {
            p.recalculateVelocity(difference);
        }
        for(Entity e: enemies)
        {
            e.recalculateVelocity(difference);
        }
        player.recalculateVelocity(difference);
    }

    // precondition : difference is time in milliseconds since last recalculated
    // postcondition: recalculates position of all moving entities and updates location of the camera based on player
    public void recalculatePositions(long difference)
    {
        for(Planet p: planets)
        {
            p.recalculatePosition(difference);
        }
        for(Entity e: enemies)
        {
            e.recalculatePosition(difference);
        }
        for(Bullet b: playerBullets)
        {
            b.recalculatePosition(difference);
        }
        for(Bullet b: enemyBullets)
        {
            b.recalculatePosition(difference);
        }
        player.recalculatePosition(difference);
        camera.updateLocation(player);
    }

    // precondition : locations has beeen updated 
    // postcondition: updates the hitboxes of all collisionable actors
    public void recalculateHitBoxes()
    {
        for(int i=0; i<enemies.size(); i++)
        {
            enemies.get(i).getHitBox().updateHitBox();
        }
        for(int i=0; i<playerBullets.size(); i++)
        {
            playerBullets.get(i).getHitBox().updateHitBox();
        }
        for(int i=0; i<enemyBullets.size(); i++)
        {
            enemyBullets.get(i).getHitBox().updateHitBox();
        }
        player.getHitBox().updateHitBox();
    }

    // precondition : locations has been updated
    // postcondition: recalculates locations of actors relative to camera
    public void recalculateScreenLocations()
    {
        for(Planet p: planets)
        {
            p.recalculateRelativeScreenLocation(camera);
        }
        for(Entity e: enemies)
        {
            e.recalculateRelativeScreenLocation(camera);
        }
        for(Bullet b: playerBullets)
        {
            b.recalculateRelativeScreenLocation(camera);
        }
        for(Bullet b: playerBullets)
        {
            b.recalculateRelativeScreenLocation(camera);
        }
        player.recalculateRelativeScreenLocation(camera);
    }

    // precondition : none
    // postcondition: checks collisions between entities and opposing faction bullets
    public void checkEntityBulletCollisions()
    {
        for(int i = playerBullets.size()-1; i>= 0; i--)
        {
            for(int j = enemies.size()-1; j>=0; j--)
            {
                try
                {
                    if(enemies.get(j).checkCollision(playerBullets.get(i)))
                    {
                        if(enemies.get(j).takeBulletDamage(playerBullets.get(i).getDamage()))
                        {
                            enemies.remove(j);
                        }
                        playerBullets.remove(i);
                    }
                }
                catch(IndexOutOfBoundsException e)
                {}   
            }
        }
        for(int i = enemyBullets.size()-1; i>= 0; i--)
        {
            if(player.checkCollision(playerBullets.get(i)))
            {
                player.takeBulletDamage(enemyBullets.get(i).getDamage());
                enemyBullets.remove(i);
            }
        }
    }

    // precondition : htiboxes has been updated
    // postcondition: checks collisions between player and entities
    public void checkPlayerEntityCollisions(long difference)
    {
        for(Entity e: enemies)
        {
            if(player.checkCollision(e))
            {
                player.takeDamage(e,difference);
                e.takeDamage(player,difference);
            }
        }
    }

    // precondition : none
    // postcondition: removes bullets after they have travelled too far
    public void checkBulletDespawn()
    {
        for(int i = playerBullets.size()-1; i>= 0; i--)
        {
            Bullet b = playerBullets.get(i);
            if(b.checkDespawn())
            {
                playerBullets.remove(i);
            }
        }
        for(int i = enemyBullets.size()-1; i>= 0; i--)
        {
            Bullet b = enemyBullets.get(i);
            if(b.checkDespawn())
            {
                playerBullets.remove(i);
            }
        }
    }

    // precondition : none
    // postcondition: checks if entity is inBounds
    public boolean inBounds(Entity e)
    {
        if(Math.abs(e.getXPos()) > levelWidth / 2 || Math.abs(e.getYPos()) > levelHeight / 2)
        {
            return false;
        }
        return true;
    }

    // precondition : none
    // postcondition: checks if entity is alive
    public boolean alive(Entity e)
    {
        return e.getCurrentHealth() > 0;
    }

    // precondition : player has been initialized
    // postcondition: returns player
    public Player getPlayer()
    {
        return player;
    }

    // precondition : camera has been initialized
    // postcondition: returns camera
    public Camera getCamera()
    {
        return camera;
    }

    // precondition : planets has been initialized and 0<=index<planets.size()
    // postcondition: returns planet at index
    public Planet getPlanet(int index)
    {
        return planets.get(index);
    }

    // precondition : nextLevelX contains x location of the next level spawn point
    // postcondition: returns x locations of next level spawn point 
    public double getNextLevelX()
    {
        return this.nextLevelX;
    }

    // precondition : nextLevelY contains y location of the next level spawn point
    // postcondition: returns y locations of next level spawn point 
    public double getNextLevelY()
    {
        return this.nextLevelY;
    }

    // precondition : none
    // postcondition: returns background image
    public ImageIcon getBackgroundImage()
    {
        return backgroundImage;
    }

    // precondition : none
    // postcondition: adds a bullet to the player's bullets
    public void addPlayerBullet(Bullet b)
    {
        this.playerBullets.add(b);
    }

    // precondition : none
    // postcondition: repaints entire frame
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        try
        {
            paintBackground(g);
            paintGrid(g);
            for(Planet p: planets)
            {
                if(p!=null){
                    p.paintComponent(g);}
            }
            for(Entity e: enemies)
            {
                if(e!=null){
                    e.paintComponent(g);}
            }
            for(Bullet b: playerBullets)
            {
                if(b!=null){
                    b.paintComponent(g);}
            }
            for(Bullet b: enemyBullets)
            {
                if(b!=null){
                    b.paintComponent(g);}
            }
            player.paintComponent(g);

            if(Driver.drawHitBoxes)
            {
                for(Entity e:enemies)
                {
                    e.getHitBox().paintComponent(g, camera);
                }
                for(Bullet b: playerBullets)
                {
                    b.getHitBox().paintComponent(g, camera);
                }
                for(Bullet b: enemyBullets)
                {
                    b.getHitBox().paintComponent(g, camera);
                }
                player.getHitBox().paintComponent(g, camera);
            }
            map.paintComponent(g);
        }
        catch(NullPointerException e)   //happens randomly every once in a while, probably when paint doesn't align with recalculation
        {
        }

    }

    // precondition : none
    // postcondition: paints background image
    public void paintBackground(Graphics g)
    {
        g.drawImage(backgroundImage.getImage(), 0, 0, (int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale), this);    
    }

    // precondition : none
    // postcondition: paints grid starting outwards from 0, 0
    public void paintGrid(Graphics g)
    {
        //draw vertical lines
        for(int i=0; i<levelWidth/2; i+=spaceBetweenGrid)
        {
            g.setColor(new Color(70,70,70));
            if((i/spaceBetweenGrid)%5 == 0)
            {
                g.setColor(Color.WHITE);
            }

            //draws lines to the right of center
            g.fillRect((int)(Driver.scale*(i-camera.getXPos()+Driver.defaultWidth/2)),0, gridLineWidth, (int)(Driver.scale*Driver.defaultHeight));

            //draws lines to the left of center
            g.fillRect((int)(Driver.scale*(-i-camera.getXPos()+Driver.defaultWidth/2)),0, gridLineWidth, (int)(Driver.scale*Driver.defaultHeight));
        }

        //draw horizontal lines
        for(int j=0; j<levelHeight/2; j+=spaceBetweenGrid)
        {
            g.setColor(new Color(70,70,70));
            if((j/spaceBetweenGrid)%5 == 0)
            {
                g.setColor(Color.WHITE);
            }

            //draws lines above center
            g.fillRect(0,(int)(Driver.scale*(j-camera.getYPos()+Driver.defaultHeight/2)), (int)(Driver.scale*Driver.defaultWidth), gridLineWidth);

            //draws lines below center
            g.fillRect(0,(int)(Driver.scale*(-j-camera.getYPos()+Driver.defaultHeight/2)), (int)(Driver.scale*Driver.defaultWidth), gridLineWidth);
        }
    }
}