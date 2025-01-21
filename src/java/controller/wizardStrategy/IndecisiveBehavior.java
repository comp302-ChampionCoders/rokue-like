package controller.wizardStrategy;

import domain.gameobjects.Hero;
import domain.monsters.WizardMonster;
import controller.HallController;

public class IndecisiveBehavior implements WizardBehavior {
    private long startTime;
    private static final long DISAPPEAR_DELAY = 2000; // 2 seconds

    @Override
    public void execute(int stateId, WizardMonster wizard, Hero hero) {
        if (stateId == INDECISIVE_STATE && wizard.isActive()) {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - startTime >= DISAPPEAR_DELAY) {
                HallController hallController = HallController.getInstance();
                hallController.getCurrentHall().removeGridElement(wizard.getX(), wizard.getY());
                wizard.setActive(false);
                System.out.println("Wizard disappeared after being indecisive for 2 seconds");
            }
        }
    }
}