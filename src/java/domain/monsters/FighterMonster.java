package domain.monsters;

import domain.gameobjects.*;
import domain.behaviors.Direction;

public class FighterMonster extends Monster {
    private static final int DETECTION_RANGE = 5;
    private Point luringGemLocation = null;
    private int moveCounter = 0; // to control movement frequency

    public FighterMonster(int x, int y) {
        super(x, y, "Fighter", DETECTION_RANGE);
    }

    @Override
    public void performAction(Hero hero) {
        // If luring gem is active, move towards it instead of hero
        if (luringGemLocation != null) {
            moveTowardsPoint(luringGemLocation.x, luringGemLocation.y);
            return;
        }

        // Fighter only moves if in detection range
        if (isInRange(hero)) {
            if (isAdjacentToHero(hero)) {
                System.out.println("FighterMonster stabs the hero!");
                hero.reduceLife();
            } else {
                moveTowardsHero(hero);
            }
        } else {
            // random movement
            moveCounter++;
            if (moveCounter >= 3){
                move(getRandomDirection());
                moveCounter = 0; // reset the counter
            }
        }
    }

    private void moveTowardsPoint(int targetX, int targetY) {
        if (targetX > getX()) move(Direction.RIGHT);
        else if (targetX < getX()) move(Direction.LEFT);
        
        if (targetY > getY()) move(Direction.DOWN);
        else if (targetY < getY()) move(Direction.UP);
    }

    // Method to be called when luring gem is used
    public void setLuringGemLocation(int x, int y) {
        this.luringGemLocation = new Point(x, y);
    }

    public void clearLuringGemEffect() {
        this.luringGemLocation = null;
    }
}