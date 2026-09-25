// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import roguelike.Creature;
import roguelike.Item;

// allows the player to eat something in their inventory

public class EatScreen extends InventoryBasedScreen
{
    public EatScreen(Creature player) {
        super(player);
    }
    protected String getVerb() {
        return "eat";
    }
    protected boolean isAcceptable(Item item) {
        return item.foodValue() != 0;
    }
    protected Screen use(Item item) {
        player.eat(item);
        return null;
    }
}