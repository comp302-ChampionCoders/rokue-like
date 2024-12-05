package src.main.java;

import java.util.HashMap;

public class Inventory {
    private HashMap<String, Integer> items;

    public Inventory() {
        items = new HashMap<>();
    }

    // Add an item to the inventory
    public void addItem(String itemName) {
        items.put(itemName, items.getOrDefault(itemName, 0) + 1);
        System.out.println(itemName + " added to inventory. Count: " + items.get(itemName));
    }

    // Remove an item from the inventory
    public boolean useItem(String itemName) {
        if (items.containsKey(itemName) && items.get(itemName) > 0) {
            items.put(itemName, items.get(itemName) - 1);
            System.out.println(itemName + " used. Remaining: " + items.get(itemName));
            if (items.get(itemName) == 0) {
                items.remove(itemName);
            }
            return true;
        } else {
            System.out.println("No " + itemName + " in inventory.");
            return false;
        }
    }

    // Get count of a specific item
    public int getItemCount(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    // Display inventory contents
    public void displayInventory() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            System.out.println("Inventory contents:");
            for (String item : items.keySet()) {
                System.out.println("- " + item + ": " + items.get(item));
            }
        }
    }
}
