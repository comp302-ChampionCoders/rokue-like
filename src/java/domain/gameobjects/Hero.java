package domain.gameobjects;

import domain.behaviors.Moveable;

public class Hero implements Moveable {
    private int x, y;
    private int lives;
    private Inventory inventory;
    private boolean isVisible;
    
    public Hero(int x, int y) {
        this.x = x;
        this.y = y;
        this.lives = 3;
        this.inventory = new Inventory();
        this.isVisible = true;
    }

    @Override
    public boolean move(Direction direction) { // find a way to call the enum inside the moveable
        int newX = x + direction.getDx();
        int newY = y + direction.getDy();
        
        if (isValidMove(newX, newY)) {
            updatePosition(newX, newY);
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidMove(int newX, int newY) {
        // Basic boundary check - actual implementation will use Hall's validation
        return true;
    }

    @Override
    public void updatePosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void toggleVisibility() {
        this.isVisible = !this.isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }   

    // Getters and setters for x, y, lives
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public void reduceLife(){
        this.lives -= 1;
    }
    
}
