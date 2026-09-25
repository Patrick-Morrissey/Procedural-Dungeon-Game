// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import roguelike.Creature;
import roguelike.Item;

// a screen that tells us details about what’s in our inventory

public class ExamineScreen extends InventoryBasedScreen
{
    public ExamineScreen(Creature player) {
        super(player);
    }
    protected String getVerb() {
        return "examine";
    }
    protected boolean isAcceptable(Item item) {
        return true;
    }
    protected Screen use(Item item) {
        // picks the correct article for the item - a or an
        String article = "aeiou".contains(item.name().subSequence(0, 1)) ? "an " : "a ";
        player.notify("It's " + article + item.name() + "." + item.details());
        return null;
    }
}