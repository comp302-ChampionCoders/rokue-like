package controller;

import domain.gameobjects.Hall;
import domain.gameobjects.GameObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinObjectControllerTest {

    private MinObjectController minObjectController;
    private HallController hallController;

    @BeforeEach
    void setUp() {
        hallController = new HallController();
        minObjectController = new MinObjectController(hallController);
    }

    // Behaviour Specification areAllMinimumRequirementsMet()
    /**
     * Checks whether all halls meet the minimum object requirements.
     *
     * Requires:
     * - `hallController` must be properly set up to initialize 4 empty but non-null halls.
     * - Each `Hall` in the `hallController` must have a valid `HallType` and a defined minimum object count (`getMinObjects()`).
     * - The `getObjects()` method for each `Hall` must return a non-null map of objects currently in the hall.
     *
     * Modifies:
     * - Does not modify any state.
     *
     * Effects:
     * - For each hall, checks if the number of objects present in the hall meets or exceeds the required minimum object count.
     * - Returns `true` if all halls meet their minimum requirements; otherwise, returns `false`.
     * - Prints a message to the console for each hall that does not meet the minimum requirements.
     */
    @Test
    void noneOfTheHallRequirementsMet() {
        hallController.resetHalls();
        boolean result = minObjectController.areAllMinimumRequirementsMet();

        assertFalse(result, "Should be false since all halls are initialized without any objects.");
    }

    @Test
    void allRequirementsMet() {
        //Add required amount of objects to each hall
        for (Hall hall : hallController.getHalls()) {
            int minObjects = hall.getHallType().getMinObjects();
            for (int i = 0; i < minObjects; i++) {
                boolean added = false;
                if(i < 16){
                    added = hall.addObject(new MockGameObject(i , 0), i, 0);
                }
                else{
                    added = hall.addObject(new MockGameObject(i - 16, 1), i - 16,1);
                }
                assertTrue(added, "Object not added, position might be wrong");
            }
        }
        boolean result = minObjectController.areAllMinimumRequirementsMet();

        assertTrue(result, "Result should be true when all requirements are met.");
    }

    @Test
    void oneHallOrPartialRequirementsMet() {
        Hall hall1 = hallController.getEarthHall();

        // Earth Hall meets requirements
        for (int i = 0; i < hall1.getHallType().getMinObjects(); i++) {
            hall1.addObject(new MockGameObject(i, 0), i, 0);
        }

        boolean result = minObjectController.areAllMinimumRequirementsMet();

        assertFalse(result, "Not all criteria are met, result should be false.");
    }

    // Mock GameObject class
    static class MockGameObject extends GameObject {
        public MockGameObject(int x, int y) {
            super(x, y, null, null);
        }
    }
}
