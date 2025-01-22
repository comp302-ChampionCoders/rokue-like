package controller.wizardStrategy;

import domain.gameobjects.Hero;
import domain.monsters.WizardMonster;
import controller.HallController;

import java.io.Serializable;
import java.util.Random;

public class HelpingBehavior implements WizardBehavior, Serializable {
    private static final Random random = new Random();

    @Override
    public void execute(int stateId, WizardMonster wizard, Hero hero) {
        if (stateId == HELPING_STATE && wizard.isActive()) {
            HallController hallController = HallController.getInstance();
            int maxAttempts = 10;

            // Try to find a valid position for the hero
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                int newX = random.nextInt(hallController.getCurrentHall().getWidth());
                int newY = random.nextInt(hallController.getCurrentHall().getHeight());

                if (hallController.getCurrentHall().isValidPosition(newX, newY)) {
                    hallController.getCurrentHall().removeGridElement(hero.getX(), hero.getY());
                    hero.updatePosition(newX, newY);
                    hallController.getCurrentHall().addGridElement(hero, newX, newY);
                    break;
                }
            }

            // Remove wizard after helping
            wizard.setActive(false);
            hallController.getCurrentHall().removeGridElement(wizard.getX(), wizard.getY());
            hallController.getCurrentHall().removeMonster(wizard);
            System.out.println("Wizard helped hero by teleporting them to safety");
        }
    }
}