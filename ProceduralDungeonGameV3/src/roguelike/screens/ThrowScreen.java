// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import roguelike.Creature;
import roguelike.Item;

// The ThrowScreen is also a simple InventoryBasedScreen except we need
// to pass some values that aren’t needed by the ThrowScreen but are used by the ThrowAtScreen.

public class ThrowScreen extends InventoryBasedScreen
{
    private int sx;
    private int sy;
    public ThrowScreen(Creature player, int sx, int sy) {
        super(player);
        this.sx = sx;
        this.sy = sy;
    }
    protected String getVerb() {
        return "throw";
    }
    protected boolean isAcceptable(Item item) {
        return true;
    }
    protected Screen use(Item item) {
        return new ThrowAtScreen(player, sx, sy, item);
    }
}
