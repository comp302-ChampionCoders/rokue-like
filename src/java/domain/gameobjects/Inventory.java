package domain.gameobjects;

import java.util.*;
import domain.behaviors.Collectible;
public class Inventory {
    private HashMap<String, List<Collectible>> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    // Add an item to the inventory
    public void addItem(Collectible item) {
        String type = item.getType();
        items.computeIfAbsent(type, k -> new ArrayList<>()).add(item);
    }

    public boolean useItem(String type) {
        List<Collectible> itemList = items.get(type);
        if (itemList != null && !itemList.isEmpty()) {
            Collectible item = itemList.remove(0); // TODO: needs proper implementation
            if (itemList.isEmpty()) {
                items.remove(type);
            }
            return true;
        }
        return false;
    }

    public int getItemCount(String type) {
        List<Collectible> itemList = items.get(type);
        return itemList != null ? itemList.size() : 0;
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
}
