// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// HitBox - defines a HitBox that is used to check for collisions

import java.awt.*;

public class HitBox
{
    private Actor actor;

    private Polygon verticalPolygon;
    private Polygon hitPolygon;

    /* precondition : relative x and realtive y define the points relative to center of 
     *                the actor that creates a convex polygon to check collisions
     * postcondition: creates a HitBox containing 2 polygons
     *                vertical polgon is a reference used to construct hitPolygon
     *                hitPolygon is the polygon that reflects the actor's hitbox, 
     *                it is a rotated and translated version of vertical polygon
     */
    public HitBox(int[] relativeX, int[] relativeY, Actor a)
    {
        verticalPolygon = new Polygon(relativeX, relativeY, relativeX.length);
        actor = a;
        updateHitBox();
    }

    // precondition : none
    // postcondition: checks if this hitbox has collided with the other hitbox
    public boolean checkCollision(HitBox other)
    {
        return checkPolygonIntersection(this.hitPolygon, other.hitPolygon);
    }

    /* precondition : none
     * postcondition: checks if two polygons intersect
     *                two polygons intersect if any of their points are in the other polygon
     */
    public static boolean checkPolygonIntersection(Polygon polygon1, Polygon polygon2)
    {
        // checks if any point in p2 is in p1
        for(int i = 0; i < polygon2.npoints;i++)
        {
            Point point2 = new Point(polygon2.xpoints[i],polygon2.ypoints[i]);
            if(polygon1.contains(point2))
                return true;
        }

        // checks if any point in p1 is in p2
        for(int i = 0; i < polygon1.npoints;i++)
        {
            Point point1 = new Point(polygon1.xpoints[i],polygon1.ypoints[i]);
            if(polygon2.contains(point1))
                return true;
        }
        return false;
    }

    /* precondition : none
     * postcondition: takes the vertical hitbox used as reference, 
     *                rotates it (if applicable) and translates it to match the actor, 
     *                and then sets the new transformed polygon to hitPolygon
     */
    public void updateHitBox()
    {
        if(actor instanceof Entity)
        {
            rotatePolygon(((Entity)(actor)).getAngle());
        }
        else if(actor instanceof Bullet)
        {
            rotatePolygon(((Bullet)(actor)).getAngle());
        }
        else
        {
            rotatePolygon(0);
        }
        centerPolygon(actor.getXPos(), actor.getYPos());
    }

    // precondition : rotation has not been done
    // postcondition: translates the polygon to match location of the actor
    public void centerPolygon(double xCenter, double yCenter)
    {
        hitPolygon.translate((int)xCenter, (int)yCenter);
    }

    // precondition : rotatePolygon(angle) is called before centerPolygon(), and angle is in radians, counter clockwise with respect to vertical axis
    // postcondition: rotates each point of the polygon around 0,0
    public void rotatePolygon(double angle)
    {
        int numPoints = verticalPolygon.npoints;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];

        for(int i=0; i<numPoints; i++)
        {
            double[] rotatedPoint = rotatePoint( verticalPolygon.xpoints[i], verticalPolygon.ypoints[i], angle);
            xPoints[i] = (int)rotatedPoint[0];
            yPoints[i] = (int)rotatedPoint[1];
        }

        this.hitPolygon = new Polygon(xPoints, yPoints, numPoints);
    }

    // precondition : angle is in radians, counter clockwise with respect to vertical axis
    // postcondition: rotates the point around 0,0 and then returns it's new coordinates in a double[] in {x,y} format
    public static double[] rotatePoint(double xPoint, double yPoint, double angle)
    {
        double angleRespectToHorizontal = - angle;

        double cosAngle = Math.cos( angleRespectToHorizontal );
        double sinAngle = Math.sin( angleRespectToHorizontal );

        yPoint *= -1;   // so positive y is now up

        double rotatedX = xPoint*cosAngle - yPoint*sinAngle;
        double rotatedY = xPoint*sinAngle + yPoint*cosAngle;

        rotatedY *= -1;   // so positive y is now down again

        return new double[]{rotatedX,rotatedY};
    }

    // precondition : none
    // postcondition: draws a polygon that displays the hitbox on screen
    public void paintComponent(Graphics g, Camera c)
    {
        int numPoints = hitPolygon.npoints;
        int[] paintX = new int[numPoints];
        int[] paintY = new int[numPoints];
        for(int i=0; i<numPoints; i++)
        {
            paintX[i] = (int)(Driver.scale* (hitPolygon.xpoints[i] - c.getXPos() + Driver.defaultWidth/2));
            paintY[i] = (int)(Driver.scale* (hitPolygon.ypoints[i] - c.getYPos() + Driver.defaultHeight/2));
        }
        Polygon relativePolygon = new Polygon(paintX, paintY, numPoints);

        g.setColor(Color.RED);
        g.drawPolygon(relativePolygon);
    }

    // precondition : none
    // postcondition: prints a polygon by giving the x coordinates in one line, and the y coordinates in the next, used for troubleshooting
    public static void printPolygon(Polygon p)
    {
        for(int i=0; i<p.npoints; i++)
        {
            System.out.print(p.xpoints[i]+", ");
        }
        System.out.println("");
        for(int i=0; i<p.npoints; i++)
        {
            System.out.print(p.ypoints[i]+", ");
        }
        System.out.println("");
        System.out.println("");
    }
}