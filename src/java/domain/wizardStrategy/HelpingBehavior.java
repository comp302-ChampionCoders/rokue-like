package domain.wizardStrategy;

import domain.gameobjects.Hero;
import domain.monsters.WizardMonster;
import domain.gameobjects.Rune;

public class HelpingBehavior implements WizardBehavior {
    @Override
    public void execute(WizardMonster monster, Hero hero, double remainingTimePercentage, Rune rune) {
        // Move hero to a random empty location
        // may need check for availability of the position
        int emptyX = (int) (Math.random() * 16); // Assume 11 is the grid width
        int emptyY = (int) (Math.random() * 12);  // Assume 9 is the grid height
        hero.updatePosition(emptyX, emptyY);
        System.out.println("Hero teleported to: (" + emptyX + ", " + emptyY + ")");

        // Deactivate the monster
        monster.setActive(false);
    }
}
