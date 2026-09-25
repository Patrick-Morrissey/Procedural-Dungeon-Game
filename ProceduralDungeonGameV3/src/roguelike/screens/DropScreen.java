// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import roguelike.Creature;
import roguelike.Item;

public class DropScreen extends InventoryBasedScreen
{
    public DropScreen(Creature player) {
        super(player);
    }

    // We’re asking the user what they want to drop so the getVerb should return that.
    protected String getVerb() {
        return "drop";
    }

    // anything can be dropped, so all items are acceptable
    protected boolean isAcceptable(Item item) {
        return true;
    }

    // Once the user selects what to drop we tell the player to do the work and return null since
    // we are done with the DropScreen
    protected Screen use(Item item) {
        player.drop(item);
        return null;
    }

}

