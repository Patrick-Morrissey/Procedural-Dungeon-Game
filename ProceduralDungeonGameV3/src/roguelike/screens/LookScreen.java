// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import roguelike.Creature;
import roguelike.Item;
import roguelike.Tile;

// The simplest targeting action is looking at surroundings; a LookScreen.
// This will display details to the user about whatever they are targeting.

public class LookScreen extends TargetBasedScreen
{
    public LookScreen(Creature player, String caption, int sx, int sy) {
        super(player, caption, sx, sy);
    }
    public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
        Creature creature = player.creature(x, y, player.z);
        if (creature != null){
            caption = creature.glyph() + " " + creature.name() + creature.details();
            return;
        }
        Item item = player.item(x, y, player.z);
        if (item != null){
            caption = item.glyph() + " " + item.name() + item.details();
            return;
        }
        Tile tile = player.tile(x, y, player.z);
        caption = tile.glyph() + " " + tile.details();
    }
}