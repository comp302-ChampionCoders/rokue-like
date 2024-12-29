package ui.swing;

import controller.ScreenTransition;
import domain.behaviors.Direction;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import domain.monsters.ArcherMonster;
import domain.monsters.FighterMonster;
import domain.monsters.Monster;
import domain.monsters.WizardMonster;
import ui.utils.CursorUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GameScreen extends JFrame {
    private final int GRID_ROWS = 12;
    private final int GRID_COLUMNS = 16;
    private final int CELL_SIZE = 50; // Grid cell size
    private Hero hero; // Hero nesnesi
    private List<Monster> monsters; // Monster listesi
    private Random random;
    private Timer monsterTimer; // Monster hareketleri için timer
    private Timer spawnTimer; // Yeni monster oluşturmak için timer
    private Timer runeTimer; // Rune yer değiştirme için timer
    private Point runePosition; // Rune pozisyonu
    private BufferedImage runeImage;

    private Timer gameTimer;
    private int timeRemaining;  

    private Timer archerAttackTimer;
    private static final int ARCHER_ATTACK_DELAY = 1000; // 1 second in milliseconds

    private final ScreenTransition returnToGameOverScreen;
    private ArrayList<Hall> allHalls = new ArrayList<>();

    private Hall earthHall;
    private Hall waterHall;
    private Hall fireHall;
    private Hall airHall;
    private Hall currentHall;

    public GameScreen(ScreenTransition returnToGameOverScreen, ArrayList<Hall> allHalls) {
        this.returnToGameOverScreen = returnToGameOverScreen;
        this.allHalls = allHalls;
        this.earthHall = allHalls.get(0);
        this.waterHall = allHalls.get(1);
        this.fireHall = allHalls.get(2);
        this.airHall = allHalls.get(3);

        this.currentHall = earthHall;

        setTitle("Game Screen");
        setSize(GRID_COLUMNS * CELL_SIZE + 50, GRID_ROWS * CELL_SIZE + 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setCursor(CursorUtils.createCustomCursor("src/resources/images/pointer_a.png"));
        hero = new Hero(0, 0); // Hero başlangıç konumu
        monsters = new ArrayList<>();
        random = new Random();
        loadEarthHall();
        //runePosition = new Point(random.nextInt(GRID_COLUMNS), random.nextInt(GRID_ROWS));
        initializeRunePosition();

       //loadRuneImage();
        spawnMonsters();
        monsterTimer = new Timer(500, e -> moveMonsters()); // Timer her 500ms monster hareketi için
        spawnTimer = new Timer(8000, e -> addRandomMonster()); // Her 8 saniyede bir monster ekle
        runeTimer = new Timer(5000, e -> teleportRune()); // Her 5 saniyede rune taşı
        archerAttackTimer = new Timer(ARCHER_ATTACK_DELAY, e -> checkArcherAttacks());
        monsterTimer.start();
        spawnTimer.start();
        runeTimer.start();
        archerAttackTimer.start();

        timeRemaining = 30; // every hall has 30 seconds to complete
        gameTimer = new Timer(1000, e -> updateTime());  // Update every second
        gameTimer.start();
        add(new GamePanel());
    }

    private void updateTime() {
        timeRemaining--;
        updateTitle();
        
        if (timeRemaining <= 0) {
            stopGame();
            returnToGameOverScreen.execute();
        }
    }
    private void updateTitle() {
        setTitle(String.format("Game Screen - Hero has %d remaining lives, %d seconds left", hero.getLives(), timeRemaining));
    }

    private void loadEarthHall() {
        Map<Point, GameObject> earthObjects = earthHall.getObjects();
    
        for (Map.Entry<Point, GameObject> entry : earthObjects.entrySet()) {
            System.out.println("icinde");
            Point position = entry.getKey();
            GameObject gameObject = entry.getValue();
    
            if (gameObject.getImage() != null) {
            } else {
                System.err.println("Missing image for object at position: " + position);
            }
        }
        repaint(); 
    }
    
    private void initializeRunePosition() {
        Map<Point, GameObject> objects = currentHall.getObjects();
    
        if (objects.isEmpty()) {
            System.out.println("No objects available in the hall to place the rune.");
            runePosition = new Point(random.nextInt(GRID_COLUMNS), random.nextInt(GRID_ROWS));
            return; // No objects to place the rune on
        }
    
        // Randomly select a GameObject
        List<Point> objectPositions = new ArrayList<>(objects.keySet());
        Point randomPosition = objectPositions.get(random.nextInt(objectPositions.size()));
    
        // Set the rune position to the selected object's position
        runePosition = new Point(randomPosition);
        System.out.println("Initial rune placed on an object at position: X=" + runePosition.x + ", Y=" + runePosition.y);
    }
    

    private void loadRuneImage() {
        try {
            runeImage = ImageIO.read(new File("src/resources/images/rune.png"));
        } catch (IOException | NullPointerException e) {
            System.err.println("Failed to load rune image.");
            e.printStackTrace();
        }
    }
    
    

    private void spawnMonsters() {
        monsters.add(new ArcherMonster(random.nextInt(GRID_COLUMNS), random.nextInt(GRID_ROWS)));
    }
    

    private void moveMonsters() {
        Direction[] directions = Direction.values(); // Get all possible directions
        for (Monster monster : monsters) {
            if ((monster instanceof FighterMonster)) { // Sadece fighter monster hareket ediyor
                Direction randomDirection = directions[random.nextInt(directions.length)];
                int newX = monster.getX() + randomDirection.getDx();
                int newY = monster.getY() + randomDirection.getDy();

                // Check boundaries and prevent overlap
                if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS && !isPositionOccupied(newX, newY)) {
                    monster.move(randomDirection);
                }
            }
        }
        checkHeroMonsterCollision();
        repaint();
    }

    private void addRandomMonster() {
        if (monsters.size() >= 5) return; // Canavar sayısı 5'i geçmesin

        int x, y;
        do {
            x = random.nextInt(GRID_COLUMNS);
            y = random.nextInt(GRID_ROWS);
        } while (isPositionOccupied(x, y) || isWithinHeroProximity(x, y));

        boolean wizardExists = monsters.stream().anyMatch(m -> m instanceof WizardMonster);
        int monsterType = random.nextInt(wizardExists ? 2 : 3); // 0: Archer, 1: Fighter, 2: Wizard (eğer yoksa)
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
        repaint();
    }
    private boolean isWithinHeroProximity(int x, int y) {
        int dx = Math.abs(x - hero.getX());
        int dy = Math.abs(y - hero.getY());
        return dx <= 3 && dy <= 3; // Hero'nun 3x3 alanını kontrol eder
    }

    private boolean isPositionOccupied(int x, int y) {
        if (hero.getX() == x && hero.getY() == y) return true;
        if (runePosition.x == x && runePosition.y == y) return true;
        for (Monster monster : monsters) {
            if (monster.getX() == x && monster.getY() == y) return true;
        }
        for(GameObject obj : currentHall.getObjects().values()){
            if(obj.getX() == x && obj.getY() == y){
                return true;
            }
        }
        return false;
    }

    private void teleportRune() {
        boolean wizardExists = monsters.stream().anyMatch(m -> m instanceof WizardMonster);
        if (wizardExists) {
            Map<Point, GameObject> objects = currentHall.getObjects();
    
            if (objects.isEmpty()) {
                System.out.println("No objects available in the hall to teleport the rune.");
                return; // No objects to teleport the rune to
            }
    
            // Randomly select a new position from the available objects
            List<Point> objectPositions = new ArrayList<>(objects.keySet());
            Point randomPosition = objectPositions.get(random.nextInt(objectPositions.size()));
    
            // Update the rune's position
            runePosition.setLocation(randomPosition);
            System.out.println("Rune teleported to an object at position: X=" + randomPosition.x + ", Y=" + randomPosition.y);
    
            repaint(); // Update the game screen
        }
    }
    

    private void checkHeroMonsterCollision() {
        for (Monster monster : monsters) {
            if (monster instanceof FighterMonster) {
                // Find distances of x's and y's
                int dx = Math.abs(monster.getX() - hero.getX());
                int dy = Math.abs(monster.getY() - hero.getY());
    
                if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                    // FighterMonster tek vuruşta 3 can alır
                    hero.reduceLife();
                    hero.reduceLife();
                    hero.reduceLife();
                    updateTitle();
                    System.out.println("Hero has been attacked by a Fighter monster, " + hero.getLives() + " lives remaining");
                    if (hero.getLives() <= 0) {
                        System.out.println("Hero has died. Returning to Main Menu...");
                        stopGame();
                        returnToGameOverScreen.execute(); // Transition to main menu
                    }
                    break;
                }
            }
        }
    }
    // Helper method to check if there's an obstacle between archer and hero
    private boolean isPathBlocked(int archerX, int archerY, int heroX, int heroY) {
        // Check only if they're in the same row or column
        if (archerX == heroX) {
            // Check vertical path
            int startY = Math.min(archerY, heroY);
            int endY = Math.max(archerY, heroY);
            for (int y = startY + 1; y < endY; y++) {
                if (isPositionOccupied(archerX, y)) return true;
            }
        } else if (archerY == heroY) {
            // Check horizontal path
            int startX = Math.min(archerX, heroX);
            int endX = Math.max(archerX, heroX);
            for (int x = startX + 1; x < endX; x++) {
                if (isPositionOccupied(x, archerY)) return true;
            }
        }
        return false;
    }
    // Add this new method for archer attacks
    private void checkArcherAttacks() {
        for (Monster monster : monsters) {
            if (monster instanceof ArcherMonster) {
                int dx = Math.abs(monster.getX() - hero.getX());
                int dy = Math.abs(monster.getY() - hero.getY());
                
                // Check if hero is in archer's range (within 2 squares horizontally OR vertically, not diagonally)
                if ((dx <= 3 && dy == 0) || (dx == 0 && dy <= 3)) {
                    // Only reduce life if there's no obstacle between archer and hero
                    if (!isPathBlocked(monster.getX(), monster.getY(), hero.getX(), hero.getY())) {
                        hero.reduceLife();
                        updateTitle();
                        System.out.println("Hero has been shot by an Archer monster, " + hero.getLives() + " lives remaining");
                        if (hero.getLives() <= 0) {
                            System.out.println("Hero has died. Returning to Main Menu...");
                            stopGame();
                            returnToGameOverScreen.execute();
                        }
                    }
                }
            }
        }
    }
    
    private void stopGame() {
        monsterTimer.stop();
        spawnTimer.stop();
        runeTimer.stop();
        archerAttackTimer.stop();
        gameTimer.stop();
        dispose(); 
    }

    private class GamePanel extends JPanel implements KeyListener, MouseListener {
        private BufferedImage heroImage;
        private BufferedImage archerImage;
        private BufferedImage fighterImage;
        private BufferedImage wizardImage;
        private boolean isKeyPressed = false; 

        public GamePanel() {
            setFocusable(true);
            addKeyListener(this);
            addMouseListener(this);
            loadImages();
            setBackground(new Color(66, 40, 53,255));
        }

        private void loadImages() {
            try {
                heroImage = ImageIO.read(new File("src/resources/images/player.png"));
                archerImage = ImageIO.read(new File("src/resources/images/archer.png"));
                fighterImage = ImageIO.read(new File("src/resources/images/fighter.png"));
                wizardImage = ImageIO.read(new File("src/resources/images/wizard.png"));
            } catch (IOException | NullPointerException e) {
                System.err.println("Failed to load images.");
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGrid(g);
            drawArcherRanges(g);
            drawEarthHallObjects(g);
            drawHero(g);
            drawMonsters(g);
            drawRune(g);
        }

        private void drawGrid(Graphics g) {
            g.setColor(Color.GRAY);
            for (int i = 0; i <= GRID_ROWS; i++) {
                g.drawLine(0, i * CELL_SIZE, GRID_COLUMNS * CELL_SIZE, i * CELL_SIZE); // Horizontal lines
            }
            for (int j = 0; j <= GRID_COLUMNS; j++) {
                g.drawLine(j * CELL_SIZE, 0, j * CELL_SIZE, GRID_ROWS * CELL_SIZE); // Vertical lines
            }
        }

        private void drawHero(Graphics g) {
            if (heroImage != null) {
                g.drawImage(heroImage, hero.getX() * CELL_SIZE, hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.RED);
                g.fillRect(hero.getX() * CELL_SIZE, hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }


        private void drawEarthHallObjects(Graphics g) {
            Map<Point, GameObject> earthObjects = earthHall.getObjects();
            for (Map.Entry<Point, GameObject> entry : earthObjects.entrySet()) {
                Point position = entry.getKey();
                GameObject gameObject = entry.getValue();
                if (gameObject.getImage() != null) {
                    g.drawImage(
                        gameObject.getImage(),
                        position.x * CELL_SIZE,
                        position.y * CELL_SIZE,
                        CELL_SIZE,
                        CELL_SIZE,
                        this
                    );
                }
            }
        }
        

        private void drawMonsters(Graphics g) {
            for (Monster monster : monsters) {
                BufferedImage monsterImage = null;
                if (monster instanceof ArcherMonster) {
                    monsterImage = archerImage;
                } else if (monster instanceof FighterMonster) {
                    monsterImage = fighterImage;
                } else if (monster instanceof WizardMonster) {
                    monsterImage = wizardImage;
                }
                if (monsterImage != null) {
                    g.drawImage(monsterImage, monster.getX() * CELL_SIZE, monster.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(monster.getX() * CELL_SIZE, monster.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        private void drawRune(Graphics g) {
            if (runeImage != null) {
                g.drawImage(runeImage, runePosition.x * CELL_SIZE, runePosition.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(runePosition.x * CELL_SIZE, runePosition.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        private void drawArcherRanges(Graphics g) {
            g.setColor(new Color(255, 0, 0, 80));  // Semi-transparent red
            
            for (Monster monster : monsters) {
                if (monster instanceof ArcherMonster) {
                    int monsterX = monster.getX();
                    int monsterY = monster.getY();
                    
                    // Check each direction (up, down, left, right) up to 2 squares
                    for (int i = 1; i <= 3; i++) {
                        // Up
                        if (monsterY - i >= 0 && !isPositionOccupied(monsterX, monsterY - i)) {
                            g.fillRect(monsterX * CELL_SIZE, 
                                     (monsterY - i) * CELL_SIZE, 
                                     CELL_SIZE, CELL_SIZE);
                        }
                        
                        // Down
                        if (monsterY + i < GRID_ROWS && !isPositionOccupied(monsterX, monsterY + i)) {
                            g.fillRect(monsterX * CELL_SIZE, 
                                     (monsterY + i) * CELL_SIZE, 
                                     CELL_SIZE, CELL_SIZE);
                        }
                        
                        // Left
                        if (monsterX - i >= 0 && !isPositionOccupied(monsterX - i, monsterY)) {
                            g.fillRect((monsterX - i) * CELL_SIZE, 
                                     monsterY * CELL_SIZE, 
                                     CELL_SIZE, CELL_SIZE);
                        }
                        
                        // Right
                        if (monsterX + i < GRID_COLUMNS && !isPositionOccupied(monsterX + i, monsterY)) {
                            g.fillRect((monsterX + i) * CELL_SIZE, 
                                     monsterY * CELL_SIZE, 
                                     CELL_SIZE, CELL_SIZE);
                        }
                    }
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX() / CELL_SIZE;
            int y = e.getY() / CELL_SIZE;

            Point clickPoint = new Point(x, y);
            Map<Point, GameObject> objects = currentHall.getObjects();

            int dx = Math.abs(((int)clickPoint.getX()) - hero.getX());
            int dy = Math.abs(((int)clickPoint.getY()) - hero.getY());
    
            if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)){

                if (objects.containsKey(clickPoint)) {
                    GameObject clickedObject = objects.get(clickPoint);
                    System.out.println("Clicked on object at: (" + ((int)clickPoint.getY()) + ", " + ((int)clickPoint.getX()) + ")");
                    handleObjectClick(clickedObject);
                }
            }
            
            
        }

        

        private void handleObjectClick(GameObject clickedObject) {
            // Determine if the object is a rune or empty
            String message;
            if (clickedObject.getX() == runePosition.x && clickedObject.getY() == runePosition.y) { 
                message = "You found the rune at: (" + clickedObject.getX() + ", " + clickedObject.getY() + ")!";
            } else {
                message = "Rune is not here. You clicked on an empty object at: (" + clickedObject.getX() + ", " + clickedObject.getY() + ").";
            }
        
            // Create a JOptionPane
            JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{}, // No buttons
                null
            );
        
            // Create a dialog with the JOptionPane
            JDialog dialog = optionPane.createDialog(this, "Object Clicked");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setModal(false); // Allow non-blocking behavior
            dialog.setVisible(true);
        
            // Set a timer to dispose of the dialog after 2 seconds
            Timer timer = new Timer(2000, e -> dialog.dispose());
            timer.setRepeats(false); // Ensure the timer runs only once
            timer.start();
        }
        

        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (isKeyPressed) return;
            isKeyPressed = true;

            Direction direction = null;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    direction = Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    direction = Direction.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    direction = Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    direction = Direction.RIGHT;
                    break;
            }

            if (direction != null) {
                int newX = hero.getX() + direction.getDx();
                int newY = hero.getY() + direction.getDy();

                // Check boundaries and prevent overlap
                if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS && !isPositionOccupied(newX, newY)) {
                    hero.move(direction);
                }
            }
            checkHeroMonsterCollision();

            System.out.println("Hero Position: X=" + hero.getX() + ", Y=" + hero.getY());
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            isKeyPressed = false; // Tuş bırakıldığında izin ver
        }

        @Override
        public void keyTyped(KeyEvent e) {}
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         GameScreen screen = new GameScreen();
    //         screen.setVisible(true);
    //     });
    // }
 
}
