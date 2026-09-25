// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

import java.util.List;

// We will use the setter for the creature’s ai from the
// Creature class to wire up the creature and the creature’s ai. The ai also needs to do
// deal with the creature trying to enter a new tile. We’re going to have a specific ai for the
// player so it doesn’t matter what you use here since we are just going to override it.
public class CreatureAI
{
    protected Creature creature;

    public CreatureAI(Creature creature)
    {
        this.creature = creature;
        // wires up the creature and the creature’s ai
        this.creature.setCreatureAI(this);
    }

    // Deals with the creature trying to enter a new tile
    // default movement behaviour
    public void onEnter(int x, int y, int z, Tile tile){
        if (tile.isGround()){
            creature.x = x;
            creature.y = y;
            creature.z = z;
        } else {
            creature.doAction("bump into a wall");
        }
    }

    // deals with the creature updating
    public void onUpdate() {}

    // onNotify is empty in creature so non-players will ignore messages and the player ai will override
    public void onNotify(String message) {}

    // can the creature see certain points and other creatures
    public boolean canSee(int wx, int wy, int wz)
    {
        // if the creature and the point are on different levels, it can't see the point
        if (creature.z != wz)
            return false;
        // if the creature doesn't have the ability to find a target anywhere without sight
        if(!creature.canFindTargetAnywhere())
        {
            // if the distance between the creature and a point is greater than the vision radius, it can't see the point - squaring all values removes negatives
            if ((creature.x - wx) * (creature.x - wx) + (creature.y - wy) * (creature.y - wy) > creature.visionRadius() * creature.visionRadius())
                return false;
        }
        // checks every point in a line between the creature and the target point
        // this uses raycasting
        for (Point p : new Line(creature.x, creature.y, wx, wy)){
            if (creature.realTile(p.x, p.y, wz).isGround() || p.x == wx && p.y == wy)
                continue;

            return false;
        }

        return true;
    }

    /** Checks what actions a creature can do **/
    protected boolean canRangedWeaponAttack(Creature other){
        return creature.weapon() != null
                && creature.weapon().rangedAttackValue() > 0
                && creature.canSee(other.x, other.y, other.z);
    }

    protected boolean canThrowAt(Creature other) {
        return creature.canSee(other.x, other.y, other.z)
                && getWeaponToThrow() != null;
    }

    protected Item getWeaponToThrow() {
        Item toThrow = null;
        for (Item item : creature.inventory().getItems()){
            if (item == null || creature.weapon() == item || creature.armor() == item)
                continue;
            if (toThrow == null || item.thrownAttackValue() > toThrow.attackValue())
                toThrow = item;
        }
        return toThrow;
    }

    protected boolean canPickup()
    {
        return creature.item(creature.x, creature.y, creature.z) != null
                && !creature.inventory().isFull();
    }

    // method to check if non-player creatures have better equipment they could equip in their inventory
    protected boolean canUseBetterEquipment() {
        int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
        int currentArmorRating = creature.armor() == null ? 0 : creature.armor().defenseValue();
        for (Item item : creature.inventory().getItems()){
            if (item == null)
                continue;
            boolean isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();
            if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
                    || isArmor && item.defenseValue() > currentArmorRating)
                return true;
        }
        return false;
    }

    // method for getting non-player creatures to switch to better equipment
    protected void useBetterEquipment() {
        int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
        int currentArmorRating = creature.armor() == null ? 0 : creature.armor().defenseValue();
        for (Item item : creature.inventory().getItems()){
            if (item == null)
                continue;
            boolean isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();
            if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
                    || isArmor && item.defenseValue() > currentArmorRating) {
                creature.equip(item);

            }
        }
    }

    /******* You could also make it keep trying until mx != 0 && my != 0, that way it would never
     stand in the same spot. You may want to make sure it doesn’t try to move into a wall or
     make it be able to go up or down stairs.
     ******/
    //  method of moving randomly. This common behavior can be called by any subclass.
    public void wander()
    {
        int mx = (int)(Math.random() * 3) - 1;
        int my = (int)(Math.random() * 3) - 1;

        Creature other = creature.creature(creature.x + mx, creature.y + my, creature.z);

        // makes sure creatures don’t fight other’s like them
        if (other != null && other.glyph() == creature.glyph())
            return;
        else
            creature.moveBy(mx, my, 0);
    }

    // finds a path to the target and moves to it.
    // Creating a new path each turn may not be the best idea but we’ll only have a few zombies
    // and roguelikes are turn based so it shouldn’t be too much of a problem.
    public void hunt(Creature target)
    {
        List<Point> points = new Path(creature, target.x, target.y).points();

        // prevents null pointer exception
        if(points == null)
        {
            return;
        }
        int mx = points.get(0).x - creature.x;
        int my = points.get(0).y - creature.y;
        creature.moveBy(mx, my, 0);
    }

    public void flee(Creature target)
    {
        List<Point> points = new Path(creature, target.x, target.y).points();

        int mx = creature.x - points.get(0).x;
        int my = creature.y - points.get(0).y;
        creature.moveBy(mx, my, 0);
    }

    // CreatureAI can call this to automatically gain some benefit when a creature
    // gains a level.
    public void onGainLevel(){
        new LevelUpController().autoLevelUp(creature);
    }

    //  CreatureAI canSee method needs to use the new realTile method to avoid
    // getting caught in an infinite recursion loop. Here’s a good-enough-for-now implementation of the CreatureAI rememberedTile method since they don’t actually have a memory
    public Tile rememberedTile(int wx, int wy, int wz) {
        return Tile.UNKNOWN;
    }

}

