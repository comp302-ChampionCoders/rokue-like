package domain.behaviors;

import domain.gameobjects.Hero;

public interface Collectible {
    // Called when hero attempts to collect the item
    void collect(Hero hero);
    
    // Check if the item can currently be collected, [checkavailability]
    boolean canBeCollected();
    
    // Make the collectible appear at specified coordinates [spawn]
    void appear(int x, int y);
    
    // Make the collectible disappear (either collected or timed out)
    void disappear();
    
    // Get the current position
    int getX();
    int getY();
    
    // Get the type/name of the collectible
    String getType();
    
    // Check if the collectible is currently available/visible
    boolean isAvailable();
    
    // Get the time remaining before disappearance (in milliseconds)
    // Returns -1 if the collectible doesn't have a timeout
    long getTimeRemaining();
}