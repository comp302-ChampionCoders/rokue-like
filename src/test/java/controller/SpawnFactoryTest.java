import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import domain.gameobjects.*;
import domain.monsters.*;
import domain.enchantments.*;
import controller.SpawnFactory;

public class SpawnFactoryTest {

    @Test
    public void testCreateMonster_ValidPosition() {
        Hall hall = new Hall(10, 10, Hall.HallType.FIRE);
        Hero hero = new Hero(0, 0);
        hall.setHero(hero);

        Monster monster = SpawnFactory.createMonster(hall);

        assertNotNull(monster);
        assertTrue(hall.isValidPosition(monster.getX(), monster.getY()));
        assertFalse(hall.isPositionOccupied(monster.getX(), monster.getY()));
        assertTrue(repOk(hall)); // Validate hall state
    }

    @Test
    public void testCreateMonster_NotWithinHeroProximity() {
        Hall hall = new Hall(10, 10, Hall.HallType.WATER);
        Hero hero = new Hero(5, 5);
        hall.setHero(hero);

        Monster monster = SpawnFactory.createMonster(hall);

        assertNotNull(monster);
        int dx = Math.abs(monster.getX() - hero.getX());
        int dy = Math.abs(monster.getY() - hero.getY());
        assertTrue(dx > 3 || dy > 3); // Not within hero proximity
        assertTrue(repOk(hall)); // Validate hall state
    }

    @Test
    public void testCreateEnchantment_ValidPosition() {
        Hall hall = new Hall(10, 10, Hall.HallType.AIR);
        Hero hero = new Hero(0, 0);
        hall.setHero(hero);

        Enchantment enchantment = SpawnFactory.createEnchantment(hall);

        assertNotNull(enchantment);
        assertTrue(hall.isValidPosition(enchantment.getX(), enchantment.getY()));
        assertFalse(hall.isPositionOccupied(enchantment.getX(), enchantment.getY()));
        assertTrue(repOk(hall)); // Validate hall state
    }

    @Test
    public void testCreateEnchantment_MaxLivesConstraint() {
        Hall hall = new Hall(10, 10, Hall.HallType.EARTH);
        Hero hero = new Hero(0, 0);
        hero.setLives(4);
        hall.setHero(hero);

        Enchantment enchantment = SpawnFactory.createEnchantment(hall);

        assertNotNull(enchantment);
        assertTrue(enchantment instanceof ExtraTime); // ExtraTime instead of ExtraLife when max lives
        assertTrue(repOk(hall)); // Validate hall state
    }

    @Test
    public void testHallStateAfterSpawn() {
        Hall hall = new Hall(10, 10, Hall.HallType.FIRE);
        Hero hero = new Hero(0, 0);
        hall.setHero(hero);

        SpawnFactory.createMonster(hall);
        SpawnFactory.createEnchantment(hall);

        assertTrue(repOk(hall)); // Validate hall state after spawns
    }

    private boolean repOk(Hall hall) {
        for (Monster monster : hall.getMonsters()) {
            int x = monster.getX();
            int y = monster.getY();
            if (!hall.isValidPosition(x, y)) {
                return false; // Invalid position
            }
            if (isWithinHeroProximity(x, y, hall)) {
                return false; // Too close to the hero
            }
        }
        return true;
    }

    private boolean isWithinHeroProximity(int x, int y, Hall hall) {
        int heroX = hall.getHero().getX();
        int heroY = hall.getHero().getY();
        int dx = Math.abs(x - heroX);
        int dy = Math.abs(y - heroY);
        return dx <= 3 && dy <= 3;
    }

}
