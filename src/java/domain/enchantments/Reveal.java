package domain.enchantments;
import domain.gameobjects.*;

public class Reveal extends Enchantment {
    private static final long DURATION = 10000;
    private int highlightX;
    private int highlightY;
    private boolean hasHighlight;

    public Reveal() {
        super("Reveal");
        this.hasHighlight = false;
    }

    @Override
    public void applyEffect(Hero hero) {
        activate();
        System.out.println("Rune's approximate location is revealed for " + (DURATION/1000) + " seconds.");
        
        // Schedule effect removal
        new Thread(() -> {
            try {
                Thread.sleep(DURATION);
                removeEffect(hero);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void removeEffect(Hero hero) {
        if (isActive()) {
            deactivate();
            hasHighlight = false;
            System.out.println("Highlight for the rune's location has disappeared.");
        }
    }

    public void setHighlightCenter(int x, int y) {
        this.highlightX = x;
        this.highlightY = y;
        this.hasHighlight = true;
    }

    public int getHighlightX() {
        return highlightX;
    }

    public int getHighlightY() {
        return highlightY;
    }

    public boolean hasHighlight() {
        return hasHighlight;
    }

    @Override
    public long getEffectDuration() {
        return DURATION;
    }
}