package domain.enchantments;

import domain.gameobjects.*;
public class CloakOfProtection extends Enchantment {
    private static final int DURATION = 20; // duration in seconds

    public CloakOfProtection() {
        super("Cloak of Protection");
    }

    @Override
    public void applyEffect(Hero hero) {
        activate();
        // hero.setInvisible(true); // Hero becomes invisible to Archer monsters // #TODO: add isVisible for Hero
        System.out.println("Hero is now invisible to Archer Monsters for " + DURATION + " seconds.");
        // Add timer logic if needed to auto-deactivate after DURATION
    }

    @Override
    public void removeEffect(Hero hero) {
        deactivate();
        // hero.setInvisible(false);
        System.out.println("Hero is no longer invisible to Archer Monsters.");
    }
}
