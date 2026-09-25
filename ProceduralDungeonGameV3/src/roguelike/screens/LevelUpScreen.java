// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import java.awt.event.KeyEvent;
import java.util.List;
import roguelike.Creature;
import roguelike.LevelUpController;
import asciiPanel.AsciiPanel;

// a LevelUpScreen that uses a LevelUpController to show what
// can be picked and applies that choice

public class LevelUpScreen implements Screen
{
    private LevelUpController controller;
    private Creature player;
    private int picks;

    public LevelUpScreen(Creature player, int picks){
        this.controller = new LevelUpController();
        this.player = player;
        this.picks = picks;
    }
    @Override
    public void displayOutput(AsciiPanel terminal) {
        List<String> options = controller.getLevelUpOptions();
        int y = 5;
        terminal.clear(' ', 5, y, 30, options.size() + 2);
        terminal.write(" Choose a level up bonus ", 5, y++);
        terminal.write("------------------------------", 5, y++);
        for (int i = 0; i < options.size(); i++){

            terminal.write(String.format("[%d] %s", i+1, options.get(i)), 5, y++);
        }
    }
    @Override
    public Screen respondToUserInput(KeyEvent key) {
        List<String> options = controller.getLevelUpOptions();

        String chars = "";
        // keeps adding a new number to the end of chars for each option available starting with 1
        for (int i = 0; i < options.size(); i++){
            chars = chars + Integer.toString(i+1);
        }
        // gets the index of player's input in chars - this is used to get the option the player picked
        int i = chars.indexOf(key.getKeyChar());

        // if the key entered wasn't an option, stay on the level up screen
        if (i < 0)
            return this;
        // applies the selected upgrade
        controller.getLevelUpOption(options.get(i)).invoke(player
        );
        // reduces the remaining options
        if (--picks < 1)
            return null; // returns to the game if no more options remain
        else
            return this; // stays on the level up screen if the player still has options to pick from
    }
}
