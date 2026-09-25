// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;
import java.util.List;

// goblins that will chase you when they can, attack from afar when they
//can, and switch to better equipment they find
// Goblins start with random weapons and armor set in StuffFactory

// Our goblins will, in order of priority, try to: ranged attack, throw attack, melee attack, pickup stuff, and wander if they can’t do
// anything else.

public class GoblinAI extends CreatureAI
{
    private Creature player;
    public GoblinAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    // what the goblin does each update in order of priority
    public void onUpdate()
    {
        if(canUseBetterEquipment())
            useBetterEquipment();
        if (canRangedWeaponAttack(player))
            creature.rangedWeaponAttack(player);
        else if (canThrowAt(player))
            creature.throwItem(getWeaponToThrow(), player.x, player.y, player.z);
        else if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else if (canPickup())
            creature.pickup();
        else
            wander();
    }
}
