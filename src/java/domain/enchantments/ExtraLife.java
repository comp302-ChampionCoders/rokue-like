package domain.enchantments;

import domain.gameobjects.Hero;

public class ExtraLife extends Enchantment {
    private static final long EFFECT_DURATION = 0; // This enchantment has no active duration

    public ExtraLife() {
        super("Extra Life", "src/resources/images/chestheart32x32.png");
    }

    @Override
    public void applyEffect(Hero hero) {

    }

    @Override
    public void removeEffect(Hero hero) {
        // This enchantment does not have an ongoing effect, so nothing to remove
    }

    @Override
    public long getEffectDuration() {
        return EFFECT_DURATION; // No duration as it is a one-time effect
    }

    @Override
    public boolean canBeCollected() {
        // Define logic if needed to control when this enchantment can be collected
        return true; // For now, it can always be collected
    }
}
