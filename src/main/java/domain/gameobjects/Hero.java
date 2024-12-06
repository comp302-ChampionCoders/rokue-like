package src.main.java.domain.gameobjects;
public class Hero {
    private int x, y;
    private int lives;
    private Inventory inventory;

    public Hero(int x, int y) {
        this.x = x;
        this.y = y;
        this.lives = 3;
        this.inventory = new Inventory();
    }

    public void move(String direction) {
        switch (direction) {
            case "up": y++; break; // check again maybe its the opposite way
            case "down": y--; break;
            case "left": x--; break;
            case "right": x++; break;
        }
    }

    public void collectItem(String itemName) {
        inventory.addItem(itemName);
    }
    
    public boolean useItem(String itemName) {
        return inventory.useItem(itemName);
    }
    
    public void displayInventory() {
        inventory.displayInventory();
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
