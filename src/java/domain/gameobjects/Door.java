package domain.gameobjects;

public class Door {
    private int x;
    private int y;
    private boolean isUnlocked;

    public Door(int x, int y) {
        this.x = x;
        this.y = y;
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
        // public boolean canPass(Hero hero) { // NEEDS PROPER IMPLEMENTATION
        //     // Hero must be adjacent to door and door must be unlocked
        //     return isUnlocked && Math.abs(hero.getX() - getX()) <= 1 && Math.abs(hero.getY() - getY()) <= 1;
        // }
    
}