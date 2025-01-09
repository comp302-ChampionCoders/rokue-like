package controller;

import domain.enchantments.*;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import domain.monsters.*;
import domain.gameobjects.Hall;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpawnController {
    private static SpawnController instance;
    private List<Monster> monsters;
    private List<Enchantment> enchantments;
    private Random random;
    private Hero hero;
    private Rune rune;
    private Hall currentHall;

    private final int GRID_ROWS = 12;
    private final int GRID_COLUMNS = 16;

    private SpawnController(Hall currentHall) {
        this.currentHall = currentHall;
        this.monsters = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        this.random = new Random();
        this.hero = initializeHeroPosition();
        this.rune = initializeRune();
    }

    public static SpawnController getInstance(Hall currentHall) {
        if (instance == null) {
            instance = new SpawnController(currentHall);
        }
        return instance;
    }

    public Hero initializeHeroPosition() {
        int x, y;
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (!currentHall.isValidPosition(x, y));
        Hero newHero = new Hero(x, y);
        currentHall.addGridElement(newHero, x, y);
        return newHero;
    }

    public Rune initializeRune() {
        int x, y;
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (!currentHall.isValidPosition(x, y));
        Rune newRune = new Rune(x, y);
        currentHall.addGridElement(newRune, x, y);
        return newRune;
    }

    public void teleportRune() {
        int x, y;
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (!currentHall.isValidPosition(x, y));
        rune.teleport(x, y);
    }

    public Hero getHero() {
        return hero;
    }

    public Rune getRune() {
        return rune;
    }

    public void updateHall(Hall newHall) {
        this.currentHall = newHall;
        this.hero = initializeHeroPosition();
        this.rune = initializeRune();
    }

    public void spawnMonster() {
        if (monsters.size() >= 5) return;

        int x, y;
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (currentHall.isPositionOccupied(x, y) || isWithinHeroProximity(x, y));

        boolean wizardExists = monsters.stream().anyMatch(m -> m instanceof WizardMonster);
        int monsterType = random.nextInt(wizardExists ? 2 : 3); // 0: Archer, 1: Fighter, 2: Wizard
        Monster newMonster = null;
        switch (monsterType) {
            case 0:
                monsters.add(new ArcherMonster(x, y));
                break;
            case 1:
                monsters.add(new FighterMonster(x, y));
                break;
            case 2:
                monsters.add(new WizardMonster(x, y));
                break;
        }
        if (newMonster != null) {
            monsters.add(newMonster);
            currentHall.addGridElement(newMonster, x, y); // Add to the grid
            System.out.println("Monster spawned at: " + x + ", " + y);
        }
    }

    public void spawnEnchantment() {
        int x, y;
        boolean maxLives = (hero.getLives() == 4);
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (currentHall.isPositionOccupied(x, y));

        Enchantment enchantment;
        int enchantmentType = random.nextInt(5); // 0: Reveal, 1: Cloak, 2: Luring Gem, 3: Extra Time, 4: Extra Life
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
                return;
        }

        // enchantment.appear(x, y);
        // enchantments.add(enchantment);

        if (enchantment != null) {
            enchantment.appear(x, y);
            enchantments.add(enchantment);
            currentHall.addGridElement(enchantment, x, y); // Add to the grid

            System.out.println("Enchantment spawned at: " + x + ", " + y);

        }
    }

    public void removeEnchantment() {
        Iterator<Enchantment> iterator = enchantments.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();
            if (enchantment.isAvailable() && enchantment.getTimeRemaining() <= 0) {
                enchantment.disappear();
                iterator.remove();
            }
        }
    }

    private boolean isWithinHeroProximity(int x, int y) {
        int dx = Math.abs(x - hero.getX());
        int dy = Math.abs(y - hero.getY());
        return dx <= 3 && dy <= 3;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public List<Enchantment> getEnchantments() {
        return enchantments;
    }
}
