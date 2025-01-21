package domain.monsters;

import controller.wizardStrategy.WizardBehavior;
import controller.wizardStrategy.ChallengingBehavior;
import controller.wizardStrategy.HelpingBehavior;
import controller.wizardStrategy.IndecisiveBehavior;
import controller.TimerController;
import domain.behaviors.Direction;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;

import domain.gameobjects.Rune;


import java.io.Serializable;

public class WizardMonster extends Monster implements Serializable {
    private static final int TELEPORT_RANGE = 0; // Wizard monsters don't move
    private long lastTeleportTime;
    private static final long TELEPORT_COOLDOWN = 5000; // 5 seconds in milliseconds
    private WizardBehavior behavior;

    public WizardMonster(int x, int y) {
        super(x, y, "Wizard", TELEPORT_RANGE);
        this.lastTeleportTime = System.currentTimeMillis();
    }

//    @Override
//    public void performAction(Hero hero) {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastTeleportTime >= TELEPORT_COOLDOWN) {
//            teleportRune();
//            lastTeleportTime = currentTime;
//        }
//    }

    @Override
    public void performAction(Hero hero) {
        double remainingTimePercentage = calculateRemainingTimePercentage();
        updateBehavior(remainingTimePercentage);

        // Determine state based on time percentage
        int stateId;
        if (remainingTimePercentage > 70) {
            stateId = WizardBehavior.CHALLENGING_STATE;
        } else if (remainingTimePercentage < 30) {
            stateId = WizardBehavior.HELPING_STATE;
        } else {
            stateId = WizardBehavior.INDECISIVE_STATE;
        }

        behavior.execute(stateId, this, hero);
    }


    private void teleportRune() {
        // Example logic for teleporting the rune (replace with actual implementation)
        int runeX = (int) (Math.random() * 11); // Assume 11 is the grid width
        int runeY = (int) (Math.random() * 9);  // Assume 9 is the grid height

        System.out.println("Rune teleported to: (" + runeX + ", " + runeY + ")");
        // You can notify the relevant game class or update the Hall object here
    }

    public void setBehavior(double remainingTimePercentage) {
        if (remainingTimePercentage > 70) {
            behavior = new ChallengingBehavior();
        } else if (remainingTimePercentage < 30) {
            behavior = new HelpingBehavior();
        } else {
            behavior = new IndecisiveBehavior();
        }
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

    protected double calculateRemainingTimePercentage() {
        TimerController timerController = TimerController.getInstance();

        // Get default total game time
        int totalGameTime = 100;
        // int totalGameTime = TimerController.DEFAULT_GAME_TIME;

        // Get remaining time - hardcoded for now, should be retrieved from TimerController
        int remainingTime = 10;  // This should be replaced with actual timer value

        // Calculate and return the percentage
        return (remainingTime / (double) totalGameTime) * 100;
    }

    private void updateBehavior(double remainingTimePercentage) {
        if (remainingTimePercentage > 70) {
            behavior = new ChallengingBehavior();
        } else if (remainingTimePercentage < 30) {
            behavior = new HelpingBehavior();
        } else {
            behavior = new IndecisiveBehavior();
        }
    }
}
