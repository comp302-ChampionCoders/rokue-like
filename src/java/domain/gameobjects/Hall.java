package domain.gameobjects;

import domain.monsters.*;
import domain.behaviors.Direction;
import domain.behaviors.GridElement;

import java.awt.Point;
import java.util.*;
import java.util.function.IntFunction;

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
    private final GridElement[][] grid;
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
        this.grid = new GridElement[height][width];
        this.monsters = new ArrayList<>();
        this.objects = new HashMap<>();
        this.isLocked = true;
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = null;
            }
        }
        updateGridWithHero();
    }

    private boolean isHeroNull(){
        if(hero == null){
            return true;
        }
        return false;
    }

    public boolean addObject(GameObject object, int x, int y) {
        Point position = new Point(x, y);
        if (isValidPosition(x, y) && !objects.containsKey(position)) {
            objects.put(position, object);
            updateGrid(x, y, object);
            return true;
        }
        return false;
    }

    public boolean removeObject(int x, int y) {
        Point position = new Point(x, y);
        if (objects.containsKey(position)) {
            objects.remove(position);
            updateGrid(x, y, null);
            return true;
        }
        return false;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && 
               !isPositionOccupied(x, y);
    }

    private boolean isPositionOccupied(int x, int y) {
        if(!isHeroNull()){
            Point position = new Point(x, y);
            return objects.containsKey(position) || 
                   monsters.stream().anyMatch(m -> m.getX() == x && m.getY() == y) ||
                    (hero.getX() == x && hero.getY() == y);
        }
        else{
            Point position = new Point(x, y);
            return objects.containsKey(position) || 
                   monsters.stream().anyMatch(m -> m.getX() == x && m.getY() == y) || objects.containsKey(position);
        }
        
    }

    public void updateState() {
        updateMonsters();

        updateGrid();
    }

    private void updateMonsters() {
        for (Monster monster : monsters) {
            if (monster.isActive()) {
                monster.performAction(hero);
            }
        }
    }

    private void updateGrid() {
        // Clear grid
        initializeGrid();
        
        // Update with objects
        for (Map.Entry<Point, GameObject> entry : objects.entrySet()) {
            Point p = entry.getKey();
            updateGrid(p.x, p.y, objects.get(p));
        }

        // Update with monsters
        for (Monster monster : monsters) {
            if (monster.isActive()) {
                updateGrid(monster.getX(), monster.getY(), monster);
            }
        }

        // Update with hero
        updateGridWithHero();
    }

    private void updateGridWithHero() {
        if(!isHeroNull()){
            grid[hero.getY()][hero.getX()] = hero;
        }
        
    }

    private void updateGrid(int x, int y, GridElement elm) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = elm;
        }
    }

    public void displayGrid() {
        System.out.println("Grid Layout:");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == null) {
                    System.out.print("."); // Boş hücreler için nokta
                } else if (grid[y][x] instanceof Hero) {
                    System.out.print("H"); // Kahraman için H
                } else if (grid[y][x] instanceof Monster) {
                    System.out.print("M"); // Canavar için M
                } else if (grid[y][x] instanceof GameObject) {
                    System.out.print("O"); // Oyun nesneleri için O
                } else {
                    System.out.print("?"); // Tanımlanamayan elemanlar için ?
                }
            }
            System.out.println(); // Her satırın sonunda yeni bir satıra geç
        }
    }
    

    // Getters
    public HallType getHallType(){
        return hallType;
    }
    public boolean isLocked() { return isLocked; }
    public GridElement[][] getGrid() { return grid; }
    public List<Monster> getMonsters() { return new ArrayList<>(monsters); }
    public Map<Point, GameObject> getObjects() { return new HashMap<>(objects); }
}
