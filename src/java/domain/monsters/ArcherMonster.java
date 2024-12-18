package domain.monsters;

import domain.gameobjects.*;
import domain.behaviors.Direction;

public class ArcherMonster extends Monster {
    private static final int SHOOT_RANGE = 4;
    private int moveCounter = 0;

    public ArcherMonster(int x, int y) {
        super(x, y, "Archer", SHOOT_RANGE);
    }

    @Override
    public void performAction(Hero hero) {
        // Don't attack if hero is invisible (using Cloak of Protection)
        if (!hero.isVisible()) {return;}
        
        // Check if hero is in range
        if (isInRange(hero)) {
            System.out.println("ArcherMonster shoots an arrow at the hero!");
            hero.reduceLife();
        } else {

            // Optionally move towards hero to get in range
            if (Math.random() < 0.5) {
                moveTowardsHero(hero);
            } else {
                moveRandomly();
            }
        }
    }
    private void moveRandomly(){
        moveCounter++;
        if (moveCounter >= 2){
            move(getRandomDirection());
            moveCounter = 0;
        }
    }
}