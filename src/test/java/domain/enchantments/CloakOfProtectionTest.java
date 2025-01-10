package domain.enchantments;

import domain.gameobjects.Hero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CloakOfProtectionTest {
    /**
     * Requires:
     * - CloakOfProtection enchantment is properly initialized
     * - Hero object is properly initialized
     *
     * Modifies:
     * - CloakOfProtection's active state
     * - Hero's visibility state
     *
     * Effects:
     * - Tests the application of cloak effect on hero
     * - Tests the duration of the effect
     * - Tests the removal of the effect and hero state restoration
     * Results:
     * - Basically we can see there are no issues when creating CoP, with its duration
     *  and the deactivation of it.
     */

    @Test
    void testCloakActivation() {
        CloakOfProtection cloak = new CloakOfProtection();
        Hero hero = new Hero(0, 0);
        boolean initialVisibility = hero.isVisible();

        cloak.applyEffect(hero);

        assertTrue(cloak.isActive(), "Cloak effect should be active after applying.");
        assertNotEquals(initialVisibility, hero.isVisible(), "Hero visibility should be toggled after applying cloak.");
    }

    @Test
    void testCloakDuration() {
        CloakOfProtection cloak = new CloakOfProtection();

        assertEquals(20000, cloak.getEffectDuration(), "Cloak duration should be 20 seconds (20000ms).");
    }

    @Test
    void testCloakDeactivation() {
        CloakOfProtection cloak = new CloakOfProtection();
        Hero hero = new Hero(0, 0);
        boolean initialVisibility = hero.isVisible();

        cloak.applyEffect(hero);
        cloak.removeEffect(hero);

        assertFalse(cloak.isActive(), "Cloak effect should be inactive after removal.");
        assertEquals(initialVisibility, hero.isVisible(),
                "Hero visibility should return to initial state after effect removal.");
    }
}