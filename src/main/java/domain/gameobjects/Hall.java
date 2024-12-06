package src.main.java.domain.gameobjects;

import src.main.java.domain.monsters.*;
import java.util.ArrayList;
import java.util.List;

public class Hall {
    private int width;
    private int height;
    private char[][] grid; // 2D array to represent the layout of the hall
    private Hero hero;
    private List<Monster> monsters; 
    private Rune rune;
    private Door door;

    public Hall(int width, int height, Hero hero) { // size should be fixed
        this.width = width;
        this.height = height;
        this.hero = hero;
        this.grid = new char[height][width]; // Initialize the grid
        this.monsters = new ArrayList<>();
        initializeLayout();
    }
    
    // First imagining the game in 2D, before involving with the UI
    private void initializeLayout() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = '.';         // Fill the grid with default values
            }
        }

        // Place the hero on the grid
        grid[hero.getY()][hero.getX()] = 'H';

        // Place a rune in the hall
        rune = new Rune(generateRandomPosition());
        grid[rune.getY()][rune.getX()] = 'R';

        // Place a door in the hall // maybe the door should have restrictions for its location, ex: border // revisit
        door = new Door(generateRandomPosition());
        grid[door.getY()][door.getX()] = 'D';
    }

    // Generate a random valid position on the grid
    private int[] generateRandomPosition() {
        int x, y;
        do {
            x = (int) (Math.random() * width);
            y = (int) (Math.random() * height);
        } while (grid[y][x] != '.'); // Ensure the position is empty
        return new int[]{x, y};
    }

    public void addMonster(Monster monster) {
        monsters.add(monster);
        int[] position = generateRandomPosition();
        monster.setX(position[0]);
        monster.setY(position[1]);
        grid[monster.getY()][monster.getX()] = 'M';
    }

    // Update State for hero and monster movements // #TODO: needs more detailed implementation
    public void updateState(String heroMoveDirection) {
        // Move hero
        int oldX = hero.getX();
        int oldY = hero.getY();
        hero.move(heroMoveDirection);

        if (isValidMove(hero.getX(), hero.getY())) {
            // Update hero position in the grid
            grid[oldY][oldX] = '.'; // replace hero "H" with "."
            grid[hero.getY()][hero.getX()] = 'H';

            checkInteractions();
        } else {
            // Revert move if wrong
            hero.setX(oldX);
            hero.setY(oldY);
        }

        // Update Monster Location, 
        for (Monster monster : monsters) {
            int oldMonsterX = monster.getX();
            int oldMonsterY = monster.getY();
            // monster.moveRandomly(); #TODO: Monster movement implementation

            if (isValidMove(monster.getX(), monster.getY())) {
                grid[oldMonsterY][oldMonsterX] = '.';
                grid[monster.getY()][monster.getX()] = 'M';
            } else {
                monster.setX(oldMonsterX);
                monster.setY(oldMonsterY);
            }
        }
    }

    // Check if a move is valid [out of bounds]
    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && grid[y][x] == '.';
    }

    private void checkInteractions() {
        // Check if the hero is on the same position as the rune // #TODO: recheck implementation
        if (hero.getX() == rune.getX() && hero.getY() == rune.getY()) {
            rune.collect();
            door.unlock();
        }

        if (hero.getX() == door.getX() && hero.getY() == door.getY() && door.isUnlocked()) {
            System.out.println("Hero enters the next hall!");
            // Transfer logic here
        }

        for (Monster monster : monsters) {
            if (hero.getX() == monster.getX() && hero.getY() == monster.getY()) {
                // hero.loseLife();
                System.out.println("Hero attacked by a monster!"); // only if by fightermonster
            }
        }
    }

    // Display the hall's grid // for debugging 
    public void displayHall() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}

