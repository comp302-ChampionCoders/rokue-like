package controller;

import domain.enchantments.*;
import domain.gameobjects.Hall;
import domain.monsters.*;

import java.awt.Point;
import java.util.Random;

public class SpawnFactory {

    private static final Random RANDOM = new Random();

    public static Monster createMonster(Hall hall) {
        int x, y;
        do {
            x = RANDOM.nextInt(hall.getWidth());
            y = RANDOM.nextInt(hall.getHeight());
        } while (hall.isPositionOccupied(x, y) || isWithinHeroProximity(x, y, hall));

        boolean wizardExists = hall.getMonsters().stream().anyMatch(m -> m instanceof WizardMonster);
        int wizExst = (wizardExists ? 2 : 3);
        int monsterType = RANDOM.nextInt(wizExst); // 0: Archer, 1: Fighter, 2: Wizard

        Monster newMonster;
        switch (monsterType) {
            case 0:
                newMonster = new ArcherMonster(x, y);
                break;
            case 1:
                newMonster = new FighterMonster(x, y);
                break;
            case 2:
                newMonster = new WizardMonster(x, y);
                break;
            default:
                throw new IllegalStateException("Unexpected monster type: " + monsterType);
        }

        hall.addMonster(newMonster);
        hall.addGridElement(newMonster, x, y);
        return newMonster;
    }

    public static Enchantment createEnchantment(Hall hall) {
        int x, y;
        do {
            x = RANDOM.nextInt(hall.getWidth());
            y = RANDOM.nextInt(hall.getHeight());
        } while (hall.isPositionOccupied(x, y));

        boolean maxLives = hall.getHero().getLives() == 4;
        int enchantmentType = RANDOM.nextInt(5); // 0: Reveal, 1: Cloak, 2: Luring Gem, 3: Extra Time, 4: Extra Life

        Enchantment enchantment;
        switch (enchantmentType) {
            case 0:
                enchantment = new Reveal();
                break;
            case 1:
                enchantment = new CloakOfProtection();
                break;
            case 2:
                enchantment = new LuringGem();
                break;
            case 3:
                enchantment = new ExtraTime();
                break;
            case 4:
                enchantment = maxLives ? new ExtraTime() : new ExtraLife();
                break;
            default:
                throw new IllegalStateException("Unexpected enchantment type: " + enchantmentType);
        }

        enchantment.appear(x, y);
        hall.addGridElement(enchantment, x, y);
        return enchantment;
    }

    private static boolean isWithinHeroProximity(int x, int y, Hall hall) {
        int dx = Math.abs(x - hall.getHero().getX());
        int dy = Math.abs(y - hall.getHero().getY());
        return dx <= 3 && dy <= 3;
    }
}
