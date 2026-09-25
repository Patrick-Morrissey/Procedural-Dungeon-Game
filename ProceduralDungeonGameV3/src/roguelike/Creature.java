// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

// We will need something to represent our player and eventually monsters - the creatures
// in our game. They will all have an 𝑥 and 𝑦 coordinate, a glyph, and a color. Since they will
// be interacting with the world, they should have a reference to that too

public class Creature
{
    private World world;
    // the x and y coordinates are publicly accessible because they will be used a lot and we don't want them
    // to be constrained or do anything when they change
    // Setters and getters could always be used instead
    public int x;
    public int y;
    public int z;

    private char glyph;
    public char glyph() {


        return glyph;

    }
    private Color color;
    public Color color() { return color; }
    private CreatureAI ai;


    /** Now you can find and use weapons and armor. Play around with different attackValues,
     defenseValues, and hit points. You can have 3 or 4 weapons or 300 weapons. Try changing
     how abundant weapons and armor are or maybe have some more common than others.
     **/
    private Inventory inventory;
    public Inventory inventory() { return inventory; }
    public void setMaxInventory(int value) { inventory = new Inventory(value); }

    // variables to support creature attacks and health deduction based on damage
    // the values are set using constructor injection
    private int maxHp;
    public int maxHp() { return maxHp; }

    private int hp;
    public int hp() { return hp; }

    private int attackValue;
    public void modifyAttackValue(int value) { attackValue += value; }

    private int throwAttackValue;
    public void modifyThrowAttackValue(int value) { throwAttackValue += value; }

    private int rangedAttackValue;
    public void modifyRangedAttackValue(int value) { rangedAttackValue += value; }

    private int defenseValue;
    public void modifyDefenseValue(int value) { defenseValue += value; }

    // returns the attackValue of the creature and adds attack value to the result if a weapon or armour is equipped
    public int attackValue() {
        return attackValue
                + (weapon == null ? 0 : weapon.attackValue())
                + (armor == null ? 0 : armor.attackValue());
    }

    // returns the defenseValue of the creature and adds defence value to the result if a weapon or armour is equipped
    public int defenseValue() {
        return defenseValue
                + (weapon == null ? 0 : weapon.defenseValue())
                + (armor == null ? 0 : armor.defenseValue());
    }

    // says how far the creature can see
    private int visionRadius;
    public int visionRadius() {

        int visionValue = this.visionRadius;

        if(hasTorchEquipped())
        {
            visionValue += weapon.visionIncreaseValue();
        }

        return visionValue;
    }

    private String name;
    public String name() { return name; } // returns the creature's name - used by messages

    private int maxFood;
    public int maxFood() { return maxFood; }
    private int food;
    public int food() { return food; }

    private Item weapon;
    public Item weapon() { return weapon; }
    private Item armor;
    public Item armor() { return armor; }

    private int xp;
    public int xp() { return xp; }

    private int level;
    public int level() { return level; }

    private int regenHpCooldown;
    private int regenHpPer1000;

    private List<Effect> effects;
    public List<Effect> effects(){ return effects; }

    private boolean canFindTargetAnywhere;
    public boolean canFindTargetAnywhere() { return canFindTargetAnywhere; }
    public void modifyCanFindTargetAnywhere(boolean canFindTargetAnywhere) { this.canFindTargetAnywhere = canFindTargetAnywhere; }

    private boolean isInvisible;
    public boolean isInvisible() { return isInvisible; }
    public void modifyIsInvisible(boolean isInvisible) { this.isInvisible = isInvisible; }

    private boolean isFlameResistant;
    public boolean isFlameResistant() { return isFlameResistant; }
    public void modifyIsFlameResistant(boolean isFlameResistant) { this.isFlameResistant = isFlameResistant; }

    private boolean canDig;
    public boolean canDig() { return canDig; }
    public void modifyCanDig(boolean canDig) { this.canDig = canDig; }

    public Creature(World world, char glyph, Color color, String name, int maxHp, int attack, int defense, int vision)
    {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.name = name;

        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.throwAttackValue = attack;
        this.rangedAttackValue = attack;
        this.defenseValue = defense;

        this.visionRadius = vision;
        this.isInvisible = false;
        this.canFindTargetAnywhere = false;

        inventory = new Inventory(20);

        // these variables determine how long the player or creatures can survive while hungry
        this.maxFood = 1000;
        this.food = maxFood / 4 * 3; // start with 75% food

        this.level = 1;

        // every turn the cooldown drops by 10
        this.regenHpPer1000 = 10;

        this.effects = new ArrayList<Effect>();

    }

    // To implement all the different behaviors of all the different creatures,
    // each creature will have a reference to a CreatureAi and the creatures can let their ai decide what to do.
    public void setCreatureAI(CreatureAI ai)
    {
        this.ai = ai;
    }

    // Since the caves we have so far aren’t all connected, the player can only walk around in the
    // open area he starts in. If change how we build the world to make sure that all open cave
    // floors are connected (as in your assignment) we can remove this, but for now
    // we’ll let creatures dig through the walls.
    public void dig(int wx, int wy, int wz) {
        modifyFood(-10); // causes hunger to increase
        world.dig(wx, wy, wz);
        doAction("dig");
    }

    public boolean hasItem(String itemName)
    {
        for(Item i : inventory.getItems())
        {
            // added a condition to only check the name of the item if the item isn't null (eg. the slot in the inventory is null
            if(i != null && i.name().equals(itemName))
            {
                return true;
            }
        }
        return false;
    }

    // called by playerAI, it checks if the creature has the required keys and if so calls the world openLockedDoor method and gets rid of the keys
    public void openLockedDoorOfChamber(int wx, int wy, int wz)
    {
        if(hasItem("yellow key") && hasItem("blue key") && hasItem("green key"))
        {
            List<Item> keysForChamber = new ArrayList<>();
            // gets the keys required from the inventory
            keysForChamber.add(inventory.get("yellow key"));
            keysForChamber.add(inventory.get("blue key"));
            keysForChamber.add(inventory.get("green key"));

            world.openLockedDoor(wx, wy, wz);
            notify("You opened the chamber containing the Battle Teddy of Destiny!");
            for (Item i : keysForChamber)
            {
                getRidOf(i);
            }
            doAction("discard the yellow, blue, and green keys");
        }
        else {
            doAction("need the yellow, blue, and green keys to unlock the Chamber of Power");
        }
    }

    // creatures will also move around in the world. What
    // happens when they try to enter a new tile is up to the creature’s ai
    public void moveBy(int mx, int my, int mz)
    {
        // It should bail out early if we’re not actually moving. This will take care of creatures killing themselves
        // when all they want to do is stand in one place
        if (mx==0 && my==0 && mz==0)
            return;

        Tile tile = world.tile(x+mx, y+my, z+mz);

        if (mz == -1)
        {
            if (tile == Tile.STAIRS_DOWN)
            {
                doAction("walk up the stairs to level %d", z+mz +1);
            }
            else
            {
                doAction("try to go up but are stopped by the cave ceiling");
                return;
            }
        }
        else if (mz == 1)
        {
            if (tile == Tile.STAIRS_UP)
            {
                doAction("walk down the stairs to level %d", z+mz +1);
            }
            else
            {
                doAction("try to go down but are stopped by the cave floor");
                return;
            }
        }

        // gets the creature at the tile a creature is trying to move into
        Creature other = world.creature(x+mx, y+my, z+mz);
        // if no other creature is present at the tile, the creature calls the ai's onEnter method
        if (other == null)
            ai.onEnter(x+mx, y+my, z+mz,tile);
        // if another creature is present, the creature will attack the other creature
        else
            attack(other);
    }

    // the attack method for creatures
    public void attack(Creature other)
    {
        int amount = Math.max(0, attackValue() - other.defenseValue()); // returns the max value from the 2 given arguments
        amount = (int)(Math.random() * amount) + 1;
        other.modifyHp(-amount);
        // notifies the creature whenever it does something interesting or something happens it (non-players will ignore this)
        // call doAction to pass a message of the event
        doAction("attack the '%s' for %d damage", other.name, amount); // %s is a placeholder for a string (other.name, %d is a placeholder for decimal (amount)

        // grants exp when a creature is killed
        if (other.hp < 1)
            gainXp(other);

    }

    // increase the creature's exp
    // This ensures that tougher creatures are worth more to kill and by subtracting the killer’s level,
    // easy creatures will soon be worth nothing.
    public void gainXp(Creature other)
    {
        int amount = other.maxHp
                + other.attackValue()
                + other.defenseValue()
                - level * 2;
        if (amount > 0)
            modifyXp(amount);
    }

    // modifies the creatures health - calls the world method to remove them if health is less than 1
    public void modifyHp(int amount)
    {
        hp += amount;
        if (hp < 1) {
            // call doAction to pass a message of the event
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }

    // when a creature dies, it leaves it drops all of it items
    private void leaveCorpse()
    {
        Item corpse = new Item('%', color, name + " corpse");
        corpse.modifyFoodValue(maxHp);
        world.addAtEmptySpace(corpse, x, y, z);
        for (Item item : inventory.getItems()){
            if (item != null)
                drop(item);
        }
    }

    public void modifyFood(int amount) {
        food += amount;
        if (food > maxFood) {
            // maxFood increases if you eat more than maxFood
            maxFood = maxFood + food / 2;
            food = maxFood;
            notify("You can't believe your stomach can hold that much!");
            modifyHp(-1);
        } else if (food < 1 && isPlayer()) {
            modifyHp(-1000);
        }
    }

    public boolean isPlayer(){
        return glyph == '@';
    }


    // the method for checking if the tile a creature wants to enter is open
    public boolean canEnter(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz).isGround() && world.creature(wx, wy,wz) == null;
    }

    // the player ai will be the receiver of messages. Creature calls it and will be the source of most messages
    // messages will be passed to the player using the creature ai onNotify method and non-player ai can ignore it
    // this uses varargs (variable-length arguments). It allows any number of extra arguments to be passed without explicitly creating an array
    // java automatically packs the extra arguments into an array
    // everything in java can be treated as an object so it can accept any type
    public void notify(String message, Object ... params){
        ai.onNotify(String.format(message, params));
    }

    // notifying nearby creatures when something happens
    public void doAction(String message, Object ... params)
    {
        // the for loop loops over a square area centered on the creature - r = 9 is for 9 tiles
        int r = 9;
        for (int ox = -r; ox < r+1; ox++)
        {
            for (int oy = -r; oy < r+1; oy++)
            {
                // trims the square into a circle using distance math
                // only keeps tiles within radius r
                if (ox*ox + oy*oy > r*r)
                    continue;
                Creature other = world.creature(x+ox, y+oy,z); // gets any creature at each location around the creature
                if (other == null)
                    continue;
                // If the message is for yourself
                if (other == this)
                    other.notify("You " + message + ".", params);
                // Other creatures see a message in third-person
                else if (other.canSee(x,y,z)) // only notify others if they can see the one doing the action.
                    // Now you have to actually see something happen in order to be notified about it.
                    // calls the makeSecondPerson method and passes in the message and params to make it grammatically correct.
                    other.notify(String.format("The '%s' %s.", name, makeSecondPerson(message)), params);
            }
        }
    }

    // does a small bit of string manipulation to make it grammatically correct. It assumes the first word is the verb, but that’s easy enough to do
    // as long as you don’t plan on supporting other languages. It’s best to avoid implicit rules
    // like this since the only way to know about it is to already know it or watch it fail when you
    // don’t follow the implicit rule. It feels dirty to have grammar rules in with the Creature code, but it’ll do for now.
    private String makeSecondPerson(String text)
    {
        String[] words = text.split(" "); // splits the sentence by spaces and turns it into an array
        words[0] = words[0] + "s"; // takes the first word and adds an "s" to the end of it

        // normal strings are immutable. Once created, they cannot be changed. Adding a letter to an existing normal string actually makes a
        // new string and makes the original variable point to it
        // This creates a mutable string. A mutable string can be modified without creating a new object
        StringBuilder builder = new StringBuilder();
        // rebuilds the sentence by looping through each word and appending a space and then the current word to the builder variable for holding the sentence
        for (String word : words)
        {
            builder.append(" ");
            builder.append(word);
        }
        return builder.toString().trim(); // removes trailing spaces - like the one at the front made by the for loop above
    }

    // method for looking at the world
    public boolean canSee(int wx, int wy, int wz)
    {
        return ai.canSee(wx, wy, wz);
    }


    /**** returns only what the creature can see ****/
    public Tile realTile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }

    // method for looking at the world - returns the tile that the creature can see at a specific location or if not, what they remember was at that location
    public Tile tile(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.tile(wx, wy, wz);
        else
            return ai.rememberedTile(wx, wy, wz);
    }

    // lets creatures see other creatures so the creature ai knows what's going on
    public Creature creature(int wx, int wy, int wz) {
        return world.creature(wx, wy, wz);
    }

    public Item item(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.item(wx, wy, wz);
        else
            return null;
    }

    // lets creatures pickup items, moving them from the world to their inventory
    public void pickup()
    {
        Item item = world.item(x, y, z);
        if (inventory.isFull() || item == null){
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", item.name());
            world.remove(x, y, z);
            inventory.add(item);
        }
    }

    // allows the creature to throw an item to a location
    public void throwItem(Item item, int wx, int wy, int wz) {
        Point end = new Point(x, y, wz);
        for (Point p : new Line(x, y, wx, wy)){
            if (!realTile(p.x, p.y, z).isGround() && !item.isDynamite())
                break;
            end = p;
        }
        wx = end.x;
        wy = end.y;
        Creature c = creature(wx, wy, wz);

        // throw dynamite
        if(item.isDynamite())
        {
            applyDynamiteDamage(item, wx, wy, wz);
            getRidOf(item);
            return;
        }
        // throw water
        if(item.isJugOfWater())
        {
            applyWaterJugSplashEffect(item, wx, wy, wz);
            getRidOf(item);
            return;
        }
        if (c != null)
            throwAttack(item, c);
        else
            doAction("throw a %s", item.name());
        // removes the item if it has a quaffEffect and the
        // target was a Creature
        if (item.quaffEffect() != null && c != null)
            getRidOf(item);
        else
            putAt(item, wx, wy, wz);

    }

    // drink
    public void quaff(Item item){
        doAction("quaff a " + item.name());
        consume(item);
    }
    // eat
    public void eat(Item item){
        doAction("eat a " + item.name());
        consume(item);
    }
    // drink and eat are similar so some of their functionality is shared through consume
    private void consume(Item item){
        if (item.foodValue() < 0)
            notify("Gross!");
            addEffect(item.quaffEffect());
            modifyFood(item.foodValue());
            getRidOf(item);
    }

    public void addEffect(Effect effect){
        if (effect == null)
            return;
        effect.start(this);
        effects.add(effect);
    }

    // method to update the effects each turn and remove any that are done.
    private void updateEffects(){
        List<Effect> done = new ArrayList<Effect>();
        for (Effect effect : effects){
            effect.update(this);
            if (effect.isDone()) {
                effect.end(this);
                done.add(effect);
            }
        }
        effects.removeAll(done);
    }

    // lets creatures drop items, moving them from their inventory to the world - only if addAtEmptySpace() finds a free space to drop the item
    public void drop(Item item)
    {
        if (world.addAtEmptySpace(item, x, y, z)){
            doAction("drop a " + item.name());
            inventory.remove(item);
            unequip(item); // unequip what we drop
        } else {
            notify("There's nowhere to drop the %s.", item.name());
        }
    }

    // used by visionRadius() and World's color() and glyph methods
    public boolean hasTorchEquipped()
    {
        return weapon != null && weapon.isTorch();
    }

    // checks if the creature has a shovel equipped
    public boolean hasShovelEquipped()
    {
        return weapon != null && weapon.isShovel();
    }

    /******** handling armour and weapons *********/
    public void unequip(Item item)
    {
        if (item == null)
            return;
        if (item == armor){
            doAction("remove a " + item.name());
            armor = null;
        } else if (item == weapon) {
            doAction("put away a " + item.name());
            weapon = null;
        }
    }

    // make sure anything equipped is added to the
    // inventory if it isn’t already there
    public void equip(Item item){
        if (!inventory.contains(item)) {
            if (inventory.isFull()) {
                notify("Can't equip %s since you're holding too much stuff.", item.name());
                return;
            } else {
                world.remove(item);
                inventory.add(item);
            }
        }
        if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0)
            return;
        if (item.attackValue() + item.rangedAttackValue() >= item.defenseValue()){
            unequip(weapon);
            doAction("wield a " + item.name());
            weapon = item;
        } else {
            unequip(armor);
            doAction("put on a " + item.name());
            armor = item;
        }
    }

    public void modifyXp(int amount) {
        xp += amount;
        notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);
        while (xp > (int)(Math.pow(level, 1.5) * 20)) {
            level++;
            doAction("advance to level %d", level);
            ai.onGainLevel();
            modifyHp(level * 2);
            // prevents hp from going higher than maxHp which leads to infinite health regen
            if(hp > maxHp)
            {
                hp = maxHp;
            }
        }
    }

    /***** Levelling up stats ********/
    public void gainMaxHp() {
        maxHp += 10;
        hp += 10;
        doAction("look healthier");
    }
    public void gainAttackValue() {
        attackValue += 2;
        doAction("look stronger");
    }

    public void gainThrowAttackValue() {
        attackValue += 2;
        doAction("look stronger at throwing");
    }

    public void gainRangedAttackValue() {
        attackValue += 2;
        doAction("look more adept with ranged weapons");
    }

    public void gainDefenseValue() {
        defenseValue += 2;
        doAction("look tougher");
    }
    public void gainVision() {
        visionRadius += 1;
        doAction("look more aware");
    }

    // used by the LookScreen
    public String details() {
        return String.format(" level:%d attack:%d defense:%d hp:%d", level, attackValue(), defenseValue(), hp);
    }

    // applies attack damage to creatures from thrown weapons. Adds half the base attack value of the
    // thrower since thrown weapons should generally do less damage than melee weapons.
    private void throwAttack(Item item, Creature other) {
        commonAttack(other, throwAttackValue + item.thrownAttackValue(), "throw a %s at the %s for %d damage", item.name(), other.name);
        // throwing a potion at a creature causes the effect to apply to it
        other.addEffect(item.quaffEffect());
    }

    private void dynamiteAttack(Item item, Creature other) {
        commonAttack(other, throwAttackValue * 2 * item.thrownAttackValue(), "throw a %s at the %s for %d damage", item.name(), other.name);
        // throwing a potion at a creature causes the effect to apply to it
        other.addEffect(item.quaffEffect());
    }

    public void applyDynamiteDamage(Item item, int wx, int wy, int wz){
        int r = item.blastRadiusValue();
        // checks a square area around the player
        // using <= makes the explosion even on each side
        for (int x = -r; x <= r; x++){
            for (int y = -r; y <= r; y++)
            {
                // trims the square area of vision into a circle based the vision radius
                if (x*x + y*y > r*r)
                    continue;
                // skip tiles that are out of bounds
                if (wx + x < 0 || wx + x >= world.width()
                        || wy + y < 0 || wy + y >= world.height())
                    continue;

                // add the current x and y to the wx values to get the p current point values for the tile to target in the blast
                int px = wx + x;
                int py = wy + y;

                world.explode(px,py,wz);
                Creature c = creature(px,py,wz);
                if(c != null)
                    dynamiteAttack(item,c);
            }
        }
        getRidOf(item);
    }

    public void applyWaterJugSplashEffect(Item item, int wx, int wy, int wz){
        int r = item.blastRadiusValue();
        // checks a square area around the player
        // using <= makes the explosion even on each side
        for (int x = -r; x <= r; x++){
            for (int y = -r; y <= r; y++)
            {
                // trims the square area of vision into a circle based the vision radius
                if (x*x + y*y > r*r)
                    continue;
                // skip tiles that are out of bounds
                if (wx + x < 0 || wx + x >= world.width()
                        || wy + y < 0 || wy + y >= world.height())
                    continue;

                // add the current x and y to the wx values to get the p current point values for the tile to target in the blast
                int px = wx + x;
                int py = wy + y;

                world.putOutFire(px,py,wz);
                Creature c = creature(px,py,wz);
                if(c != null)
                    c.addEffect(item.quaffEffect());
            }
        }
        getRidOf(item);
    }

    // Creatures need a way to attack with ranged weapons
    public void rangedWeaponAttack(Creature other){
        commonAttack(other, rangedAttackValue + weapon.rangedAttackValue(), "fire a %s at the %s for %d damage", weapon.name(), other.name);
    }

    // The attack weapons all have similar bodies so we could use an Extract Method refactoring to move the commonalities to a separate method and use Extract Parameter or
    // Parameterize Method to pass in the differences.
    // This particular implementation has the hidden side effect that the message that is passed
    // in must end in a damage indicator since the commonAttack method will add that. It’s
    // not good to do things like this too often, but sometimes that happens.
    private void commonAttack(Creature other, int attack, String action, Object ... params) {
        modifyFood(-2);
        int amount = Math.max(0, attack - other.defenseValue());
        amount = (int)(Math.random() * amount) + 1;
        Object[] params2 = new Object[params.length+1];
        for (int i = 0; i < params.length; i++){
            params2[i] = params[i];
        }
        params2[params2.length - 1] = amount;
        doAction(action, params2);
        other.modifyHp(-amount);
        if (other.hp < 1)
            gainXp(other);
    }

    /** Creatures sometimes use up an item and it no longer exists. Other times they no longer
    // have the item, but it still exists in the world. Either way, we need to make sure they no
    // longer have it equipped and that they no longer have it in their inventory. One way to do
    // this is to create two helper methods and call these when possible.
     **/
    // gets rid of the item from the inventory and the game
    private void getRidOf(Item item){
        inventory.remove(item);
        unequip(item);
    }
    // gets rid of the item from the inventory and puts it at an empty spot in the world
    private void putAt(Item item, int wx, int wy, int wz){
        inventory.remove(item);
        unequip(item);
        world.addAtEmptySpace(item, wx, wy, wz);
    }

    public void modifyRegenHpPer1000(int amount) { regenHpPer1000 += amount; }

    // called by update()
    private void regenerateHealth(){
        regenHpCooldown -= regenHpPer1000;
        if (regenHpCooldown < 0 && hp < maxHp){
            modifyHp(1);
            modifyFood(-1);
            regenHpCooldown += 1000;
        }
    }

    // causes 10 damage to the player and 5 to other creatures
    private void applyFireTileDamage()
    {
        if(world.tile(x,y,z) == Tile.FIRE && !isFlameResistant())
        {
            int amount;
            if(isPlayer())
            {
                amount = 10;
            }
            else {
                amount = 5;
            }
            modifyHp(-amount);
            doAction("got burned by fire");
        }
    }


    // delegates the functionality of creature update to creature ai and decrements the food variable of the creature
    public void update(){
        modifyFood(-1);
        regenerateHealth();
        applyFireTileDamage();
        updateEffects();
        ai.onUpdate();
    }

}

