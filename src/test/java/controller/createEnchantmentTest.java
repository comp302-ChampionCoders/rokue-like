package controller;

import domain.enchantments.*;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class createEnchantmentTest {

    private Hall hall;
    private Hero hero;

    @BeforeEach
    void setUp() {
        hero = new Hero(0, 0);
        hall = new Hall(10, 10, Hall.HallType.EARTH); // A 10x10 hall
        hall.setHero(hero);
    }

    @Test
    void testGenerateExtraTimeWhenHeroHasMaxLives() {
        // Set hero's lives to 4 (max)
        hero.setLives(4);

        // Generate an enchantment
        Enchantment enchantment = SpawnFactory.createEnchantment(hall);

        // Assert that the enchantment generated is ExtraTime
        assertTrue(enchantment instanceof ExtraTime, "Expected ExtraTime when hero has max lives.");
    }

    @Test
    void testGenerateExtraLifeWhenHeroHasOneLife() {
        // Set hero's lives to 1
        hero.setLives(1);
        hero.setExtraLifeAngel(true); // Enable extra life angel condition

        // Generate an enchantment
        Enchantment enchantment = SpawnFactory.createEnchantment(hall);

        // Assert that the enchantment generated is ExtraLife
        assertTrue(enchantment instanceof ExtraLife, "Expected ExtraLife when hero has 1 life and extraLifeAngel is true.");
    }

    @Test
    void testRandomEnchantmentWhenConditionsDoNotHold() {
        // Set hero's lives to a value other than 1 or 4
        hero.setLives(2);
        hero.setExtraLifeAngel(false); // Disable extra life angel condition

        // Generate multiple enchantments to check randomness
        boolean hasExtraLife = false;
        boolean hasExtraTime = false;
        boolean hasOtherEnchantments = false;

        for (int i = 0; i < 20; i++) { // Generate 20 enchantments to check randomness
            Enchantment enchantment = SpawnFactory.createEnchantment(hall);
            if (enchantment instanceof ExtraLife) {
                hasExtraLife = true;
            } else if (enchantment instanceof ExtraTime) {
                hasExtraTime = true;
            } else {
                hasOtherEnchantments = true;
            }
        }

        // Assert that all types of enchantments have a chance to be generated
        assertTrue(hasExtraLife, "Expected some ExtraLife enchantments.");
        assertTrue(hasExtraTime, "Expected some ExtraTime enchantments.");
        assertTrue(hasOtherEnchantments, "Expected some other enchantments.");
    }
}
