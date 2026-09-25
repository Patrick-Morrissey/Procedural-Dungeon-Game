// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

import asciiPanel.AsciiPanel;

import java.awt.*;
import java.util.List;

// A lot of creatures that will be created will have the same values, all goblins will have a 𝑔 glyph etc,
// and we need to make sure we always wire up the correct ai for each new creature. To
// centralize and hide all this assembly, we’ll create a class that’s responsible for nothing
// else: the StuffFactory. Using a factory means the other code doesn’t have to
// deal with all this assembly each time a new creature is created.

/***
 * Don’t forget to tweak each creatures hp, attack, defense, the food values of corpses, and
 * the xp gained or how much xp is needed for each level. You should also change the number
 * of items and creatures per level. You could leave few items on the ground. That way the
 * player almost has to confront goblins to get better loot.
***/

public class StuffFactory
{
    private World world;
    public StuffFactory(World world)
    {
        this.world = world;
    }

    // the method for creating a player
    // messages list is created in playscreen, then passed to creature factory which then passes it to player ai
    public Creature newPlayer(List<String> messages, FieldOfView fov)
    {
        Creature player = new Creature(world, '@', AsciiPanel.brightWhite, "player",100, 20, 9,5);
        player.setMaxInventory(8);
        // uses the addAtEmptyLocation method in World to place the player at an empty location
        world.addAtEmptyLocation(player,0);
        new PlayerAI(player, messages, fov); // sets the player ai
        return player;
    }

    // the method for creating a fungus
    public Creature newFungus(int depth)
    {
        Creature fungus = new Creature(world, 'f', AsciiPanel.green, "fungus",10, 0, 0,9);
        world.addAtEmptyLocation(fungus, depth);
        new FungusAI(fungus, this); // sets the fungus ai and passes the creaturefactory into the ai's constructor
        return fungus;
    }

    public Creature newBat(int depth)
    {
        Creature bat = new Creature(world, 'b', AsciiPanel.yellow, "bat",15, 5, 0,9);
        bat.modifyIsFlameResistant(true);
        world.addAtEmptyLocation(bat, depth);
        new BatAI(bat);
        return bat;
    }

    public Creature newZombie(int depth, Creature player)
    {
        Creature zombie = new Creature(world, 'z', AsciiPanel.white, "zombie", 40, 10, 10,9);
        world.addAtEmptyLocation(zombie, depth);
        new ZombieAI(zombie, player);
        return zombie;
    }

    public Creature newGoblin(int depth, Creature player){
        Creature goblin = new Creature(world, 'g', AsciiPanel.brightGreen, "goblin", 60, 15, 5,9);
        goblin.equip(randomWeapon(depth));
        goblin.equip(randomArmor(depth));
        world.addAtEmptyLocation(goblin, depth);
        new GoblinAI(goblin, player);
        return goblin;
    }

    // skeletons have lower health than zombies and goblins but have moderate attack damage and are always armed with a bow
    public Creature newSkeleton(int depth, Creature player){
        Creature skeleton = new Creature(world, 's', AsciiPanel.brightWhite, "skeleton", 30, 10, 5,9);
        skeleton.equip(newBow(depth));
        world.addAtEmptyLocation(skeleton, depth);
        new SkeletonAI(skeleton, player);
        return skeleton;
    }

    // ghouls only spawn once the player picks up the victory item, they have high health, defence and attack of all enemies but have no weapons or armour and won't pick up anything
    public Creature newGhoul(int depth, Creature player){
        Creature ghoul = new Creature(world, 'h', AsciiPanel.brightMagenta, "ghoul", 75, 25, 10,9);
        ghoul.modifyCanFindTargetAnywhere(true);
        world.addAtEmptyLocation(ghoul, depth);
        new GhoulAI(ghoul, player);
        return ghoul;
    }

    // can only be seen with a lamp
    public Creature newPhantom(int depth, Creature player)
    {
        Creature phantom = new Creature(world, 'p', AsciiPanel.yellow, "phantom",30, 5, 5,20);
        phantom.modifyIsInvisible(true);
        phantom.modifyIsFlameResistant(true);
        world.addAtEmptyLocation(phantom, depth);
        new PhantomAI(phantom, player);
        return phantom;
    }

    // has very high health, moderate attack damage, moves very slowly aside from when it's not charging at the player. Drops a shovel when killed
    public Creature newRiftWorm(int depth, Creature player)
    {
        Creature riftWorm = new Creature(world, 'w', new Color(123,60,0), "rift worm",85, 40, 10,15);
        riftWorm.inventory().add(newShovel(depth));
        world.addAtEmptyLocation(riftWorm, depth);
        new RiftWormAI(riftWorm, player);
        return riftWorm;
    }


    // One advantage of having all our items be the same class but have different values is that
    // an item can be more than one thing, e.g. you could make an edible weapon and the player
    // would be able to eat or wield it with no extra code, or you could have a weapon that
    // increases attack and defense.

    public Item newRock(int depth){
        Item rock = new Item(',', AsciiPanel.yellow, "rock");
        rock.modifyThrownAttackValue(5);
        world.addAtEmptyLocation(rock, depth);
        return rock;
    }

    /***** legendary battle teddy bear that is the best weapon in the game, it can shoot fire from its eyes - causes more zombies to spawn when picking it up
     * can be used for melee or ranged combat
     *****/
    public Item newVictoryItem(int depth){
        Item item = new Item('t', AsciiPanel.brightWhite, "Battle Teddy of Destiny");
        world.addAtEmptyLocation(item, depth);
        item.modifyAttackValue(200);
        item.modifyRangedAttackValue(200);
        return item;
    }

    /** the three keys needed to open the locked chamber on the bottom level **/
    public Item newYellowKey(int depth){
        Item item = new Item('k', AsciiPanel.brightYellow, "yellow key");
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newBlueKey(int depth){
        Item item = new Item('k', AsciiPanel.brightBlue, "blue key");
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newGreenKey(int depth){
        Item item = new Item('k', AsciiPanel.brightGreen, "green key");
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newEtherealLamp(int depth){
        Item item = new Item('l', AsciiPanel.brightYellow, "ethereal lamp");
        item.modifyAttackValue(1);
        item.modifyVisionIncreaseValue(10);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newShovel(int depth){
        Item item = new Item('/', AsciiPanel.yellow, "shovel");
        item.modifyAttackValue(1);
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    public Item newDagger(int depth){
        Item item = new Item(')', AsciiPanel.white, "dagger");
        item.modifyAttackValue(5 + depth);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newSword(int depth){
        Item item = new Item(')', AsciiPanel.brightWhite, "sword");
        item.modifyAttackValue(10 + depth);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newStaff(int depth){
        Item item = new Item(')', AsciiPanel.yellow, "staff");
        item.modifyAttackValue(5 + depth);
        item.modifyDefenseValue(3 + depth);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newBow(int depth){
        Item item = new Item(')', AsciiPanel.yellow, "bow");
        item.modifyAttackValue(1);
        item.modifyRangedAttackValue(5 + depth);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newDynamite(int depth){
        Item item = new Item('!', AsciiPanel.red, "dynamite stick");
        item.modifyAttackValue(1);
        item.modifyBlastRadiusValue(4);
        item.modifyThrownAttackValue(10);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newLightArmor(int depth){
        Item item = new Item('[', AsciiPanel.green, "tunic");
        item.modifyDefenseValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newMediumArmor(int depth){
        Item item = new Item('[', AsciiPanel.white, "chainmail");
        item.modifyDefenseValue(4);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newHeavyArmor(int depth){
        Item item = new Item('[', AsciiPanel.brightWhite, " platemail");
                item.modifyDefenseValue(6);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // a simple one-time potion where the work happens in the start method itself
    public Item newPotionOfHealth(int depth){
        Item item = new Item('!', AsciiPanel.white, "health potion");
                item.setQuaffEffect(new Effect(1){
                    public Item start(Creature creature){
                        if (creature.hp() == creature.maxHp())
                            return null;
                        creature.modifyHp(15);
                        creature.doAction("look healthier");
                        return null;
                    }
                });
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // a simple one-time potion where the work happens in the start method itself
    public Item newJugOfWater(int depth){
        Item item = new Item('!', AsciiPanel.white, "water jug");
        item.modifyBlastRadiusValue(4);
        item.setQuaffEffect(new Effect(1){
            public Item start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return null;
                creature.modifyHp(5);
                creature.doAction("look healthier");
                return null;
            }
        });
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // a potion that affects the creature each turn
    public Item newPotionOfPoison(int depth){
        Item item = new Item('!', AsciiPanel.white, "poison potion");
                item.setQuaffEffect(new Effect(20)
                {
                    public Item start(Creature creature){
                        creature.doAction("look sick");
                        return null;
                    }
                    public void update(Creature creature){
                        super.update(creature);
                        creature.modifyHp(-1);
                    }
                });
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // a potion that will affect the creature at the start and restore it at the end
    public Item newPotionOfWarrior(int depth){
        Item item = new Item('!', AsciiPanel.white, "warrior's potion");
                item.setQuaffEffect(new Effect(20)
                {
                    public Item start(Creature creature){
                        creature.modifyAttackValue(5);
                        creature.modifyDefenseValue(5);
                        creature.doAction("look stronger");
                        return null;
                    }
                    public void end(Creature creature){
                        creature.modifyAttackValue(-5);
                        creature.modifyDefenseValue(-5);
                        creature.doAction("look less strong");
                    }
                });
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // a potion that will make the creature immune to fire for 20 turns
    public Item newPotionOfFireResistance(int depth){
        Item item = new Item('!', AsciiPanel.white, "fire resistance potion");
        item.setQuaffEffect(new Effect(20)
        {
            public Item start(Creature creature){
                creature.modifyIsFlameResistant(true);
                creature.doAction("are resistant to fire");
                return null;
            }
            public void end(Creature creature){
                creature.modifyIsFlameResistant(false);
                creature.doAction("are vulnerable to fire again");
            }
        });
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    // method for generating a random weapon
    public Item randomWeapon(int depth){
        switch ((int)(Math.random() * 4)){
            case 0: return newDagger(depth);
            case 1: return newSword(depth);
            case 2: return newBow(depth);
            default: return newStaff(depth);
        }
    }

    // method for generating a random piece of armour
    public Item randomArmor(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newLightArmor(depth);
            case 1: return newMediumArmor(depth);
            default: return newHeavyArmor(depth);
        }
    }

    // method for generating a random potion
    public Item randomPotion(int depth){
        switch ((int)(Math.random() * 4)){
            case 0: return newPotionOfHealth(depth);
            case 1: return newPotionOfPoison(depth);
            case 2: return newPotionOfFireResistance(depth);
            default: return newPotionOfWarrior(depth);
        }
    }

    public Item newEdibleWeapon(int depth){
        Item item = new Item(')', AsciiPanel.yellow, "baguette");
        item.modifyAttackValue(3);
        item.modifyFoodValue(50);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newHealingHerb(int depth){
        Item item = new Item('"', AsciiPanel.green, "healing herb");
        item.setQuaffEffect(new Effect(1){
            public Item start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return null;
                creature.modifyHp(10);
                creature.doAction("look healthier");
                return null;
            }
        });
        item.modifyFoodValue(100);

        world.addAtEmptyLocation(item, depth);
        return item;
    }
}
