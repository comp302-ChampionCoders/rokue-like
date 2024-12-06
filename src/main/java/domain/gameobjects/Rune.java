package src.main.java.domain.gameobjects;

public class Rune {
    private int x; 
    private int y; 
    private boolean isCollected;

    public Rune(int[] position) {
        this.x = position[0];
        this.y = position[1];
        this.isCollected = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() { // this method should revoke door.unlock()
        if (!isCollected) {
            isCollected = true;
            System.out.println("Rune collected! The door is now unlocked.");
        }
    }
}
