package domain.monsters;

import domain.gameobjects.*;
import domain.enchantments.*;

public class ArcherMonster extends Monster {

    public ArcherMonster(int x, int y) {
        super(x, y, "Archer");
    }

    @Override
    public void performAction(Hero hero) {
        if (Math.abs(hero.getX() - this.x) <= 4 || Math.abs(hero.getY() - this.y) <= 4) {
            System.out.println("ArcherMonster shoots an arrow at the hero!");
            hero.reduceLife(); 
        } else {
            System.out.println("ArcherMonster is out of range.");
        }
    }
}
