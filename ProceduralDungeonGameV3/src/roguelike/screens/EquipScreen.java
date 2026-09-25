// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import roguelike.Creature;
import roguelike.Item;

public class EquipScreen extends InventoryBasedScreen
{
    public EquipScreen(Creature player) {
        super(player);
    }
    protected String getVerb() {
        return "wear, wield, or remove";
    }
    protected boolean isAcceptable(Item item) {
        return item.attackValue() > 0 || item.defenseValue() > 0;
    }
    protected Screen use(Item item) {
        // if the weapon or armour is already equipped, it gets unequipped
        if(item == player.weapon() || item == player.armor())
        {
            player.unequip(item);
            return null; // this was added to ensure an item that was unequipped isn't automatically reequipped
        }
        player.equip(item);
        return null;
    }
}
