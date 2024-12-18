package domain.monsters;

import domain.gameobjects.GameObject;
import domain.gameobjects.Hero;
import domain.behaviors.Moveable;
import domain.behaviors.Direction;

public abstract class Monster extends GameObject implements Moveable {
    protected int detectionRange;

    public Monster(int x, int y, String type, int detectionRange) {
        super(x ,y, type);
        this.detectionRange = detectionRange;
    }

    public abstract void performAction(Hero hero); // Abstract method to be implemented by subclasses

    @Override
    public boolean move(Direction direction) {
        int newX = getX() + direction.getDx();
        int newY = getY() + direction.getDy();
        
        if (isValidMove(newX, newY)) {
            updatePosition(newX, newY);
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidMove(int newX, int newY) {
        // Actual implementation will depend on Hall's validation
        return true;
    }

    @Override
    public void updatePosition(int newX, int newY) {
        setPosition(newX, newY);
    }

    public boolean isInRange(Hero hero) {
        int dx = Math.abs(hero.getX() - getX());
        int dy = Math.abs(hero.getY() - getY());
        return dx <= detectionRange && dy <= detectionRange;
    }

    public boolean isAdjacentToHero(Hero hero) {
        return Math.abs(hero.getX() - getX()) <= 1 && 
               Math.abs(hero.getY() - getY()) <= 1;
    }

    protected void moveTowardsHero(Hero hero) {
        if (hero.getX() > getX()) move(Direction.RIGHT);
        else if (hero.getX() < getX()) move(Direction.LEFT);
        
        if (hero.getY() > getY()) move(Direction.DOWN);
        else if (hero.getY() < getY()) move(Direction.UP);
    }
}
