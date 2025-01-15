package domain.gameobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class addGridElementTest {
    /**
     * Requires:
     * - x and y are integer values representing grid coordinates.
     * - currentHall is properly initialized and non-null.
     *
     * Modifies:
     * - Nothing.
     *
     * Effects:
     * - Returns true if the position (x, y) is within the grid bounds and not occupied.
     * - Returns false if the position is out of bounds or already occupied.
     */

    @Test
    void testAddGridElementSuccess() {
        Hall hall = new Hall(10, 10, Hall.HallType.EARTH);
        GameObject object = new GameObject(5, 5, null);

        boolean result = hall.addGridElement(object, 5, 5);

        assertTrue(result, "Object should be successfully added to the grid.");
        assertTrue(hall.isPositionOccupied(5, 5), "Position (5,5) should be occupied after adding the object.");
    }

    @Test
    void testAddGridElementOccupied() {
        Hall hall = new Hall(10, 10, Hall.HallType.EARTH);
        GameObject object1 = new GameObject(5, 5, null);
        GameObject object2 = new GameObject(5, 5, null);

        hall.addGridElement(object1, 5, 5);
        boolean result = hall.addGridElement(object2, 5, 5);

        assertFalse(result, "Adding an object to an occupied position should fail.");
    }

    @Test
    void testAddGridElementOutOfBounds() {
        Hall hall = new Hall(10, 10, Hall.HallType.EARTH);
        GameObject object = new GameObject(11, 11, null);

        boolean result = hall.addGridElement(object, 11, 11);

        assertFalse(result, "Adding an object out of grid bounds should fail.");
    }
}