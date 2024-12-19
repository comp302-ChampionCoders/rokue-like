package domain.gameobjects;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected boolean isActive;
    protected String type;
    
    public GameObject(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isActive = true;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
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
    
    public String getType() {
        return type;
    }
}