package domain.enchantments;
import domain.gameobjects.*;

public class Reveal extends Enchantment {
    private static final int DURATION = 10000; 
    private Point highlightCenter;

    public Reveal() {
        super("Reveal");
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
            highlightCenter = null;
            System.out.println("Highlight for the rune's location has disappeared.");
        }
    }

    public void setHighlightCenter(Point center) {this.highlightCenter = center;}
    public Point getHighlightCenter() {return highlightCenter;}

    @Override
    public long getEffectDuration() {return DURATION;}
}

