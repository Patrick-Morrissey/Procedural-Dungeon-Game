// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

public class ZombieAI extends CreatureAI
{
    private Creature player; // needs a reference to the player so it knows who to look for

    public ZombieAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    // During the zombie’s turn it will move to the player if it can see him, otherwise it will wander
    // around. Since zombies are a little slow, we’ll give them a chance of doing nothing during
    // their turn for just a little bit of interest.
    public void onUpdate(){
        if (Math.random() < 0.2)
            return;
        if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else
            wander();
    }
}

