package domain.gameobjects;

import domain.monsters.*;
import domain.behaviors.GridElement;
import domain.enchantments.Enchantment;

import java.awt.Point;
import java.util.*;


import controller.SpawnController;

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
    private final Map<Point, GridElement> gridElements;
    private  Hero hero;
    private  List<Monster> monsters;
    private final Map<Point, GameObject> objects;
    private Rune rune;
    private Door door;
    private boolean isLocked;
    private boolean isActive;
    private SpawnController spawnController;

    public Hall(int width, int height,HallType hallType) {
        this.width = width;
        this.height = height;
        this.hallType = hallType;
        this.grid = new GridElement[height][width];
        this.gridElements = new HashMap<>();
        this.monsters = new ArrayList<>();
        this.objects = new HashMap<>();
        this.isLocked = true;
        this.hero = null; 
        this.rune = null;
        initializeGrid();
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    private void initializeGrid() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = null;
            }
        }
    }

    public Rune getRune(){
        return rune;
    }

    public void setRune(Rune rune){
        this.rune = rune;
    }

    public void setHero(Hero hero){
        this.hero = hero;
    }

    public boolean addObject(GameObject object, int x, int y) {
        Point position = new Point(x, y);
        if (isValidPosition(x, y) && !objects.containsKey(position)) {
            objects.put(position, object);
            gridElements.put(position, object);
            updateGrid(x, y, object);
            return true;
        }
        return false;
    }

    public boolean removeObject(int x, int y) {
        Point position = new Point(x, y);
        if (objects.containsKey(position)) {
            objects.remove(position);
            gridElements.remove(position);
            updateGrid(x, y, null);
            return true;
        }
        return false;
    }

    public boolean addGridElement(GridElement element, int x, int y) {
        Point position = new Point(x, y);
        if (isValidPosition(x, y) && !gridElements.containsKey(position)) {
            gridElements.put(position, element);
            updateGrid(x, y, element);
            return true;
        }
        return false;
    }

    public boolean removeGridElement(int x, int y) {
        Point position = new Point(x, y);
        if (gridElements.containsKey(position)) {
            gridElements.remove(position);
            updateGrid(x, y, null);
            return true;
        }
        return false;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && 
               !isPositionOccupied(x, y);
    }

    public boolean isPositionOccupied(int x, int y) {
        Point position = new Point(x, y);
        return gridElements.containsKey(position);
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
    public void updateGrid() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = null;
            }
        }

        for (Map.Entry<Point, GridElement> entry : gridElements.entrySet()) {
            Point p = entry.getKey();
            updateGrid(p.x, p.y, entry.getValue());
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
                } else if (grid[y][x] instanceof Enchantment) {
                        System.out.print("E"); // E for enchantments
                } else {
                    System.out.print("?"); // ? for undefined objects
                }
            }
            System.out.println(); 
        }
    }
    

    // Getters
    public HallType getHallType(){
        return hallType;
    }

    public SpawnController getSpawnController(){
        return spawnController;
    }

    public void addMonster(Monster monster){
        monsters.add(monster);
    }

    public void clearMonsters(){
        this.monsters = new ArrayList<>();
    }

    public boolean isLocked() { return isLocked; }
    public GridElement[][] getGrid() { return grid; }
    public List<Monster> getMonsters() { return new ArrayList<>(monsters); }
    public Map<Point, GameObject> getObjects() { return new HashMap<>(objects); }
    public Map<Point, GridElement> getGridElements() {
        return new HashMap<>(gridElements);
    }
    public Hero getHero(){return hero;}
}
