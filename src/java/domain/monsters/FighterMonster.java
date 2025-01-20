package domain.monsters;

import domain.gameobjects.*;
import domain.behaviors.Direction;

public class FighterMonster extends Monster {
    private static final int DETECTION_RANGE = 5;
    private boolean isLured;
    private int lureX, lureY;
    private int moveCounter = 0; // to control movement frequency

    public FighterMonster(int x, int y) {
        super(x, y, "Fighter", DETECTION_RANGE);
        this.isLured = false;
    }

    @Override
    public void performAction(Hero hero, Rune rune) {
        // If luring gem is active, move towards it instead of hero
        if (isLured) {
            moveTowardsPoint(lureX, lureY);
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
            if (moveCounter >= 3) {
                move(getRandomDirection());
                moveCounter = 0; // reset the counter
            }
        }
    }

    public int getLureX() {
        return lureX;
    }

    public void setLureX(int lureX) {
        this.lureX = lureX;
    }

    public int getLureY() {
        return lureY;
    }

    public void setLureY(int lureY) {
        this.lureY = lureY;
    }

    public boolean isLured() {
        return isLured;
    }

    public void setLured(boolean lured) {
        isLured = lured;
    }

    public void moveTowardsPoint(int targetX, int targetY) {
        if (targetX > getX()) move(Direction.RIGHT);
        else if (targetX < getX()) move(Direction.LEFT);
        
        if (targetY > getY()) move(Direction.DOWN);
        else if (targetY < getY()) move(Direction.UP);
    }

    // Method to be called when luring gem is used
    public void setLuringGemLocation(int x, int y) {
        this.lureX = x;
        this.lureY = y;
        this.isLured = true;
    }

    public void clearLuringGemEffect() {
        this.isLured = false;
    }
}