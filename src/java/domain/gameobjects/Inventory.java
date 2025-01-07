package domain.gameobjects;

import java.util.*;
import domain.behaviors.Collectible;
public class Inventory {
    private HashMap<String, List<Collectible>> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public void addItem(Collectible item) {
        String type = item.getType();
        items.computeIfAbsent(type, k -> new ArrayList<>()).add(item);
    }

    public boolean contains(String type) {
        return items.containsKey(type) && !items.get(type).isEmpty();
    }

    public boolean hasCloakOfProtection() {
        return contains("Cloak of Protection");
    }

    public boolean hasReveal() {
        return contains("Reveal");
    }

    public boolean hasLuringGem() {
        return contains("Luring Gem");
    }
    
    public Collectible useItem(String type) {
        if (contains(type)) { // Check if the inventory contains the item
            List<Collectible> itemList = items.get(type);
            Collectible itemToUse = null;
    
            // Find the item by type
            for (Collectible item : itemList) {
                if (item.getType().equals(type)) { // Ensure the type matches
                    itemToUse = item;
                    break;
                }
            }
    
            // Remove the found item and update the inventory
            if (itemToUse != null) {
                itemList.remove(itemToUse); // Remove the specific item
                if (itemList.isEmpty()) {
                    items.remove(type); // Remove the type if the list is now empty
                }
                return itemToUse; // Return the used item
            }
        }
        return null; // Return null if no item was found
    }

    public List<Collectible> getItems(String type) {
        return items.getOrDefault(type, new ArrayList<>());
    }

    public Map<String, Integer> getInventoryContents() {
        Map<String, Integer> contents = new HashMap<>();
        items.forEach((type, list) -> contents.put(type, list.size()));
        return contents;
    }

    public boolean hasItem(String type) {
        return items.containsKey(type) && !items.get(type).isEmpty();
    }
    
        // public int getItemCount(String type) { // could be useful if we later on need to limit enchantments.
    //     List<Collectible> itemList = items.get(type);
    //     return itemList != null ? itemList.size() : 0;
    // }
}
