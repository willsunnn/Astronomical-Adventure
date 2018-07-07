// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Camera - creates a point in level that defines the center of the camera in the level

public class Camera
{
    private double xPos;
    private double yPos;

    private int maxX;
    private int minX;
    private int maxY;
    private int minY;

    // precondition : levelWidth and levelHeight are equal to the levels parameters
    // postcondition: creates a camera 
    public Camera(int levelWidth, int levelHeight)
    {
        this.xPos = 0;
        this.yPos = 0;

        this.maxX = levelWidth/2 - Driver.defaultWidth/2;
        this.minX = -maxX;
        this.maxY = levelHeight/2 - Driver.defaultHeight/2;
        this.minY = -maxY;
    }

    // precondition : actor is initialized
    // postcondition: sets the location of the camera to the location of the actor, and checks the bounds of the camera
    public void updateLocation(Actor a)
    {
        this.xPos = a.getXPos();
        this.yPos = a.getYPos();
        checkInBounds();
    }

    // precondition : camera location has been set
    // postcondition: moves camera if camera is offscreen
    public void checkInBounds()
    {
        if(this.xPos > maxX){
            xPos = maxX;}
        else if(this.xPos<minX){
            xPos = minX;}

        if(this.yPos > maxY){
            yPos = maxY;}
        else if(this.yPos < minY){
            yPos = minY;}
    }

    // precondition : updateLocation has been called
    // postcondition: returns camera's xPosition
    public double getXPos()
    {   
        return this.xPos;
    }

    // precondition : updateLocation has been called
    // postcondition: returns camera's yPosition
    public double getYPos()
    {
        return this.yPos;
    }
}