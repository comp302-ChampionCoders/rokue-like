package src.main.java.domain.behaviors;

enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
}

public interface Moveable {
    boolean move(Direction direction);
    boolean isValidMove(int newX, int newY);
    void updatePosition(int x, int y);
}

