// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

// The LoseScreen will eventually display how lame our foolish hero was and ask if they’d
// like to play again
public class LoseScreen implements Screen
{
    public void displayOutput(AsciiPanel terminal)
    {
        terminal.write("You failed to recover the Battle Teddy of Destiny and lost.", 1, 1);
        terminal.writeCenter("Press [Enter] to play again", 22);
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