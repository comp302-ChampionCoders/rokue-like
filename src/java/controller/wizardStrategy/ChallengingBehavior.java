package controller.wizardStrategy;

import domain.gameobjects.Hero;
import domain.monsters.WizardMonster;
// import ui.GameScreen;  // We'll need this to access handleWizardTeleportAction

public class ChallengingBehavior implements WizardBehavior {
    @Override
    public void execute(int stateId, WizardMonster wizard, Hero hero) {
        if (stateId == CHALLENGING_STATE) {
            wizard.setActive(true); // Keep wizard active to maintain teleport behavior
            // Note: The actual teleport happens through GameScreen's timer system
            System.out.println("Wizard in challenging mode - Teleporting rune every 3 seconds");
        }
    }
}