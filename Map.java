// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Map - creates a map to be displayed in the upper left hand corner

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Map extends JPanel
{
    public final int defaultMapHeight = 160;     //in pixels
    public final int defaultTopLeftX  = 25;
    public final int defaultTopLeftY  = 25;
    public final int roundCornerRadius = 5;
    public final Color defaultMapBackgroundColor = new Color(255,255,255);
    public final Color defaultMapBorderColor = Color.YELLOW;
    public final static double DEFAULT_ENTITY_EXPAND_SCALE = 10.0;

    public final double BOSS_ICON_WIDTH = 10;
    public final double MIN_PLANET_DIAMETER = 5;     //the smallest a planet will be displayed
    public final double MIN_PLAYER_AREA   = 20;

    public final double playerIconScale = 3;

    private int topLeftX;
    private int topLeftY;

    private int mapWidth;
    private int mapHeight;       //in pixels

    private int levelWidth;                  //in pixels
    private int levelHeight;

    private double scale;           //ratio of levelHeight/mapHeight

    private ArrayList<Planet> planets;
    private ArrayList<Entity> enemies;
    private Player player;
    private Camera camera;

    // precondition : levelWidth and levelHeight are dimensions of the level. All actors reflect the actors in level
    // postcondition: creates a map with all actors to be shown on the map
    public Map(int levelWidth, int levelHeight, ArrayList<Planet> planets, ArrayList<Entity> enemies, Player player, Camera camera)
    {
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        double levelSizeRatio = (double)(levelWidth) / levelHeight;

        this.mapHeight = defaultMapHeight;
        this.mapWidth = (int)(mapHeight * levelSizeRatio);

        this.scale = ((double)(mapHeight)) / levelHeight;

        this.topLeftX = defaultTopLeftX;
        this.topLeftY = defaultTopLeftY;

        this.planets = planets;
        this.enemies = enemies;
        this.player = player;
        this.camera = camera;
    }

    // precondition : none
    // postcondition: draws a border, background, and actors
    public void paintComponent(Graphics g)
    {
        paintBorder(g);
        paintPlanets(g);
        paintEnemies(g);
        paintEntity(g, player);
        paintCameraBox(g);
    }

    // precondition : none
    // postcondition: draws a border around the map
    public void paintBorder(Graphics g)
    {
        g.setColor(defaultMapBorderColor);
        g.fillRoundRect((int)(Driver.scale*(topLeftX-roundCornerRadius)), (int)(Driver.scale*(topLeftY-roundCornerRadius)), 
            (int)(Driver.scale*(mapWidth+2*roundCornerRadius)), (int)(Driver.scale*(mapHeight+2*roundCornerRadius)), 
            (int)(Driver.scale*roundCornerRadius), (int)(Driver.scale*roundCornerRadius));

        g.setColor(defaultMapBackgroundColor);
        g.fillRect((int)(Driver.scale*topLeftX), (int)(Driver.scale*topLeftY), 
            (int)(Driver.scale*mapWidth), (int)(Driver.scale*mapHeight));
    }

    // precondition : none
    // postcondition: paints all the planets
    public void paintPlanets(Graphics g)
    {
        for(Planet p: planets)
        {
            if(inBounds(p))
            {
                paintPlanet(g, p);
            }
        }
    }

    // precondition : none
    // postcondition: paints a planet with color defined in planet
    public void paintPlanet(Graphics g, Planet p)
    {
        g.setColor(p.getMapColor());
        double xLoc = topLeftX + mapWidth/2 + (p.getXPos()-p.getRadius())* scale;
        double yLoc = topLeftY + mapHeight/2 + (p.getYPos()-p.getRadius()) * scale;
        double diameter = 2 * p.getRadius() * scale;
        if(diameter < MIN_PLANET_DIAMETER)
        {
            diameter = MIN_PLANET_DIAMETER;
        }
        g.fillOval( (int)(Driver.scale*xLoc), (int)(Driver.scale*yLoc), (int)(Driver.scale*diameter), (int)(Driver.scale*diameter));
    }

    // precondition : none
    // postcondition: paints all entities
    public void paintEnemies(Graphics g)
    {
        for(Entity e: enemies)
        {
            if(inBounds(e))
            {
                if(e instanceof Enemy)
                {
                    paintEntity(g, e);
                }
                else if(e instanceof Boss)
                {
                    paintBoss(g, (Boss)e);
                }
            }
        }
    }

    // precondition : none
    // postcondition: checks if actor is inbounds
    public boolean inBounds(Actor a)
    {
        boolean inX = Math.abs(a.getXPos()) < levelWidth/2;
        boolean inY = Math.abs(a.getYPos()) < levelHeight/2;
        return inX && inY;
    }

    // precondition : none
    // postcondition: paints an entity as a triangle
    public void paintEntity(Graphics g, Entity e)
    {
        Polygon triangle = constructEntityTriangle(e);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(e.getMapColor());

        g2.rotate(e.getAngle(), 
            (int)(Driver.scale*(topLeftX + mapWidth/2  + scale * e.getXPos() )),
            (int)(Driver.scale*(topLeftY + mapHeight/2 + scale * e.getYPos() )));
        // the coordinates ^ are the screen coordinates the graphics object rotates around

        // paints the component vertically with respect to the rotated Graphics frame
        g.fillPolygon(triangle);

        // unrotates Graphics g 'angle' radians around center of entity
        g2.rotate( - e.getAngle(), 
            (int)(Driver.scale*(topLeftX + mapWidth/2  + scale * e.getXPos() )),
            (int)(Driver.scale*(topLeftY + mapHeight/2 + scale * e.getYPos() )));
    }

    // precondition : none
    // postcondition: checks if entity icon on map is too small
    public boolean isEntityTooSmall(Entity e)
    {
        double expandScale = 1.0;
        if(e instanceof Player) //if e is player, it will make the icon bigger
        {
            expandScale *= playerIconScale;
        }

        double entityMapWidth = scale * expandScale * e.getEntityWidth();
        double entityMapHeight = scale * expandScale * e.getEntityHeight();
        double area = 0.5 * entityMapWidth * entityMapHeight;

        return area<MIN_PLAYER_AREA ;
    }

    // precondition : none
    // postcondition: returns the factor the entity icon should be scaled up if it is too small
    public double getExpandScale(Entity e)
    {
        double expandScale = 1.0;
        if(e instanceof Player) //if e is player, it will make the icon bigger
        {
            expandScale *= playerIconScale;
        }

        double entityMapWidth = scale * expandScale * e.getEntityWidth();
        double entityMapHeight = scale * expandScale * e.getEntityHeight();
        double area = 0.5 * entityMapWidth * entityMapHeight;

        return Math.pow(MIN_PLAYER_AREA / area, 0.5);
    }

    // precondition : none
    // postcondition: paints the boss with a custom boss icon
    public void paintBoss(Graphics g, Boss b)
    {
        double x = Driver.scale*(topLeftX + mapWidth/2   + scale * b.getXPos() - BOSS_ICON_WIDTH/2 );
        double y = Driver.scale*(topLeftY + mapHeight/2  + scale * b.getYPos() - BOSS_ICON_WIDTH/2 );

        g.drawImage(b.getMapIcon().getImage(), (int)x, (int)y, (int)BOSS_ICON_WIDTH, (int)BOSS_ICON_WIDTH, this);
        g.setColor(Color.RED);
    }

    // precondition : none
    // postcondition: creates a polygon that defines how the entity should be painted on map
    public Polygon constructEntityTriangle(Entity e)
    {
        double expandScale = 1.0;
        if(e instanceof Player) //if e is player, it will make the icon bigger
        {
            expandScale *= playerIconScale;
        }
        if(isEntityTooSmall(e))
        {
            expandScale *= getExpandScale(e);
        }

        int[] xCoord = new int[] {
                (int)(Driver.scale*(topLeftX + mapWidth/2  + scale * e.getXPos() )),
                (int)(Driver.scale*(topLeftX + mapWidth/2  + scale * (e.getXPos() - expandScale * e.getEntityWidth()/2))),
                (int)(Driver.scale*(topLeftX + mapWidth/2  + scale * (e.getXPos() + expandScale * e.getEntityWidth()/2)))};

        int[] yCoord = new int[] {
                (int)(Driver.scale*(topLeftY + mapHeight/2 + scale * (e.getYPos() - expandScale * e.getEntityHeight()/2))),
                (int)(Driver.scale*(topLeftY + mapHeight/2 + scale * (e.getYPos() + expandScale * e.getEntityHeight()/2))),
                (int)(Driver.scale*(topLeftY + mapHeight/2 + scale * (e.getYPos() + expandScale * e.getEntityHeight()/2)))};

        return new Polygon( xCoord, yCoord, xCoord.length);
    }

    // precondition : camera has been initialized
    // postcondition: draws a red box around the area the camera displays on the map
    public void paintCameraBox(Graphics g)
    {
        g.setColor(Color.RED);
        double x = defaultTopLeftX + mapWidth/2 + scale * (camera.getXPos() - Driver.defaultWidth/2);
        double y = defaultTopLeftY + mapHeight/2 + scale * (camera.getYPos() - Driver.defaultHeight/2);
        g.drawRect((int)(Driver.scale*x), (int)(Driver.scale*y), (int)(scale * Driver.scale*Driver.defaultWidth), (int)(scale * Driver.scale*Driver.defaultHeight));
    }
}