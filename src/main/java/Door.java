package src.main.java;

public class Door {
    private int x; 
    private int y; 
    private boolean isUnlocked; 

    public Door(int[] position) {
        this.x = position[0];
        this.y = position[1];
        this.isUnlocked = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void unlock() { // rune claimed must trigger this method, #TODO: add sound implementation 
        if (!isUnlocked) {
            isUnlocked = true;
            System.out.println("The door has been unlocked! You can now proceed.");
        }
    }
}
