// Name: Patrick Morrissey K00218348
// Date: 17/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

// phantoms can only be seen while a torch is equipped, can't be damaged by far, and will chase you when they can,

public class PhantomAI extends CreatureAI
{
    private Creature player;
    public PhantomAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    // what the phantom does each update in order of priority
    public void onUpdate()
    {
        if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else if (canPickup())
            creature.pickup();
        else
            wander();
    }
}
