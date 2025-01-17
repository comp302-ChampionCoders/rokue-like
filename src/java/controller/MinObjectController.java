package controller;

import domain.gameobjects.Hall;

public class MinObjectController {
    private HallController hallController;

    public MinObjectController(HallController hallController) {
        this.hallController = hallController;
    }

    public boolean areAllMinimumRequirementsMet() {
        for (Hall hall : hallController.getHalls()) {
            int currentObjectCount = hall.getObjects().size();
            int requiredObjectCount = hall.getHallType().getMinObjects();

            if (currentObjectCount < requiredObjectCount) {
                System.out.println("Minimum requirement not met for Hall: " + hall.getHallType());
                //return false;
            }
        }
        return true;
    }
}
