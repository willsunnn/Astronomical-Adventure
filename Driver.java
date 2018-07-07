// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Driver - runs the game and adds background sounds

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Driver
{
    public final static String GAME_TITLE = "Cody's Astronomically Amazing Astronomical Adventure";
    public final static int FPS = 30;               //frames per second
    public final static long time = 1000/FPS;       //milliseconds per frame
    public final static int defaultHeight = 540;    //in pixels
    public final static int defaultWidth = 540*16/9;//for 16x9 aspect ratio
    public static double scale = 1.0;           //for scaling the display
    public static final File defaultBGM = new File("resources/sounds/background_music.wav");

    public static boolean drawHitBoxes;

    public final static double G_CONSTANT = 100000;      //in acceleration is pixels per s^2
    //pixels * pixels^2 / s^2 / mass

    private FileReader files;
    private Frame frame;
    private Level level;
    private Player player;
    private Clip clip;

    private boolean gameFinished;
    private boolean buttonPressed;

    // precondition : none
    // postcondition: starts the game
    public static void main(String[] args)
    {
        start();
    }

    // precondition : none
    // postcondition: starts the game with scale 1.0 and without drawing hitboxes
    public static void start()
    {
        start(1.0, false);
    }

    // precondition : scale is not zero
    // postcondition: starts the game scaled up to size scale and with/without drawing hitboxes
    public static void start(double scale, boolean hitboxes)
    {
        Driver.scale = scale;
        Driver.drawHitBoxes = hitboxes;
        Driver d = new Driver();

        d.startScreen();
        d.loopMusic(defaultBGM);
        if(! d.gameFinished)
        {
            int startLevel = 3;     //level 3 is earth level
            d.gameFinished = false;
            d.buttonPressed = false;
            while(!d.gameFinished)
            {
                d.runGame(startLevel);
                d.gameOver();
                d.clearGame();
            }

        }
        d.frame.setVisible(false); 
        d.frame.dispose(); 
        d.stopMusic();
    }

    // precondition : file is a valid playable file in format .wav
    // postcondition: loops the music until stopped
    public void loopMusic(File file)
    {
        try
        {
            AudioInputStream sound = AudioSystem.getAudioInputStream(file);
            this.clip = AudioSystem.getClip();
            clip.open(sound);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch(Exception e)
        {
            System.out.println("BGM error: " + e.getMessage());
        }
    }

    // precondition : none
    // postcondition: stops the loop of music
    public void stopMusic()
    {
        this.clip.stop();
    }

    // precondition : none
    // postcondition: constructs a file reader class to take levels as well as a frame class
    public Driver()
    {
        this.player = new Player(0,0,0,0,0,0,new Color(0,0,0));
        files = new FileReader(this.player);
        frame = new Frame();
    }

    // precondition : none
    // postcondition: makes levels from scratch from text again
    public void clearGame()
    {
        player = new Player(0,0,0,0,0,0,new Color(0,0,0));
        level = null;
        files = new FileReader(player);     //reloads all the levels
    }

    // precondition : startLevel is between 0 and NUM_OF_LEVELS defined in FileReader, or -1 to end the game
    // postcondition: runs the game, starting at level startLevel
    public void runGame(int startLevel)
    {
        int levelNum = startLevel;
        boolean done = false;
        while(!done)   //levelNum = -1 when there are no more levels
        {
            levelNum = runLevel(levelNum);
            if(levelNum == -1 || levelNum >= FileReader.TOTAL_NUM_OF_LEVELS)
            {
                done = true;
            }
        }
    }

    // precondition : levelNum is between 0 and NUM_OF_LEVELS defined in FileReader
    // postcondition: runs one level, returns the index of the next level or -1 if no more levels
    public int runLevel(int levelNum)
    {
        boolean changeLocation = false;
        double xPos = 0.0;
        double yPos = 0.0;
        if(level!=null)
        {
            changeLocation = true;
            xPos = level.getNextLevelX();
            yPos = level.getNextLevelY();
            if(xPos==0 && yPos==0)
            {
                changeLocation = false;
            }
        }

        frame.getContentPane().removeAll();
        level = files.getLevel(levelNum);
        frame.addLevel(level);
        int nextLevel = level.play(changeLocation, xPos, yPos);
        return nextLevel;
    }

    // precondition : frame has been initialized
    // postcondition: creates the start screen in the frame
    public void startScreen()
    {
        gameFinished = true;
        buttonPressed = false;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,(int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale));

        int buttonY = (int)( 300.0/540 * (Driver.defaultHeight*Driver.scale));
        int buttonWidth = (int)( 90.0/960 * (Driver.defaultWidth*Driver.scale));
        int buttonHeight = (int)( 40.0/960 * (Driver.defaultWidth*Driver.scale));

        int playButtonX = (int)( 305.0/960 * (Driver.defaultWidth*Driver.scale));

        JButton playButton = new JButton("Start");
        playButton.setBounds( playButtonX, buttonY, buttonWidth, buttonHeight);
        playButton.setFocusable(false);
        playButton.addActionListener(new RestartButtonListener(false, this));
        panel.add(playButton);

        int quitButtonX = (int)(565.0/960 * (Driver.defaultWidth*Driver.scale));

        JButton quitButton = new JButton("Quit");
        quitButton.setBounds( quitButtonX, buttonY, buttonWidth, buttonHeight);
        quitButton.setFocusable(false);
        quitButton.addActionListener(new RestartButtonListener(true, this));
        panel.add(quitButton);

        ImageIcon startScreen = new ImageIcon("resources/title_screens/start_screen.png");
        JLabel label = new JLabel(startScreen);
        label.setBounds(0,0,(int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale));
        panel.add(label);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize((int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale));

        while(!buttonPressed)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {}
        }

        //if it reaches here, either start or quit has been pressed, and gameFinished is set by Jbutton accordingly
    }

    // precondition : frame has been initialized
    // postcondition: screen displays game over screen
    public void gameOver()
    {
        buttonPressed = false;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,(int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale));

        int buttonY = (int)( 380.0/540 * (Driver.defaultHeight*Driver.scale));
        int buttonWidth = (int)( 90.0/960 * (Driver.defaultWidth*Driver.scale));
        int buttonHeight = (int)( 40.0/960 * (Driver.defaultWidth*Driver.scale));

        int yesX = (int)( 305.0/960 * (Driver.defaultWidth*Driver.scale));

        JButton yes = new JButton("yes");
        yes.setBounds( yesX, buttonY, buttonWidth, buttonHeight);
        yes.setFocusable(false);
        yes.addActionListener(new RestartButtonListener(false, this));
        panel.add(yes);

        int noX = (int)(565.0/960 * (Driver.defaultWidth*Driver.scale));

        JButton no = new JButton("no");
        no.setBounds( noX, buttonY, buttonWidth, buttonHeight);
        no.setFocusable(false);
        no.addActionListener(new RestartButtonListener(true, this));
        panel.add(no); 
        ImageIcon gameOver = new ImageIcon("resources/title_screens/game_over.png");
        JLabel label = new JLabel(gameOver);
        label.setBounds(0,0,(int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale));
        panel.add(label);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize((int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale));

        while(!this.buttonPressed)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {}
        }
        frame.getContentPane().removeAll();
    }

    //RestartButtonListener - used in driver to tell when the JButton has been pressed and whether or not the game should continue
    private class RestartButtonListener implements ActionListener
    {
        private boolean done;
        private Driver driver;

        // precondition : driver has been initialized
        // postcondition: creates a button that determines whether or not the game should continue
        //if false game starts, if true game ends
        private RestartButtonListener(boolean done, Driver driver)
        {
            this.done = done;
            this.driver = driver;
        }

        // precondition : none
        // postcondition: updates the status of the driver
        public void actionPerformed(ActionEvent e)
        {
            driver.gameFinished = this.done;
            driver.buttonPressed = true;
        }
    }
}