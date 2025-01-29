package domain.enchantments;
import domain.gameobjects.*;

import java.io.Serializable;

public class Reveal extends Enchantment implements Serializable {
    private static final long DURATION = 10000;
    private int highlightX;
    private int highlightY;
    private boolean hasHighlight;

    public Reveal() {
        super("Reveal", "/images/reveal32x32.png");
        this.hasHighlight = false;
    }


    @Override
    public void applyEffect(Hero hero) {

    }

    public void applyEffect(Hall hall) {
        if (hall != null) {
            Rune rune = hall.getRune(); // Access the rune from the hall
            if (rune != null) {
                setHighlightCenter(rune.getX(), rune.getY()); // Highlight the rune's position
                System.out.println("Reveal enchantment activated: Highlighting rune's location.");
            } else {
                System.out.println("No rune found in the current hall.");
            }
        } else {
            System.out.println("Invalid hall passed to Reveal.");
        }

        // Schedule effect removal
        new Thread(() -> {
            try {
                Thread.sleep(DURATION);
                removeEffect(null); // Effect removal doesn't need specific input
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
        System.out.println("Highlight set at: (" + x + ", " + y + ")"); // debug
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