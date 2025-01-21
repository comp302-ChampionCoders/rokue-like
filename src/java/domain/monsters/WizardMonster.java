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

    private Hall currentHall;
    private int currentTimeRemaining;

    public WizardMonster(int x, int y) {
        super(x, y, "Wizard", TELEPORT_RANGE);
        this.lastTeleportTime = System.currentTimeMillis();
    }

    public void setTimeInfo(Hall hall, int remainingTime) {
        this.currentHall = hall;
        this.currentTimeRemaining = remainingTime;
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
        int totalGameTime = currentHall.getInitialTime();  // This gets the time based on objects

        System.out.println("=== Time Calculation Debug ===");
        System.out.println("Total Game Time (based on objects): " + totalGameTime);
        System.out.println("Current Remaining Time: " + currentTimeRemaining);

        double percentage = (currentTimeRemaining / (double) totalGameTime) * 100;
        System.out.println("Calculated Percentage: " + percentage + "%");
        System.out.println("Current Wizard State: " +
                (percentage > 70 ? "CHALLENGING" :
                        percentage < 30 ? "HELPING" :
                                "INDECISIVE"));
        System.out.println("===========================");

        return percentage;
    }

//    protected double calculateRemainingTimePercentage() {
//        TimerController timerController = TimerController.getInstance();
//
//        // Get default total game time
//        // int totalGameTime = 100;
//        int totalGameTime = TimerController.getDefaultGameTime();
//        int remainingTime = TimerController.getRemainingTime();
//
//        // Get remaining time - hardcoded for now, should be retrieved from TimerController
//        // TEST CASES
//        // int totalGameTime = 100;
//        // int remainingTime = 10;  // HERO DOES TP, WIZARD DOESNT disappear on grid [CONDITION TIME < 0.3]
//        // int remainingTime = 50; // CONDITION [TIME 0.3 < 0.7] Doesn't do anything, doesn't disappear
//        // int remainingTime = 90; // CONDITION [TIME > 0.7] does nothing
//
//        double percentage = (remainingTime / (double) totalGameTime) * 100;
//
//        // Debug output
//        System.out.println("=== Time Calculation Debug ===");
//        System.out.println("Total Game Time: " + totalGameTime);
//        System.out.println("Remaining Time: " + remainingTime);
//        System.out.println("Calculated Percentage: " + percentage + "%");
//        System.out.println("Current Wizard State: " +
//                (percentage > 70 ? "CHALLENGING" :
//                        percentage < 30 ? "HELPING" :
//                                "INDECISIVE"));
//        System.out.println("===========================");
//
//        return percentage;
//    }

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
