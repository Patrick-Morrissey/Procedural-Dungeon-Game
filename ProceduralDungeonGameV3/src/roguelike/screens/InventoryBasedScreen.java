// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import roguelike.Creature;
import roguelike.Item;
import asciiPanel.AsciiPanel;

//  There’s a key that gets pressed, some verb (drop, quaff, read), some
// check against the items (droppable, quaffable, readable), and some action (drop, quaff,
// read). The common behavior can be put in one class called InventoryBasedScreen and the
// specific details can be in subclasses. That way we can have a DropScreen, QuaffScreen,
// ReadScreen and others that all subclass the InventoryBasedScreen and just provide a few simple details

// We need the reference to the player because that’s the one who’s going to do the work
// of dropping, quaffing, eating, etc. It’s protected so that the subclasses can use it. The
// letters are so we can assign a letter to each inventory slot (if you allow the inventory to be
// larger then you need to add more characters). We’ve also got abstract methods so our
// subclasses can specify the verb, what items are acceptable for the action, and a method
// to actually perform the action. Using an item returns a Screen since it may lead to a
// different screen, e.g. if we’re going to throw something then we can transition into some
// sort of targeting screen.

public abstract class InventoryBasedScreen implements Screen
{
    protected Creature player;
    private String letters;
    protected abstract String getVerb();
    protected abstract boolean isAcceptable(Item item);
    protected abstract Screen use(Item item);

    public InventoryBasedScreen(Creature player)
    {
        this.player = player;
        this.letters = "abcdefghijklmnopqrstuvwxyz";
    }

    // We not only ask what they want to use but go ahead and show a list of acceptable items.
    // write the list in the lower left hand corner and ask the user what to do
    public void displayOutput(AsciiPanel terminal) {
        ArrayList<String> lines = getList();
        int y = 23 - lines.size();
        int x = 4;
        if (lines.size() > 0)
            terminal.clear(' ', x, y, 20, lines.size());
        for (String line : lines){
            terminal.write(line, x, y++);
        }
        terminal.clear(' ', 0, 23, 80, 1);
        terminal.write("What would you like to " + getVerb() + "? ", 2, 23);
                terminal.repaint();
    }

    // makes a list of all the acceptable items and the letter for each corresponding inventory slot
    private ArrayList<String> getList()
    {
        ArrayList<String> lines = new ArrayList<String>();
        Item[] inventory = player.inventory().getItems();
        for (int i = 0; i < inventory.length; i++){
            Item item = inventory[i];
            if (item == null || !isAcceptable(item))
                continue;

            // tells the player what they have equipped
            String line = letters.charAt(i) + " - " + item.glyph() + " " + item.name();
            if(item == player.weapon() || item == player.armor())
                line += " (equipped)";
            lines.add(line);
        }
        return lines;
    }

    // press escape to go back to playing the game, select a valid character to use, or some invalid key
    // that will do nothing and keep you on the current screen
    public Screen respondToUserInput(KeyEvent key)
    {
        char c = key.getKeyChar();
        Item[] items = player.inventory().getItems();
        if (letters.indexOf(c) > -1
                && items.length > letters.indexOf(c)
                && items[letters.indexOf(c)] != null
                && isAcceptable(items[letters.indexOf(c)]))
            return use(items[letters.indexOf(c)]);
        else if (key.getKeyCode() == KeyEvent.VK_ESCAPE)
            return null;
        else
            return this;
    }


}