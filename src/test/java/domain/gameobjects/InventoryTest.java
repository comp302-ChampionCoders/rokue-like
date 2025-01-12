package domain.gameobjects;

import domain.enchantments.CloakOfProtection;
import domain.enchantments.LuringGem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {
    /**
     * Requires:
     * - `item` is a valid `Collectible` object.
     * - Inventory is properly initialized and non-null.
     *
     * Modifies:
     * - The internal `items` map of the inventory.
     *
     * Effects:
     * - Adds the given item to the inventory.
     * - Ensures the item count for the type is updated correctly.
     */

    @Test
    void addSingleItem() {
        Inventory inventory = new Inventory();
        CloakOfProtection cloak = new CloakOfProtection();

        inventory.addItem(cloak);

        assertTrue(inventory.hasItem("Cloak of Protection"), "Inventory should contain the added item.");
        assertEquals(1, inventory.getItemCount("Cloak of Protection"), "Item count should be 1 after adding a single item.");
    }

    @Test
    void addMultipleItemsSameType() {
        Inventory inventory = new Inventory();
        CloakOfProtection cloak1 = new CloakOfProtection();
        CloakOfProtection cloak2 = new CloakOfProtection();

        inventory.addItem(cloak1);
        inventory.addItem(cloak2);

        assertTrue(inventory.hasItem("Cloak of Protection"), "Inventory should contain the added items.");
        assertEquals(2, inventory.getItemCount("Cloak of Protection"), "Item count should be 2 after adding two items of the same type.");
    }

    @Test
    void addDifferentItemTypes() {
        Inventory inventory = new Inventory();
        CloakOfProtection cloak = new CloakOfProtection();
        LuringGem gem = new LuringGem();

        inventory.addItem(cloak);
        inventory.addItem(gem);

        assertTrue(inventory.hasItem("Cloak of Protection"), "Inventory should contain the added Cloak of Protection.");
        assertTrue(inventory.hasItem("Luring Gem"), "Inventory should contain the added Luring Gem.");
        assertEquals(1, inventory.getItemCount("Cloak of Protection"), "Item count for Cloak of Protection should be 1.");
        assertEquals(1, inventory.getItemCount("Luring Gem"), "Item count for Luring Gem should be 1.");
    }
}