package controller;

import domain.behaviors.GridElement;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResetToBuildModeTest {

    /**
     * Resets the current hall to build mode by removing non-GameObject elements.
     *
     * Requires:
     * - The `currentHall` must be initialized and not null.
     * - The `currentHall` should have a valid `gridElements` collection.
     *
     * Modifies:
     * - The `gridElements` collection in `currentHall`.
     * - The `hero` reference in `currentHall`.
     *
     * Effects:
     * - Removes all elements in the `gridElements` collection that are not instances of `GameObject`.
     * - Sets the `hero` reference in `currentHall` to null.
     * - Ensures that only `GameObject` instances remain in the `gridElements` collection after the method executes.
     */



    @Test
    void testResetToBuildModeRemovesAllNonGameObjects() {
        HallController hallController = new HallController();

        Hall currentHall = hallController.getCurrentHall();
        currentHall.addGridElement(new Hero(0, 0), 0, 0); // Hero
        currentHall.addGridElement(new Rune(1, 1, currentHall), 1, 1); // Rune

        hallController.resetToBuildModeVersions();

        // Check gridElements list
        for (GridElement gridElement : currentHall.getGridElements().values()) {
            assertTrue(gridElement instanceof GameObject, "Only GameObjects should remain after reset.");
        }
    }

    @Test
    void testResetToBuildModeRemovesHero() {
        HallController hallController = new HallController();

        // Add Hero
        Hall currentHall = hallController.getCurrentHall();
        Hero hero = new Hero(0, 0);
        currentHall.setHero(hero);
        currentHall.addGridElement(hero, 0, 0);

        // Reset
        hallController.resetToBuildModeVersions();

        // Hero should be set to null
        assertNull(currentHall.getHero(), "Hero should be null after reset.");
        assertFalse(currentHall.getGridElements().containsValue(hero), "Hero should be removed from grid elements.");
    }


    @Test
    void testResetToBuildModeRetainsGameObjects() {
        HallController hallController = new HallController();

        Hall currentHall = hallController.getCurrentHall();
        GameObject gameObject = new GameObject(0, 0, null);
        currentHall.addGridElement(gameObject, 0, 0);

        hallController.resetToBuildModeVersions();

        // All GameObjects remain
        assertTrue(currentHall.getGridElements().containsValue(gameObject), "GameObject should remain after reset.");
    }


}