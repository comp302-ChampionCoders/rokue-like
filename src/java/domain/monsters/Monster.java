package domain.monsters;

import domain.behaviors.GridElement;
import domain.gameobjects.Hero;
import domain.behaviors.Moveable;
import domain.behaviors.Direction;

public abstract class Monster implements Moveable, GridElement {
    protected int x, y;
    protected int detectionRange;
    protected boolean isActive;
    protected String type;

    public Monster(int x, int y, String type, int detectionRange) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.detectionRange = detectionRange;
        this.isActive = true;
    }

    public abstract void performAction(Hero hero); // Abstract method to be implemented by subclasse
    
    // GridElement implementation
    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

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
    protected Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[(int)(Math.random() * directions.length)];
    }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public String getType() { return type; }
}