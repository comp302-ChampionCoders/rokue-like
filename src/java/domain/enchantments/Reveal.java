package domain.enchantments;
import domain.gameobjects.*;

public class Reveal extends Enchantment {
    private static final int DURATION = 10; 

    public Reveal() {
        super("Reveal Enchantment");
    }

    @Override
    public void applyEffect(Hero hero) {
        activate();
        System.out.println("Rune's approximate location is revealed for " + DURATION + " seconds.");
        // Logic to highlight the 4x4 grid area containing the rune
    }

    @Override
    public void removeEffect(Hero hero) {
        deactivate();
        System.out.println("Highlight for the rune's location has disappeared.");
    }
}

