package domain.monsters;

import domain.gameobjects.*;
import domain.enchantments.*;

public class WizardMonster extends Monster {

    public WizardMonster(int x, int y) {
        super(x, y, "Wizard");
    }

    @Override
    public void performAction(Hero hero) {
        System.out.println("WizardMonster teleports the rune to a random location!");
        teleportRune();
    }

    public void teleportRune() {
        int newX = (int) (Math.random() * 100); // Assuming a 100x100 grid
        int newY = (int) (Math.random() * 100);
        System.out.println("Rune teleported to: (" + newX + ", " + newY + ")");
    }
}
