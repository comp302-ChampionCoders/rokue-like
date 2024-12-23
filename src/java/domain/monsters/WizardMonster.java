package domain.monsters;

import domain.behaviors.Direction;
import domain.gameobjects.Hero;

public class WizardMonster extends Monster {
    private static final int TELEPORT_RANGE = 0; // Wizard monsters don't move
    private long lastTeleportTime;
    private static final long TELEPORT_COOLDOWN = 5000; // 5 seconds in milliseconds

    public WizardMonster(int x, int y) {
        super(x, y, "Wizard", TELEPORT_RANGE);
        this.lastTeleportTime = System.currentTimeMillis();
    }

    @Override
    public void performAction(Hero hero) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime >= TELEPORT_COOLDOWN) {
            teleportRune();
            lastTeleportTime = currentTime;
        }
    }

    private void teleportRune() {
        // Example logic for teleporting the rune (replace with actual implementation)
        int runeX = (int) (Math.random() * 11); // Assume 11 is the grid width
        int runeY = (int) (Math.random() * 9);  // Assume 9 is the grid height

        System.out.println("Rune teleported to: (" + runeX + ", " + runeY + ")");
        // You can notify the relevant game class or update the Hall object here
    }

    @Override
    public boolean move(Direction direction) {
        // Wizard monsters don't move
        return false;
    }

    @Override
    public void moveTowardsHero(Hero hero) {
        // Wizard monsters don't move
    }
}
