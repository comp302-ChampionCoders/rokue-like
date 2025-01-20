package domain.wizardStrategy;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import domain.monsters.WizardMonster;

public class ChallengingBehavior implements WizardBehavior {

    private long lastTeleportTime = System.currentTimeMillis();
    private static final long TELEPORT_INTERVAL = 3000; // 3 seconds

    @Override
    public void execute(WizardMonster monster, Hero hero, double remainingTimePercentage, Rune rune) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime >= TELEPORT_INTERVAL) {
            // Teleport rune to a random location every 3 seconds
            // May need check for objects for teleport place
            int runeX = (int) (Math.random() * 16);
            int runeY = (int) (Math.random() * 12);
            rune.teleport(runeX, runeY);
            System.out.println("Rune teleported to: (" + runeX + ", " + runeY + ")");
            lastTeleportTime = currentTime;
        }
    }
}
