package domain.gameobjects;

public class Door extends GameObject {
    private boolean isUnlocked;

    public Door(int x, int y) {
        super(x, y, 'D', "Door");
        this.isUnlocked = false;
    }

    @Override
    public void interact(Hero hero) {
        if (isUnlocked) {
            System.out.println("Door opened! Proceeding to next hall...");
            // Logic for transitioning to next hall would go here
        } else {
            System.out.println("The door is locked. Find the rune first!");
        }
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void unlock() {
        if (!isUnlocked) {
            isUnlocked = true;
            System.out.println("The door has been unlocked! You can now proceed.");
        }
    }

    @Override
    public boolean canBePlacedAt(int x, int y, Hall hall) {
        // Doors can only be placed on the edges of the hall
        return super.canBePlacedAt(x, y, hall) && 
               (x == 0 || x == hall.getGrid()[0].length - 1 || 
                y == 0 || y == hall.getGrid().length - 1);
    }
}