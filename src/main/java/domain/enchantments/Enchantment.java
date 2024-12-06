package src.main.java.domain.enchantments;

import src.main.java.domain.gameobjects.*;
public abstract class Enchantment {
    private String name; 
    private boolean isActive; 

    public Enchantment(String name) {
        this.name = name;
        this.isActive = false;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        if (!isActive) {
            isActive = true;
            System.out.println(name + " has been activated.");
        }
    }

    public void deactivate() {
        if (isActive) {
            isActive = false;
            System.out.println(name + " has been deactivated.");
        }
    }

    // Abstract method to define specific behavior
    public abstract void applyEffect(Hero hero);

    // Abstract method to undo the effect
    public abstract void removeEffect(Hero hero);
}

