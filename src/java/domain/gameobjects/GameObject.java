package domain.gameobjects;

public abstract class GameObject {
    protected Point position;
    protected boolean isActive;
    protected String type;
    
    public GameObject(int x, int y, String type) {
        this.position = new Point(x, y);
        this.type = type;
        this.isActive = true;
    }
    
    public int getX() {
        return position.x;
    }
    
    public int getY() {
        return position.y;
    }
    
    public void setPosition(int x, int y) {
        this.position = new Point(x, y);
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