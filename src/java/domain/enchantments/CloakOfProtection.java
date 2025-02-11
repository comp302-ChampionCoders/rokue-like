package domain.enchantments;

import domain.gameobjects.*;

import java.io.Serializable;

public class CloakOfProtection extends Enchantment implements Serializable {
    private static final int DURATION = 20000; // duration in seconds

    public CloakOfProtection() {
        super("Cloak of Protection", "/images/cloak32x32.png");
    }

    @Override
    public void applyEffect(Hero hero) {
        activate();
        hero.setIsVisible(false);
        hero.setIsCloaked(true);
        System.out.println("Hero is now invisible to Archer Monsters for " + (DURATION/1000) + " seconds.");
        
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
            hero.setIsVisible(true);
            hero.setIsCloaked(false);
            System.out.println("Hero is no longer invisible to Archer Monsters.");
        }
    }

    @Override
    public long getEffectDuration() {
        return DURATION;
    }
}
