package src.main.java;

public class LuringGem extends Enchantment {
    public LuringGem() {
        super("Luring Gem");
    }

    @Override
    public void applyEffect(Hero hero) {
        activate();
        System.out.println("Luring Gem thrown. Nearby Fighter Monsters are distracted.");
        // Logic to make Fighter Monsters chase the gem.
    }

    @Override
    public void removeEffect(Hero hero) {
        deactivate();
        System.out.println("Luring Gem's effect has ended.");
        // Logic to revert back to same movement.
    }
}
