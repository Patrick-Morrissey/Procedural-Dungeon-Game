// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

import asciiPanel.AsciiPanel;

public class SkeletonAI extends CreatureAI
{
    private Creature player;
    private final int fleeDuration;
    private final int chaseDuration;
    private int fleeTimeLeft;
    private int chaseTimeLeft;

    public SkeletonAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
        this.fleeDuration = 4;
        this.chaseDuration = 5;
    }

    // what the goblin does each update in order of priority
    public void onUpdate()
    {
        if(fleeTimeLeft > 0)
        {
            wander();
            fleeTimeLeft--;
        }

        else if(chaseTimeLeft > 0)
        {
            hunt(player);
            chaseTimeLeft--;
        }

        else if (creature.canSee(player.x, player.y, player.z))
        {
            switch ((int)(Math.random() * 5)){
                case 0:
                case 1:
                case 2:
                    if (canRangedWeaponAttack(player)) // 50% percent chance of attacking and then running
                    {
                        creature.rangedWeaponAttack(player);
                        fleeTimeLeft += fleeDuration;
                    }
                    break;
                // 25% chance they will chase the player
                case 3:
                    chaseTimeLeft += chaseDuration;
                    break;
                case 4:
                    if (canRangedWeaponAttack(player)) // 25% chance they will just attack the player
                        creature.rangedWeaponAttack(player);
                    break;
            }
        }

        else if (canPickup())
            creature.pickup();
        else
            wander();
    }
}
