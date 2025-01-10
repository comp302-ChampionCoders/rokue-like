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
    private List<Enchantment> enchantments; // Hall bazli degil artik, enchantmentsi method icine verilen hallu kullanarak hero uzerinden almamiz gerekli, bunu silebiliriz
    private Random random;
    private Rune rune;
    private final int GRID_ROWS = 12;
    private final int GRID_COLUMNS = 16;

    //SpawnController artik Hall bazli degil, methodlari Hall bazli
    private SpawnController() {
        this.enchantments = new ArrayList<>();
        this.random = new Random();
    }

    public static SpawnController getInstance() {
        if (instance == null) {
            instance = new SpawnController();

        }
        return instance;
    }

    //
    public Hero initializeHeroPosition(Hall hall) {
        if(hall.getHero() == null){
            int x, y;
            do {
                x = random.nextInt(GRID_COLUMNS);
                y = random.nextInt(GRID_ROWS);
            } while (!hall.isValidPosition(x, y));
            Hero newHero = new Hero(x, y);
            hall.setHero(newHero);
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
        if (hall.getMonsters().size() >= 5) return;
        Monster monster = SpawnFactory.createMonster(hall);
        hall.addMonster(monster);
        System.out.println("Monster spawned at: " + monster.getX() + ", " + monster.getY());
    }
     
    public Enchantment spawnEnchantment(Hall hall) {
        Enchantment enchantment = SpawnFactory.createEnchantment(hall);
        System.out.println("Enchantment spawned at: " + enchantment.getX() + ", " + enchantment.getY());
        return enchantment;
    }

    public void removeEnchantment(Hall hall) {
        Iterator<Enchantment> iterator = enchantments.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();
            if (enchantment.isAvailable() && enchantment.getTimeRemaining() <= 0) {
                enchantment.disappear();
                iterator.remove();
                hall.removeGridElement(enchantment.getX(), enchantment.getY());
            }
        }
    }
    public List<Enchantment> getEnchantments(Hall hall) { //#TODO Enchantmentlar SpawnControllerda sadece bir liste olmaz, her Hall icin enchantmentlara ayri erismek lazim
        return enchantments;
    }
}
