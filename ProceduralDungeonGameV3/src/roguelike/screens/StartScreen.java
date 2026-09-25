// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;

// The first screen players will see is the StartScreen. This is just a screen that displays
// some info and sets us in “play” mode when the user hits enter.
public class StartScreen implements Screen
{
    public void displayOutput(AsciiPanel terminal)
    {
        terminal.write("Welcome to the Roguelike Game", 1, 1);
        terminal.writeCenter("Press [Enter] to start", 22);
    }
    public Screen respondToUserInput(KeyEvent key)
    {
        if (key.getKeyCode() == KeyEvent.VK_ENTER)
        {
            return new PlayScreen();
        }
        return this;
    }
}

