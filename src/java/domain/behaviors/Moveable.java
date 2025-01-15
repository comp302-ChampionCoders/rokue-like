package domain.behaviors;

public interface Moveable {
    boolean move(Direction direction);
    boolean isValidMove(int newX, int newY);
    void updatePosition(int x, int y);
}