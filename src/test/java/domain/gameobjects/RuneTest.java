package domain.gameobjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RuneTest
 *
 * Overview:
 * The Rune class represents a collectible item in a game that can appear on the grid, be collected by a hero, and teleported by a wizard monster.
 * It maintains its position, availability, and collection state.
 *
 * Abstract Function:
 * AF(c) = A Rune object where:
 *   - c.x, c.y are the coordinates of the Rune on the grid.
 *   - c.isAvailable indicates if the Rune can currently be interacted with.
 *   - c.isCollected represents if the Rune has been collected by a hero.
 *
 * Representation Invariant:
 *   - x >= 0
 *   - y >= 0
 *   - If isCollected == true, then isAvailable == false.
 *   - If isAvailable == true, then isCollected == false.
 *
 * repOk():
 * Verifies the representation invariant for the Rune class.
 */
public class RuneTest {

    private Rune rune;

    @BeforeEach
    void setUp() {
        rune = new Rune(5, 5, null); // Hall not required for basic tests
    }

    // repOk method
    private boolean repOk() {
        if (rune.getX() < 0 || rune.getY() < 0) return false;
        if (rune.isCollected() && rune.isAvailable()) return false;
        if (!rune.isCollected() && !rune.isAvailable()) return false;
        return true;
    }

    @Test
    public void testRuneInitialization() {
        // Test if the Rune is initialized correctly
        assertEquals(5, rune.getX(), "Rune X position should be 5.");
        assertEquals(5, rune.getY(), "Rune Y position should be 5.");
        assertTrue(rune.isAvailable(), "Rune should be available after initialization.");
        assertFalse(rune.isCollected(), "Rune should not be collected after initialization.");
        assertTrue(repOk(), "repOk should hold after initialization.");
    }

    @Test
    public void testRuneCollect() {
        // Test collecting the Rune
        Hero hero = new Hero(0, 0); // Mock hero
        rune.collect(hero);
        assertTrue(rune.isCollected(), "Rune should be collected.");
        assertFalse(rune.isAvailable(), "Rune should not be available after being collected.");
        assertTrue(repOk(), "repOk should hold after collecting the Rune.");
    }

    @Test
    public void testRuneUnCollect() {
        // Test uncollecting the Rune
        Hero hero = new Hero(0, 0); // Mock hero
        rune.collect(hero);
        rune.unCollect(hero);
        assertFalse(rune.isCollected(), "Rune should not be collected after unCollect.");
        assertTrue(rune.isAvailable(), "Rune should be available after unCollect.");
        assertTrue(repOk(), "repOk should hold after uncollecting the Rune.");
    }

    @Test
    public void testRuneTeleport() {
        // Test teleporting the Rune
        rune.teleport(8, 9);
        assertEquals(8, rune.getX(), "Rune X position should be updated to 8 after teleport.");
        assertEquals(9, rune.getY(), "Rune Y position should be updated to 9 after teleport.");
        assertTrue(rune.isAvailable(), "Rune should still be available after teleport.");
        assertTrue(repOk(), "repOk should hold after teleporting the Rune.");
    }

}
