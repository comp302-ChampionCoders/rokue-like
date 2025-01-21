package controller.wizardStrategy;

import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import domain.monsters.WizardMonster;
import controller.HallController;

public interface WizardBehavior {
    int CHALLENGING_STATE = 0;  // >70%
    int INDECISIVE_STATE = 1;   // 30-70%
    int HELPING_STATE = 2;      // <30%

    void execute(int stateId, WizardMonster wizard, Hero hero);
}
