package domain.gameobjects;

import domain.behaviors.Direction;
import domain.behaviors.Moveable;

public class Hero extends GameObject implements Moveable {
    
    private int lives;
    private Inventory inventory;
    private boolean isVisible;
    
    public Hero(int x, int y) {
        super(x, y, "Hero");
        this.lives = 3;
        this.inventory = new Inventory();
        this.isVisible = true;
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

    public void reduceLife(){
        this.lives--;
        if (this.lives <= 0){
            setActive(false);
        }
    }
    public int getLives(){return lives;}
    public Inventory getInventory(){return inventory;}
    
}
