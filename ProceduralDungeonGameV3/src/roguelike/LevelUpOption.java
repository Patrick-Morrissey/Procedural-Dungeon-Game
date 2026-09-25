// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

// a class to represent a level up option’s name and actual effect

// For this simple roguelike, when something gains a level it get’s some stat bonus; increased
// hp, increased attack, etc. The player will be shown a list to chose from but other creatures
// will get one at random.

public abstract class LevelUpOption
{
    private String name;
    public String name() { return name; }

    public LevelUpOption(String name){
        this.name = name;
    }

    public abstract void invoke(Creature creature);
}
