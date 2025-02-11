package domain.enchantments;

import domain.behaviors.Direction;
import domain.gameobjects.*;

import java.io.Serializable;

public class LuringGem extends Enchantment implements Serializable {
    private int throwX;
    private int throwY;
    private boolean hasThrowLocation;
    private static final long DURATION = 5000;

    public LuringGem() {
        super("Luring Gem", "/images/lure32x32.png");
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