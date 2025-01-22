package controller.wizardStrategy;

import domain.gameobjects.Hero;
import domain.monsters.WizardMonster;
import controller.HallController;

import java.io.Serializable;

public class IndecisiveBehavior implements WizardBehavior, Serializable {
    private HallController hallController;

    public IndecisiveBehavior() {
        this.hallController = HallController.getInstance();
    }

    @Override
    public void execute(int stateId, WizardMonster wizard, Hero hero) {
        if (stateId == INDECISIVE_STATE && wizard.isActive()) {
            // Remove wizard from the grid
            hallController.getCurrentHall().removeGridElement(wizard.getX(), wizard.getY());
            hallController.getCurrentHall().removeMonster(wizard);
            wizard.setActive(false);
            System.out.println("Wizard disappeared due to indecision");
        }
    }
}