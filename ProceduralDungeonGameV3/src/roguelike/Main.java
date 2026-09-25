// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

import javax.swing.JFrame;
import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import roguelike.screens.Screen;
import roguelike.screens.StartScreen;

// creating an AsciiPanel
// to display some text and making sure the window is the correct size
public class Main extends JFrame implements KeyListener
{
    private AsciiPanel terminal;
    private Screen screen;

    public Main() {
        super("Roguelike Game");
        terminal = new AsciiPanel();
        add(terminal);
        pack();
        screen = new StartScreen();
        addKeyListener(this); // this is a KeyListener interface method used for adding key listeners

        repaint();
    }

    public void repaint()
    {
        terminal.clear(); // clears the ascii panel's contents
        // updates the screen with new the contents of terminal generated from this method is the current active window
        screen.displayOutput(terminal);
        // a method in JFrame used for scheduling an event on the repaint queue so java will invoke
        // the class's paint method the next time it can (e.g. when a key input is detected)
        super.repaint(); // calls
    }

    public void keyPressed(KeyEvent e)
    {
        screen = screen.respondToUserInput(e); // the screen will be set to the screen object is returned by this method
        repaint();
    }

    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }

    public static void main(String[] args) {
        Main app = new Main();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }
}
