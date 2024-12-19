package domain.gameobjects;

import domain.monsters.*;
import domain.behaviors.Direction;
import java.util.*;

public class Hall {
    // Hall type enumeration
    public enum HallType {
        EARTH(6), AIR(9), WATER(13), FIRE(17);
        
        private final int minObjects;
        
        HallType(int minObjects) {
            this.minObjects = minObjects;
        }
        
        public int getMinObjects() {
            return minObjects;
        }
    }

    private final HallType hallType; 

    private final int width;
    private final int height;
    private final char[][] grid;
    private final Hero hero;
    private final List<Monster> monsters;
    private final Map<Point, GameObject> objects;
    private Rune rune;
    private Door door;
    private boolean isLocked;
    private boolean isActive;
    private Timer monsterSpawnTimer;

    public Hall(int width, int height, Hero hero, HallType hallType) {
        this.width = width;
        this.height = height;
        this.hero = hero;
        this.hallType = hallType;
        this.grid = new char[height][width];
        this.monsters = new ArrayList<>();
        this.objects = new HashMap<>();
        this.isLocked = true;
        initializeGrid();
        setupMonsterSpawner();
    }

    private void initializeGrid() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = '.';
            }
        }
        updateGridWithHero();
    }

    private void setupMonsterSpawner() {
        monsterSpawnTimer = new Timer();
        monsterSpawnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isActive && monsters.size() < 3) {
                    spawnRandomMonster();
                }
            }
        }, 8000, 8000); // Spawn every 8 seconds
    }

    private void spawnRandomMonster() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(width);
            y = rand.nextInt(height);
        } while (!isValidPosition(x, y));

        Monster monster;
        switch (rand.nextInt(3)) {
            case 0:
                monster = new ArcherMonster(x, y);
                break;
            case 1:
                monster = new FighterMonster(x, y);
                break;
            default:
                monster = new WizardMonster(x, y);
        }
        monsters.add(monster);
        updateGrid();
    }

    public boolean addObject(GameObject object, int x, int y) {
        Point position = new Point(x, y);
        if (isValidPosition(x, y) && !objects.containsKey(position)) {
            objects.put(position, object);
            updateGrid(x, y, getObjectSymbol(object));
            return true;
        }
        return false;
    }

    public boolean removeObject(int x, int y) {
        Point position = new Point(x, y);
        if (objects.containsKey(position)) {
            objects.remove(position);
            updateGrid(x, y, '.');
            return true;
        }
        return false;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && 
               grid[y][x] == '.' && !isPositionOccupied(x, y);
    }

    private boolean isPositionOccupied(int x, int y) {
        Point position = new Point(x, y);
        return objects.containsKey(position) || 
               monsters.stream().anyMatch(m -> m.getX() == x && m.getY() == y) ||
               (hero.getX() == x && hero.getY() == y);
    }

    public void updateState() {
        updateMonsters();
        checkCollisions();
        updateGrid();
    }

    private void updateMonsters() {
        for (Monster monster : monsters) {
            if (monster.isActive()) {
                monster.performAction(hero);
            }
        }
    }

    private void checkCollisions() {
        // Check hero-monster collisions
        for (Monster monster : monsters) {
            if (monster.isActive() && monster.isAdjacentToHero(hero)) {
                handleMonsterCollision(monster);
            }
        }

        // Check if hero found rune
        Point heroPosition = new Point(hero.getX(), hero.getY());
        if (objects.get(heroPosition) instanceof Rune) {
            handleRuneCollection();
        }
    }

    private void handleMonsterCollision(Monster monster) {
        if (monster instanceof FighterMonster) {
            hero.reduceLife();
        }
    }

    private void handleRuneCollection() {
        //rune.collect();
        isLocked = false;
        // Update door state
    }

    private void updateGrid() {
        // Clear grid
        initializeGrid();
        
        // Update with objects
        for (Map.Entry<Point, GameObject> entry : objects.entrySet()) {
            Point p = entry.getKey();
            updateGrid(p.x, p.y, getObjectSymbol(entry.getValue()));
        }

        // Update with monsters
        for (Monster monster : monsters) {
            if (monster.isActive()) {
                updateGrid(monster.getX(), monster.getY(), 'M');
            }
        }

        // Update with hero
        updateGridWithHero();
    }

    private void updateGridWithHero() {
        grid[hero.getY()][hero.getX()] = 'H';
    }

    private void updateGrid(int x, int y, char symbol) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = symbol;
        }
    }

    private char getObjectSymbol(GameObject object) {
        if (object instanceof Rune) return 'R';
        if (object instanceof Door) return 'D';
        return 'O'; // Generic object
    }

    // Getters
    public boolean isLocked() { return isLocked; }
    public char[][] getGrid() { return grid; }
    public List<Monster> getMonsters() { return new ArrayList<>(monsters); }
    public Map<Point, GameObject> getObjects() { return new HashMap<>(objects); }
}