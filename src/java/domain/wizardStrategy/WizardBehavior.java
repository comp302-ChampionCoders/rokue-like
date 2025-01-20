package domain.wizardStrategy;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import domain.monsters.WizardMonster;

public interface WizardBehavior {
    void execute(WizardMonster monster, Hero hero, double remainingTimePercentage, Rune rune);
}
