package domain.enchantments;

import domain.gameobjects.*;

public class ExtraTime extends Enchantment {
    private static final long EFFECT_DURATION = 0; // This enchantment has no active duration
    private static final int EXTRA_TIME_AMOUNT = 5; // Adds 5 seconds to the game timer

    public ExtraTime() {
        super("Extra Time", "src/resources/images/clock_icon.png");
    }

    @Override
    public void applyEffect(Hero hero) {
        // Assuming there is a method in the game to add time
        System.out.println("Extra time collected! Adding " + EXTRA_TIME_AMOUNT + " seconds to the timer.");
        // You can integrate this with your game timer logic, e.g., TimerController or GameScreen
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
