package domain.monsters;


import domain.gameobjects.Rune;
import domain.wizardStrategy.WizardBehavior;
import domain.wizardStrategy.HelpingBehavior;
import domain.wizardStrategy.ChallengingBehavior;
import domain.wizardStrategy.IndecisiveBehavior;
import domain.behaviors.Direction;
import domain.gameobjects.Hero;
import controller.TimerController;


public class WizardMonster extends Monster {
    private static final int TELEPORT_RANGE = 0; // Wizard monsters don't move
    private long lastTeleportTime;
    private static final long TELEPORT_COOLDOWN = 5000; // 5 seconds in milliseconds

    private WizardBehavior behavior;

    public WizardMonster(int x, int y) {
        super(x, y, "Wizard", TELEPORT_RANGE);
        this.lastTeleportTime = System.currentTimeMillis();
    }

    /*
    @Override
    public void performAction(Hero hero, Rune rune) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime >= TELEPORT_COOLDOWN) {
            teleportRune();
            lastTeleportTime = currentTime;
        }
    }
    */

    @Override
    public void performAction(Hero hero, Rune rune) {
        double remainingTimePercentage = calculateRemainingTimePercentage();
        updateBehavior(remainingTimePercentage);
        behavior.execute(this, hero, remainingTimePercentage, rune);
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



    //ADD LOGIC FOR TIME CALCULATION
    protected double calculateRemainingTimePercentage() {
        TimerController timerController = TimerController.getInstance();

        // Get remaining time for the current game
        //int remainingTime = timerController.getRemainingGameTime(HallType.GENERAL); // Replace HallType.GENERAL with the actual type
        int remainingTime = 10;
        // Get the default total game time
        int totalGameTime = TimerController.DEFAULT_GAME_TIME;

        // Calculate and return the percentage
        return (remainingTime / (double) totalGameTime) * 100;
    }

    private void updateBehavior(double remainingTimePercentage) {
        if (remainingTimePercentage < 30) {
            behavior = new HelpingBehavior();
        } else if (remainingTimePercentage > 70) {
            behavior = new ChallengingBehavior();
        } else {
            behavior = new IndecisiveBehavior();
        }
    }
}
