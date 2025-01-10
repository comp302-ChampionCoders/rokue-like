package controller;

import domain.behaviors.Direction;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveHeroTest {
    /**
     * Requires:
     * - direction is a valid Direction enum value (UP, DOWN, LEFT, RIGHT).
     * - hero and currentHall are properly initialized and non-null.
     *
     * Modifies:
     * - The hero's position on the grid.
     * - The grid state in currentHall.
     *
     * Effects:
     * - Moves the hero in the specified direction if the target position is valid.
     * - Removes the hero's old position from the grid and updates it with the new position.
     * - Prevents movement if the target position is out of bounds or occupied.
     */

    @Test
    void moveHeroUp() {
        HallController hallController = new HallController();
        hallController.updateHero();
        Hero hero = hallController.getHero();

        int initialX = hero.getX();
        int initialY = hero.getY();

        hallController.moveHero(Direction.UP);

        assertEquals(initialX, hero.getX(), "Hero's X coordinate should remain unchanged.");
        assertEquals(initialY - 1, hero.getY(), "Hero's Y coordinate should decrease by 1 when moving up.");
    }

    @Test
    void moveHeroThroughObject() {
        HallController hallController = new HallController();
        hallController.updateHero();
        Hero hero = hallController.getHero();
        Hall currentHall = hallController.getCurrentHall();

        // Place an object at a specific position
        int objectX = hero.getX();
        int objectY = hero.getY() + 1;
        GameObject obstacle = new GameObject(objectX, objectY, null);
        currentHall.addObject(obstacle, objectX, objectY);

        // Attempt to move the hero down into the object's position
        hallController.moveHero(Direction.DOWN);

        // Verify that the hero did not move through the object
        assertEquals(objectX, hero.getX(), "Hero's X coordinate should remain unchanged.");
        assertEquals(objectY - 1, hero.getY(), "Hero's Y coordinate should remain unchanged, just above the object.");
    }

    @Test
    void moveHeroOutOfBounds() {
        HallController hallController = new HallController();
        hallController.updateHero();
        Hero hero = hallController.getHero();
        Hall currentHall = hallController.getCurrentHall();

        // Move hero to the top-left corner
        hero.setPosition(0, 0);
        hallController.moveHero(Direction.UP);

        assertEquals(0, hero.getX(), "Hero's X coordinate should remain unchanged.");
        assertEquals(0, hero.getY(), "Hero's Y coordinate should not go below 0.");

        // Move hero to the bottom-right corner
        hero.setPosition(currentHall.getWidth() - 1, currentHall.getHeight() - 1);
        hallController.moveHero(Direction.DOWN);

        assertEquals(currentHall.getWidth() - 1, hero.getX(), "Hero's X coordinate should remain unchanged.");
        assertEquals(currentHall.getHeight() - 1, hero.getY(), "Hero's Y coordinate should not exceed the hall's height.");
    }
}