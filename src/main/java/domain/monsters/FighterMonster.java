package src.main.java.domain.monsters;

import src.main.java.domain.gameobjects.*;
import src.main.java.domain.enchantments.*;

public class FighterMonster extends Monster {

    public FighterMonster(int x, int y) {
        super(x, y, "Fighter");
    }

    @Override
    public void performAction(Hero hero) {
        if (isAdjacentToHero(hero)) {
            System.out.println("FighterMonster stabs the hero!");
            hero.reduceLife(); 
        } else {
            moveTowardsHero(hero);
            System.out.println("FighterMonster moves closer to the hero.");
        }
    }
}
