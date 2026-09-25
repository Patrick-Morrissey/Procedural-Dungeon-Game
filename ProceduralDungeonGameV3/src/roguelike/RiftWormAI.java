// Name: Patrick Morrissey K00218348
// Date: 17/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

// riftworms move very slowly, can dig through walls, have great vision, and will hunt the player if they see them

public class RiftWormAI extends CreatureAI
{
    private Creature player;
    private final int movementWaitDuration = 3;
    private int turnsLeftToWaitBeforeMoving;

    private final int chargeWaitDuration = 3;
    private final int chargeMaxDuration = 5;
    private final int chargeMinDuration = 5;
    private int chargeTurnsLeft;

    public RiftWormAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
        this.turnsLeftToWaitBeforeMoving = movementWaitDuration;
    }

    // overrides the creatureAI onEnter method and allows the creature to dig through diggable walls
    public void onEnter(int x, int y, int z, Tile tile)
    {
        if (tile.isGround()){
            creature.x = x;
            creature.y = y;
            creature.z = z;
        }
        // allows the riftWorm to dig if the tile is diggable
        else if (tile.isDiggable()) {
            creature.dig(x, y, z);
            creature.x = x;
            creature.y = y;
            creature.z = z;
        }
    }

    // what the goblin does each update in order of priority
    public void onUpdate()
    {
        if(turnsLeftToWaitBeforeMoving > 0)
        {
            turnsLeftToWaitBeforeMoving--;
        }
        else {
            turnsLeftToWaitBeforeMoving = movementWaitDuration;

            if (creature.canSee(player.x, player.y, player.z))
                hunt(player);
            else if (canPickup())
                creature.pickup();
            else
                wander();
        }
    }

    // I was going to a way to make worms charge at the player (move in the direction of the player at the time of
    // starting the method for a set number of turns and breaks any walls encountered) but I couldn't figure out how to do it
    public int distanceToTarget()
    {
        Point currentPos = new Point(creature.x, creature.y, creature.z);
        return currentPos.distanceBetweenPoints(creature.x, creature.y, player.x, player.y);
    }

}
