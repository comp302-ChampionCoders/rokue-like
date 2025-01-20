package domain.wizardStrategy;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import domain.monsters.WizardMonster;

public class IndecisiveBehavior implements WizardBehavior {
    private long startTime;

    public IndecisiveBehavior() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void execute(WizardMonster monster, Hero hero, double remainingTimePercentage, Rune rune) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= 2000) { // 2 seconds
            monster.setActive(false);
            System.out.println("Wizard monster disappeared after being indecisive.");
        }
    }
}
