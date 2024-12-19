package domain.gameobjects;

public class Door extends GameObject {
    private boolean isUnlocked;

    public Door(int x, int y) {
        super(x, y, "Door");
        this.isUnlocked = false;
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
        public boolean canPass(Hero hero) {
            // Hero must be adjacent to door and door must be unlocked
            return isUnlocked && Math.abs(hero.getX() - getX()) <= 1 && Math.abs(hero.getY() - getY()) <= 1;
        }
    
        // Method to validate door placement in build mode
        public boolean isValidPlacement(int x, int y, int width, int height) {
            // Doors can only be placed on the edges of the hall
            return x == 0 || x == width - 1 || y == 0 || y == height - 1;
        }
    
}