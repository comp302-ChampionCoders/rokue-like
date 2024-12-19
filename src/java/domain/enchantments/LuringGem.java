package domain.enchantments;

import domain.gameobjects.*;
import domain.behaviors.Direction;

public class LuringGem extends Enchantment {
    private int throwX;
    private int throwY;
    private boolean hasThrowLocation;
    private static final long DURATION = 5000;

    public LuringGem() {
        super("Luring Gem");
    }

    public void throwGem(Direction direction, int distance) {
        if (!isActive()) {
            throwX = getX() + direction.getDx() * distance;
            throwY = getY() + direction.getDy() * distance;
            hasThrowLocation = true;
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
            hasThrowLocation = false;
            System.out.println("Luring Gem's effect has ended.");
        }
    }

    public boolean hasThrowLocation() {
        return hasThrowLocation;
    }

    public int getThrowX() {
        return throwX;
    }

    public int getThrowY() {
        return throwY;
    }

    @Override
    public long getEffectDuration() {
        return DURATION;
    }
}