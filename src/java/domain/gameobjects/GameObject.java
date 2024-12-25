package domain.gameobjects;

import java.awt.*;
import domain.behaviors.GridElement;

public class GameObject implements GridElement {
    protected int x;
    protected int y;
    protected boolean isActive;
    private Image image;
    
    public GameObject(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.isActive = true;
    }

    @Override
    public int getX() {
        return x;
    }

    public Image getImage(){
        return image;
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
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    

}