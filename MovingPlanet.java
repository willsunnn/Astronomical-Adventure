// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// MovingPlanet - a Planet that can move

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class MovingPlanet extends Planet
{
    /* precondition : filePath is the ImageIcon, relative to "resources/actor_gifs/planet_gifs/"
     *                mapCol is the color of the planet's icon on the map
     * postcondition: constructs a circular actor planet that moves
     */
    public MovingPlanet(double xPos, double yPos, double xV, double yV, int mass, int radius, String filePath, Color mapCol)
    {
        super(xPos, yPos, xV, yV, mass, radius, filePath, mapCol);
    }
}