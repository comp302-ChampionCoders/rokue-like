package domain.monsters;

import domain.gameobjects.*;
import domain.behaviors.Direction;

public class WizardMonster extends Monster {
    private static final int TELEPORT_RANGE = 0; // Doesn't move
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
            System.out.println("WizardMonster teleports the rune!");
            teleportRune();
            lastTeleportTime = currentTime;
        }
    }

    public void teleportRune() {
        // This method should be implemented by the Hall class
        // The actual rune teleportation logic will be handled there
        // This is just a notification that it's time to teleport
        System.out.println("Rune teleportation triggered");
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