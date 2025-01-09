package controller;

import domain.enchantments.*;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import domain.monsters.*;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpawnController {
    private static SpawnController instance;
    private List<Monster> monsters;
    private List<Enchantment> enchantments; // Hall bazli degil artik, enchantmentsi method icine verilen hallu kullanarak hero uzerinden almamiz gerekli, bunu silebiliriz
    private Random random;
    private Rune rune;
    private final int GRID_ROWS = 12;
    private final int GRID_COLUMNS = 16;

    //SpawnController artik Hall bazli degil, methodlari Hall bazli
    private SpawnController() {
        this.monsters = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        this.random = new Random();
    }

    public static SpawnController getInstance() {
        if (instance == null) {
            instance = new SpawnController();

        }
        return instance;
    }

    public Hero initializeHeroPosition(Hall hall) {
        if(hall.getHero() == null){
            int x, y;
            do {
                x = random.nextInt(GRID_COLUMNS);
                y = random.nextInt(GRID_ROWS);
            } while (!hall.isValidPosition(x, y));
            Hero newHero = new Hero(x, y);
            hall.addGridElement(newHero, x, y);
            return newHero;
        }
        return hall.getHero();
    }

    public Rune initializeRune(Hall hall) {
        Map<Point, GameObject> objects = hall.getObjects();
    
        if (objects.isEmpty()) {
            System.out.println("No objects available in the hall to place the rune.");
            Point runePosition = new Point(random.nextInt(GRID_COLUMNS), random.nextInt(GRID_ROWS));
            rune = new Rune(runePosition.x, runePosition.y, hall);
            hall.setRune(rune);
            return rune;
        }
        List<Point> objectPositions = new ArrayList<>(objects.keySet());
        Point randomPosition = objectPositions.get(random.nextInt(objectPositions.size()));
    
        Point runePosition = new Point(randomPosition);
        rune = new Rune(runePosition.x, runePosition.y, hall);
        hall.addGridElement(rune, runePosition.x, runePosition.y);
        System.out.println("Initial rune placed on an object at position: X=" + runePosition.x + ", Y=" + runePosition.y);
        return rune;
        
      

    }

    public void teleportRune(Hall hall) {
        int x, y;
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (!hall.isValidPosition(x, y));
        rune.teleport(x, y);
    }

    public void spawnMonster(Hall hall) {
        if (monsters.size() >= 5) return;

        int x, y;
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (hall.isPositionOccupied(x, y) || isWithinHeroProximity(x, y, hall));

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
            hall.addGridElement(newMonster, x, y); // Add to the grid
            System.out.println("Monster spawned at: " + x + ", " + y);
        }
    }

    public void spawnEnchantment(Hall hall) {
        int x, y;
        boolean maxLives = (hall.getHero().getLives() == 4);
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (hall.isPositionOccupied(x, y));

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
            hall.addGridElement(enchantment, x, y); // Add to the grid

            System.out.println("Enchantment spawned at: " + x + ", " + y);

        }
    }

    public void removeEnchantment(Hall hall) {
        Iterator<Enchantment> iterator = enchantments.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();
            if (enchantment.isAvailable() && enchantment.getTimeRemaining() <= 0) {
                enchantment.disappear();
                iterator.remove();
            }
        }
    }

    private boolean isWithinHeroProximity(int x, int y, Hall hall) {
        int dx = Math.abs(x - hall.getHero().getX());
        int dy = Math.abs(y - hall.getHero().getY());
        return dx <= 3 && dy <= 3;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public List<Enchantment> getEnchantments(Hall hall) { //#TODO Enchantmentlar SpawnControllerda sadece bir liste olmaz, her Hall icin enchantmentlara ayri erismek lazim
        return enchantments;
    }
}
