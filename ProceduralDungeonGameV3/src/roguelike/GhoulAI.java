// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

public class GhoulAI extends CreatureAI
{
    private Creature player; // needs a reference to the player so it knows who to look for

    public GhoulAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    // The ghoul has a very small chance of doing nothing during its turn otherwise it will constantly chase the player without needing to see them
    public void onUpdate(){
        if (Math.random() < 0.1)
            return;
        if(canSee(player.x, player.y, player.z))
            hunt(player);
        else {
            wander();
        }
    }
}

