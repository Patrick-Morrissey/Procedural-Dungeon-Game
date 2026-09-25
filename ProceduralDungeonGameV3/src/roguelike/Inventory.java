// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

// Now that we’ve got a world with items in it, we need to be able to pick them up and do
// stuff with them. A lot can happen with a creature’s inventory so let’s create another class
// for that. Instead of using a list we’ll use an array so the items index doesn’t change when
// we lose something before it. E.g. if we quaff the potion in our ‘d’ slot, whatever was in the
// ‘e’ slot should remain there and not slide into the ‘d’ slot. Also add methods to add an item to the
// first open slot in our inventory, remove an item and check if the inventory is full and we
// can’t carry any more.

public class Inventory
{
    private Item[] items;
    public Item[] getItems() { return items; }
    public Item get(int i) { return items[i]; }


    public Inventory(int max)
    {
        items = new Item[max];
    }
    public void add(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == null){
                items[i] = item;
                break;
            }
        }
    }
    public void remove(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == item){
                items[i] = null;
                return;
            }
        }
    }

    public Item get(String itemName)
    {
        for (Item i : items){
            // updated to have a null check to prevent null pointer exceptions
            if (i != null && i.name().equals(itemName))
                return i;
        }

        return null;
    }

    public boolean isFull(){
        int size = 0;
        for (int i = 0; i < items.length; i++){
            if (items[i] != null)
                size++;
        }
        return size == items.length;
    }

    // checks if a particular item is in inventory
    public boolean contains(Item item) {
        for (Item i : items){
            if (i == item)
                return true;
        }
        return false;
    }

}