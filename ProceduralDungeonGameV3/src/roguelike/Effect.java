// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

// When the player, or smart monster, quaffs a potion it’s effect will be applied
// to the creature. What can we say about effects? Most effects will only last for a certain
// duration. Some will apply every turn or every few turns (like poison or a slow heal). Others
// will have a change that lasts for the duration (like confuse or resist cold). This can be done
// with a start method that that applies the change when first quaffed, an end method that
// unapplies the change when the duration has run out, and an update method that is called
// every turn in between.

public class Effect
{
    protected int duration;
    public boolean isDone() { return duration < 1; }
    public Effect(int duration){
        this.duration = duration;
    }
    public void update(Creature creature){
        duration--;
    }
    public Item start(Creature creature){
        return null;
    }
    public void end(Creature creature){
    }
}