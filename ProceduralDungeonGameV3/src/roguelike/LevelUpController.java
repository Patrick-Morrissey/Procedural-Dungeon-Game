// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;
import java.util.ArrayList;
import java.util.List;

// We need something to track all the possible level up options and enforce some of our level-up
// logic. We’ll call it a LevelUpController.

// These are anonymous classes. Anonymous classes can make some things very clear and succinct - other
//things are best left to regular classes.

public class LevelUpController
{
    private static LevelUpOption[] options = new LevelUpOption[]
    {
        new LevelUpOption("Increased hit points"){
            public void invoke(Creature creature) { creature.gainMaxHp(); }
        },
        new LevelUpOption("Increased base attack value"){
            public void invoke(Creature creature) { creature.gainAttackValue(); }
        },

        new LevelUpOption("Increased ranged attack value"){
            public void invoke(Creature creature) { creature.gainRangedAttackValue(); }
        },
        new LevelUpOption("Increased throw attack value"){
            public void invoke(Creature creature) { creature.gainThrowAttackValue(); }
        },
        new LevelUpOption("Increased defense value"){
            public void invoke(Creature creature) { creature.gainDefenseValue(); }
        },
        new LevelUpOption("Increased vision"){
            public void invoke(Creature creature) { creature.gainVision(); }
        },
        new LevelUpOption("Increased health regen rate"){
            // increases regen speed by 100
            public void invoke(Creature creature) { creature.modifyRegenHpPer1000(100); }
        }
    };

    // select one option at random and apply it to a given creature
    public void autoLevelUp(Creature creature){
        options[(int)(Math.random() * options.length)].invoke(creature);
    }

    // returns the list of level up option
    public List<String> getLevelUpOptions() {
        List<String> names = new ArrayList<String>();
        for (LevelUpOption option : options) {
            names.add(option.name());
        }
        return names;
    }

    // returns a level up option
    public LevelUpOption getLevelUpOption(String name) {
        for (LevelUpOption option : options) {
            if (option.name().equals(name))
                return option;
        }
        return null;
    }
}