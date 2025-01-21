package domain.monsters;

import domain.behaviors.GridElement;
import domain.enchantments.Lure;
import domain.gameobjects.*;
import domain.behaviors.Direction;
import java.lang.Math;

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
    public void performAction(Hero hero) {
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

    public void moveTowardsPoint(int targetX, int targetY, Hall hall) {
        int horizontal_diff = targetX - getX();
        int vertical_diff = targetY - getY();
        boolean occupiedX = false;
        boolean occupiedY = false;
        if ((getX() + 1 == targetX && getY() == targetY) || (getX() - 1 == targetX && getY() == targetY) || (getX() == targetX && getY() + 1 == targetY) || (getX() == targetX && getY() - 1 == targetY)) {
            setLured(false);
            hall.removeGridElement(targetX, targetY);
        }
        else {
            if (Math.abs(horizontal_diff) >= Math.abs(vertical_diff)) {
                // Attempt to move horizontally if valid
                if (targetX > getX() && hall.isValidPosition(getX() + 1, getY())) {
                    move(Direction.RIGHT);
                } else if (targetX > getX()) {
                    occupiedX = true;
                } else if (targetX < getX() && hall.isValidPosition(getX() - 1, getY())) {
                    move(Direction.LEFT);
                } else {
                    occupiedX = true;
                }
            }
            else {
                // Attempt to move vertically if valid
                if (targetY > getY() && hall.isValidPosition(getX(), getY() + 1)) {
                    move(Direction.DOWN);
                } else if (targetY > getY()) {
                    occupiedY = true;
                } else if (targetY < getY() && hall.isValidPosition(getX(), getY() - 1)) {
                    move(Direction.UP);
                } else {
                    occupiedY = true;
                }
            }
            if (occupiedX) {
                // Attempt to move vertically if valid
                if (targetY > getY() && hall.isValidPosition(getX(), getY() + 1)) {
                    move(Direction.DOWN);
                } else if (targetY > getY()) {
                    move(Direction.UP);
                } else if (targetY < getY() && hall.isValidPosition(getX(), getY() - 1)) {
                    move(Direction.UP);
                } else {
                    move(Direction.DOWN);
                }
            }
            if (occupiedY) {
                // Attempt to move horizontally if valid
                if (targetX > getX() && hall.isValidPosition(getX() + 1, getY())) {
                    move(Direction.RIGHT);
                } else if (targetX > getX() ) {
                    move(Direction.LEFT);
                } else if (targetX < getX() && hall.isValidPosition(getX() - 1, getY())) {
                    move(Direction.LEFT);
                } else {
                    move(Direction.RIGHT);
                }
            }
        }
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