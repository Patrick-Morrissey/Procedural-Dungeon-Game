// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;

// sometimes we’re in “play” mode, sometimes in “select spell” mode, and sometimes “you lost” mode.
// Each mode has a different way of handling input and output, and so will be represented by a
// different screen. Each screen will display output on our AsciiPanel and responds to user input
public interface Screen
{
    // takes an AsciiPanel to display itself on
    void displayOutput(AsciiPanel terminal);

    Screen respondToUserInput(KeyEvent key);
}