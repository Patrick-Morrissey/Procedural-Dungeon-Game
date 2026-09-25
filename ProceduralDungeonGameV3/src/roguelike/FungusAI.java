// Name: Patrick Morrissey K00218348
// Date: 24/04/2026
// Function: CA2 Dungeon Objectives and Gameplay Extension

package roguelike;

// the ai for the fungus creatures
// the fungi spreads to a nearby open space every once in a while as part of it’s behavior during updating
public class FungusAI extends CreatureAI
{
    private StuffFactory factory;
    private int spreadcount;

    public FungusAI(Creature creature, StuffFactory factory)
    {
        super(creature);
        this.factory = factory;
    }

    public void onUpdate()
    {
        // if spreadcount is less than 5 and the random number generated between 0 and 1 is less than 0.02
        // every update call has 0.2% chance of calling the spread method
        if (spreadcount < 5 && Math.random() < 0.002)
            spread();
    }

    private void spread()
    {
        // sets how far away from the parent fungus a child can spawn
        int x = creature.x + (int)(Math.random() * 11) - 5;
        int y = creature.y + (int)(Math.random() * 11) - 5;

        // if the tile at the generated coordinates can't be entered (e.g a wall), the method exits here, otherwise a new fungus
        // is spawned there and the spreadcount is incremented
        if (!creature.canEnter(x, y, creature.z))
            return;
        Creature child = factory.newFungus(creature.z);
        child.x = x;
        child.y = y;
        spreadcount++;
        // call doAction to pass a message of the event
        creature.doAction("spawn a child");
    }
}

