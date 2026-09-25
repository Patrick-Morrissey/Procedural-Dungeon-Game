// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

public class BatAI extends CreatureAI
{
    public BatAI(Creature creature)
    {
        super(creature);
    }

    // bats move twice for every one of your moves
    public void onUpdate()
    {
        wander();
        wander();
    }
}
