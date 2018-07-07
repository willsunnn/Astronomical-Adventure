// William Sun, McCain Pratt, Cody Grijalva-Hylbert         period 5
// May 31 2018
// Frame - extends a JFrame, registers key strokes and mouse presses

import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Frame extends JFrame implements KeyListener, MouseListener, MouseMotionListener
{
    private Level level;
    private Player player;

    // precondition : none
    // postcondition: constructs a Frame with dimensions as defined in Driver
    public Frame()
    {
        super(Driver.GAME_TITLE);
        setSize((int)(Driver.defaultWidth*Driver.scale), (int)(Driver.defaultHeight*Driver.scale));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        repaint();
    }

    // precondition : frame has been initialized
    // postcondition: adds the listeners to the frame
    public void respringFrame()
    {
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);

        repaint();
    }

    // precondition : level is not null, and level has a player
    // postcondition: sets the level to be displayed in the frame
    public void addLevel(Level level)
    {
        this.level = level;
        this.player = level.getPlayer();
        respringFrame();
        getContentPane().add(level);
        level.repaint();
        setVisible(true);
    }

    // precondition : none
    // postcondition: passes release of keystrokes to player to stop movement
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player.setPress('w',false);
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.setPress('a',false);
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            player.setPress('s',false);
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.setPress('d',false);
        }
    }

    // precondition : none
    // postcondition: passes presses of keystrokes to player for movement
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player.setPress('w',true);
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.setPress('a',true);
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            player.setPress('s',true);
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.setPress('d',true);
        }
    }

    // precondition : none
    // postcondition: none
    public void keyTyped(KeyEvent key)
    {}

    // precondition : none
    // postcondition: none
    public void mouseClicked(MouseEvent mouse)
    {}

    // precondition : player has been initialized
    // postcondition: player spawns a bullet and adds it to the level
    public void mousePressed(MouseEvent mouse)
    {
        level.addPlayerBullet(player.shoot());
    }

    // precondition : none
    // postcondition: none
    public void mouseReleased(MouseEvent mouse)
    {}

    // precondition : none
    // postcondition: none
    public void mouseEntered(MouseEvent mouse)
    {}

    // precondition : none
    // postcondition: none
    public void mouseExited(MouseEvent mouse)
    {}

    // precondition : player has been initialized
    // postcondition: passes location of mouse on frame to the player
    public void mouseMoved(MouseEvent mouse)
    {
        player.updateMouseLocation( mouse.getX(), mouse.getY() - 25, level.getCamera() );
    }

    // precondition : none
    // postcondition: none
    public void mouseDragged(MouseEvent mouse)
    {}
}