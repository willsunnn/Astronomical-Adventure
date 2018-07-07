// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// StationaryPlanet - a Planet that cannot move

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class StationaryPlanet extends Planet
{
    /* precondition : filePath is the ImageIcon, relative to "resources/actor_gifs/planet_gifs/"
     *                mapCol is the color of the planet's icon on the map
     * postcondition: constructs a circular actor planet, that does not move
     */
    public StationaryPlanet(double xPos, double yPos, int mass, int radius, String filePath, Color mapCol)
    {
        super(xPos, yPos, 0, 0, mass, radius, filePath, mapCol);
    }

    // precondition : none
    // postcondition: overrides super methods because StationaryPlanet does not move
    public void recalculateAcceleration(ArrayList<Planet> planets)
    {}

    // precondition : none
    // postcondition: overrides super methods because StationaryPlanet does not move
    public void recalculateVelocity(long time)
    {}

    // precondition : none
    // postcondition: overrides super methods because StationaryPlanet does not move
    public void recalculatePosition(long time)
    {}

}