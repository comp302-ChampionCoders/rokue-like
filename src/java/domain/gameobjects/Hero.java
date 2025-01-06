package domain.gameobjects;

import domain.behaviors.Direction;
import domain.behaviors.GridElement;
import domain.behaviors.Moveable;

public class Hero implements Moveable, GridElement {
    
    private int lives;
    private Inventory inventory;
    private boolean isVisible;
    private int x;
    private int y;
    private boolean isAlive;
    
    public Hero(int x, int y) {
        this.x = x;
        this.y = y;
        this.lives = 3;
        this.inventory = new Inventory();
        this.isVisible = true;
        this.isAlive = true;
    }

    @Override
    public boolean move(Direction direction) { // find a way to call the enum inside the moveable
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
        // Basic boundary check - actual implementation will use Hall's validation
        return true;
    }

    @Override
    public void updatePosition(int newX, int newY) {
        setPosition(newX, newY);
    }

    public void toggleVisibility() {
        this.isVisible = !this.isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }   

    private boolean setIsAlive(Boolean bool){
        return bool;
    }

    public void reduceLife(){
        this.lives--;
        if (this.lives <= 0){
            setIsAlive(false);
        }
    }

    public void addLife() {
        if (this.lives != 4) {
            this.lives++;
        }
    }
    public int getLives(){return lives;}
    public Inventory getInventory(){return inventory;}

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
       return y;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
}
