package domain.gameobjects;

import domain.behaviors.Direction;
import domain.behaviors.GridElement;
import domain.behaviors.Moveable;

import java.io.Serializable;

public class Hero implements Moveable, GridElement, Serializable {
    
    private int lives;
    private Inventory inventory;
    private boolean isVisible;
    private int x;
    private int y;
    //private boolean isAlive;
    private boolean isCloaked;
    private boolean isDamaged;
    private String direction = "RIGHT";
    private boolean extraLifeAngel = true;
    private boolean isThrowing = false;

    public boolean isThrowing() {
        return isThrowing;
    }

    public void setThrowing(boolean throwing) {
        isThrowing = throwing;
    }

    public Hero(int x, int y) {
        this.x = x;
        this.y = y;
        this.lives = 3;
        this.inventory = new Inventory();
        this.isVisible = true;
        //this.isAlive = true;
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
        if (this.lives <= 4) {
            this.lives++;
        }
    }
    public int getLives(){return lives;}
    
    public void setLives(int lives){
        this.lives = lives;
    }
    public Inventory getInventory(){return inventory;}

    public void setInventory(Inventory inventory){
        this.inventory = inventory;
    }

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

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isDamaged(){
        return isDamaged;
    }

    public boolean isCloaked(){
        return isCloaked;
    }

    public void setIsDamaged(boolean isDamaged){
        this.isDamaged = isDamaged;
    }

    public void setIsCloaked(boolean isCloaked){
        this.isCloaked = isCloaked;
    }

    public boolean isExtraLifeAngel() {
        return extraLifeAngel;
    }

    public void setExtraLifeAngel(boolean extraLifeAngel) {
        this.extraLifeAngel = extraLifeAngel;
    }
}
