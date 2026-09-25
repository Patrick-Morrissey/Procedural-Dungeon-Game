// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import roguelike.Creature;
import roguelike.Item;

// allows the player to quaff items

public class QuaffScreen extends InventoryBasedScreen
{
    public QuaffScreen(Creature player) {
        super(player);
    }
    protected String getVerb() {
        return "quaff";
    }
    protected boolean isAcceptable(Item item) {
        return item.quaffEffect() != null;
    }
    protected Screen use(Item item) {
        player.quaff(item);
        return null;
    }
}