package domain.gameobjects;

public class Rune extends GameObject {
    private boolean isCollected;

    public Rune(int x, int y) {
        super(x, y, 'R', "Rune");
        this.isCollected = false;
    }

    @Override
    public void interact(Hero hero) {
        if (!isCollected) {
            collect();
            // Notify the hall that rune was collected (this could be done through an observer pattern)
            System.out.println("Rune collected! The door is now unlocked.");
        }
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        if (!isCollected) {
            isCollected = true;
            setInteractable(false);
        }
    }
}
