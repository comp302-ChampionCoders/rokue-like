package src.main.java.domain.gameobjects;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected char symbol;
    protected boolean isInteractable;
    protected String type;

    public GameObject(int x, int y, char symbol, String type) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
        this.type = type;
        this.isInteractable = true;
    }

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

    public char getSymbol() {
        return symbol;
    }

    public String getType() {
        return type;
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public void setInteractable(boolean interactable) {
        this.isInteractable = interactable;
    }

    // Method to handle interactions with the hero
    public abstract void interact(Hero hero);

    // Method to check if this object can be placed at a specific location
    public boolean canBePlacedAt(int x, int y, Hall hall) {
        return hall.isValidPosition(x, y);
    }

    @Override
    public String toString() {
        return type + " at position (" + x + ", " + y + ")";
    }
}