package controller;

import controller.SpawnController;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class SpawnControllerTest {

    private SpawnController spawnController;
    private HallController hallController;

    @BeforeEach
    void setUp() {
        spawnController = SpawnController.getInstance();
        hallController = new HallController();
    }

    // Behaviour Specification initializeHeroPosition()
    /**
     * Places a Hero in a random valid position within the given Hall and returns its value.
     * If a Hero already exists in the Hall, the existing Hero is returned.
     *
     * Requires:
     * - `hall` must not be null.
     * - `hall.getWidth()` and `hall.getHeight()` must give valid grid boundaries.
     * - `hall.isValidPosition(x, y)` should accurately reflect whether a position is valid for the initialization.
     * - `hall.addGridElement(element, x, y)` should add the Hero to the grid if the position is valid.
     *
     * Modifies:
     * - The `hall` object, by setting a new Hero if no Hero currently exists.
     * - The grid state of the `hall` and its gridElements list, by placing the Hero at a randomly chosen valid position.
     *
     * Effects:
     * - If `hall.getHero()` returns null, a new Hero is created and placed in a random valid position within the Hall.
     * - The new Hero is added to the Hall's grid using `addGridElement`.
     * - The Hero's position on the grid will become occupied (and thus invalid for further placement).
     *
     * Returns:
     * - The newly created Hero if no Hero exists in the Hall.
     * - The existing Hero if one is already present in the Hall.
     */

    @Test
    void testInitializeHeroPositionSuccessful() {
        Hall hall = hallController.getEarthHall();
        Hero hero = spawnController.initializeHeroPosition(hall);

        assertNotNull(hero, "Hero should not be null.");
        assertFalse(hall.isValidPosition(hero.getX(), hero.getY()), "Position of hero should be not valid since hero is initialized there.");
        assertEquals(hero, hall.getHero(), "Hero should be properly set in the hall.");
        Point heroPoint = new Point(hero.getX(), hero.getY());
        assertEquals(hall.getGridElements().get(heroPoint), hero,"Hero should be successfully added in gridElements List");
    }

    @Test
    void testInitializeHeroPositionWhenAlreadyExists() {
        Hall hall = hallController.getEarthHall();

        // First initialization
        Hero firstHero = spawnController.initializeHeroPosition(hall);

        // Second call to initializeHeroPosition should return the same Hero
        Hero secondHero = spawnController.initializeHeroPosition(hall);

        assertNotNull(firstHero, "First Hero should not be null.");
        assertNotNull(secondHero, "Second Hero should not be null.");
        assertSame(firstHero, secondHero, "Both calls should return the same Hero instance.");
    }

    @Test
    void testHeroPositionWithinHallBounds() {
        Hall hall = hallController.getEarthHall();

        Hero hero = spawnController.initializeHeroPosition(hall);

        // Check that hero is within the bounds of the hall
        assertTrue(hero.getX() >= 0 && hero.getX() < hall.getWidth(), "Hero's X position should be within bounds.");
        assertTrue(hero.getY() >= 0 && hero.getY() < hall.getHeight(), "Hero's Y position should be within bounds.");
    }
}