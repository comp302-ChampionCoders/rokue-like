package domain.enchantments;

import domain.gameobjects.*;
import domain.behaviors.Direction;

public class LuringGem extends Enchantment {
    private Point throwLocation;
    private static final long DURATION = 5000;

    public LuringGem() {
        super("Luring Gem");
    }

    public void throwGem(Direction direction, int distance) {
        if (!isActive()) {
            int newX = getX() + direction.getDx() * distance;
            int newY = getY() + direction.getDy() * distance;
            throwLocation = new Point(newX, newY);
            activate();
        }
    }

    @Override
    public void applyEffect(Hero hero) {
        activate();
        System.out.println("Luring Gem thrown. Nearby Fighter Monsters are distracted.");
        
        // Schedule effect removal
        new Thread(() -> {
            try {
                Thread.sleep(DURATION);
                removeEffect(hero);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void removeEffect(Hero hero) {
        if (isActive()) {
            deactivate();
            throwLocation = null;
            System.out.println("Luring Gem's effect has ended.");
        }
    }

    public Point getThrowLocation() {
        return throwLocation;
    }

    @Override
    public long getEffectDuration() {
        return DURATION;
    }
}