package domain.enchantments;

import domain.gameobjects.*;

import java.io.Serializable;

public class ExtraTime extends Enchantment implements Serializable {
    private static final long EFFECT_DURATION = 0; // This enchantment has no active duration

    public ExtraTime() {
        super("Extra Time", "/images/clock_icon.png");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'canBeCollected'");
    }
}
