// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

import java.awt.Color;

public class Item
{
    private char glyph;
    public char glyph() { return glyph; }
    private Color color;
    public Color color() { return color; }
    private String name;
    public String name() { return name; }

    private int foodValue;
    public int foodValue() { return foodValue; }
    public void modifyFoodValue(int amount) { foodValue += amount; }

    private int attackValue;
    public int attackValue() { return attackValue; }
    public void modifyAttackValue(int amount) { attackValue += amount; }

    private int defenseValue;
    public int defenseValue() { return defenseValue; }
    public void modifyDefenseValue(int amount) { defenseValue += amount; }

    // represent how much damage is done when an Item is thrown
    private int thrownAttackValue;
    public int thrownAttackValue() { return thrownAttackValue; }
    public void modifyThrownAttackValue(int amount) {thrownAttackValue += amount; }

    // for ranged weapons
    // Anything that has a non zero rangedAttackValue is a ranged weapon. This way a
    // weapon can have separate attack values for melee, thrown, and ranged combat.
    private int rangedAttackValue;
    public int rangedAttackValue() { return rangedAttackValue; }
    public void modifyRangedAttackValue(int amount) {rangedAttackValue += amount; }

    // for drinking potions
    private Effect quaffEffect;
    public Effect quaffEffect() { return quaffEffect; }
    public void setQuaffEffect(Effect effect) { this.quaffEffect = effect; }

    private int blastRadiusValue;
    public int blastRadiusValue() { return blastRadiusValue; }
    public void modifyBlastRadiusValue(int amount) { blastRadiusValue += amount; }

    private int visionIncreaseValue;
    public int visionIncreaseValue() { return visionIncreaseValue; }
    public void modifyVisionIncreaseValue(int amount) { visionIncreaseValue += amount; }


    public Item(char glyph, Color color, String name){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.thrownAttackValue = 1;
    }

    // the ExamineScreen uses this method to display information about items
    public String details()
    {
        String details = "";
        if (attackValue != 0)
            details += " attack:" + attackValue;

        if (thrownAttackValue != 1)
            details += " thrown:" + thrownAttackValue;

        if (defenseValue != 0)
            details += " defense:" + defenseValue;

        if (foodValue != 0)
            details += " food:" + foodValue;
        if(blastRadiusValue != 0)
            details += " blast radius:" + foodValue;

        return details;
    }

    // returns whether the item is a key
    public boolean isKey(){
        return glyph == 'k';
    }
    // returns whether the item is the victory item
    public boolean isVictoryItem(){
        return glyph == 't';
    }

    public boolean isDynamite(){
        return name.equals("dynamite stick") ;
    }

    public boolean isJugOfWater(){
        return name.equals("water jug") ;
    }

    public boolean isTorch(){
        return name.equals("ethereal lamp") ;
    }

    public boolean isShovel(){
        return name.equals("shovel");
    }
}