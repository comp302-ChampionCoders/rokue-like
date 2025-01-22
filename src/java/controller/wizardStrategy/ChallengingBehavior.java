package controller.wizardStrategy;

import controller.HallController;
import domain.gameobjects.Hero;
import domain.monsters.WizardMonster;
import domain.gameobjects.Hall;
import domain.gameobjects.Rune;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import domain.gameobjects.GameObject;

public class ChallengingBehavior implements WizardBehavior, Serializable {
    private static final int TELEPORT_INTERVAL = 3000; // 3 seconds
    private HallController hallController;

    public ChallengingBehavior() {
        hallController = HallController.getInstance();
    }

    @Override
    public void execute(int stateId, WizardMonster wizard, Hero hero) {
        if (stateId == CHALLENGING_STATE) {
            wizard.setActive(true);
            System.out.println("Wizard in challenging mode - Teleporting rune every 3 seconds");
            Rune rune = teleportRune(wizard.getCurrentHall());
            if(rune != null) {
                hallController.getCurrentHall().setRune(rune);
            }else{
                System.out.println("No rune found to teleport");
            }
        }
    }


//    private void teleportRune(Hall hall) {
//        if (hall == null) return;
//
//        Map<Point, GameObject> objects = hall.getObjects();
//        if (objects.isEmpty()) {
//            System.out.println("No objects available in the hall to teleport the rune.");
//            return;
//        }
//
//        // Get the rune from the hall
//        Rune rune = hall.getRune();
//        if (rune != null && !rune.isCollected()) {
//            // Get available positions from objects
//            List<Point> objectPositions = new ArrayList<>(objects.keySet());
//
//            // Select random position
//            int randomIndex = (int)(Math.random() * objectPositions.size());
//            Point randomPosition = objectPositions.get(randomIndex);
//
//            // Update rune position
//            hall.removeGridElement(rune.getX(), rune.getY());
//            rune.setPosition(randomPosition.x, randomPosition.y);
//            hall.addGridElement(rune, randomPosition.x, randomPosition.y);
//
//            System.out.println("Rune teleported to position: X=" + randomPosition.x + ", Y=" + randomPosition.y);
//        }
//    }

    private Rune teleportRune(Hall hall) {
        if (hall == null) {
            System.out.println("DEBUG: Hall is null in teleportRune");
            return null;
        }

        Map<Point, GameObject> objects = hall.getObjects();
        if (objects.isEmpty()) {
            System.out.println("DEBUG: No objects available in hall to teleport rune");
            return null;
        }

        // Get the rune from the hall
        Rune rune = hall.getRune();
        System.out.println("DEBUG: Current rune state - " +
                (rune == null ? "null" :
                        "Position: (" + rune.getX() + "," + rune.getY() + "), Collected: " + rune.isCollected()));

        if (rune != null && !rune.isCollected()) {
            // Get available positions from objects
            List<Point> objectPositions = new ArrayList<>(objects.keySet());

            // Select random position
            int randomIndex = (int)(Math.random() * objectPositions.size());
            Point randomPosition = objectPositions.get(randomIndex);

            System.out.println("DEBUG: Attempting to teleport rune to position: (" +
                    randomPosition.x + "," + randomPosition.y + ")");

            // Update rune position
            try {
                rune.setPosition(randomPosition.x, randomPosition.y);
                System.out.println("DEBUG: Successfully teleported rune to: (" +
                        randomPosition.x + "," + randomPosition.y + ")");
                return rune;
            } catch (Exception e) {
                System.out.println("DEBUG: Error during rune teleportation: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("DEBUG: Rune is either null or already collected");
            return null;
        }
    }
}