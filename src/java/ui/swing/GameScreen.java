package ui.swing;

import controller.ScreenTransition;
import controller.TimerController;
import domain.behaviors.Direction;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import domain.monsters.ArcherMonster;
import domain.monsters.FighterMonster;
import domain.monsters.Monster;
import domain.monsters.WizardMonster;
import ui.utils.CursorUtils;
import ui.utils.SoundPlayerUtil;

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

    private Hero hero; 
    private List<Monster> monsters; // Monster listesi
    private Random random;

    private TimerController timerController;

    private Point runePosition;
    private BufferedImage runeImage;

    private int timeRemaining;

    private final ScreenTransition returnToGameOverScreen;
    private ArrayList<Hall> allHalls = new ArrayList<>();

    private Hall earthHall;
    private Hall waterHall;
    private Hall fireHall;
    private Hall airHall;
    private Hall currentHall;
    
    private GamePanel gamePanel;
    private JPanel sidePanel; // Side panel for inventory, timer, hearts, and buttons
    private JLabel[] heartLabels; // Array of heart icons for lives
    private JLabel timerLabel; // Timer display

    public GameScreen(ScreenTransition returnToGameOverScreen, ArrayList<Hall> allHalls) {

        this.returnToGameOverScreen = returnToGameOverScreen;
        this.allHalls = allHalls;
        this.earthHall = allHalls.get(0);
        this.waterHall = allHalls.get(1);
        this.fireHall = allHalls.get(2);
        this.airHall = allHalls.get(3);

        this.currentHall = earthHall;
        
        timeRemaining = 50;

        this.timerController = TimerController.getInstance();
        initializeTimers();

        setTitle("Game Screen");
        setSize(GRID_COLUMNS * CELL_SIZE + 250, GRID_ROWS * CELL_SIZE + 50); // Extra width for side panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setCursor(CursorUtils.createCustomCursor("src/resources/images/pointer_a.png"));
        hero = new Hero(0, 0); // Hero starts at (0,0) // #TODO: NEEDS TO BE RANDOMIZED
        monsters = new ArrayList<>();
        random = new Random();
        loadEarthHall();
        //runePosition = new Point(random.nextInt(GRID_COLUMNS), random.nextInt(GRID_ROWS));
        initializeRunePosition();

       //loadRuneImage();
        spawnMonsters();

        // Initialize the game panel and side panel
        gamePanel = new GamePanel(); // grid
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(100, 70, 83)); // Lighter purple background
        sidePanel.setPreferredSize(new Dimension(250, getHeight())); // Set width of the side pane
        
        // Set up the side panel
        setupSidePanel();

        // Add panels to the frame
        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        setVisible(true);
    }

    private void setupSidePanel() {
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(100, 70, 83)); // Lighter purple background
        sidePanel.setPreferredSize(new Dimension(250, getHeight())); // Set width of the side panel
    
        // Pause and exit buttons (top of the side panel)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(100, 70, 83)); // Match side panel color
    
        JButton pauseButton = createButton("src/resources/images/pause_button.png", e -> {
            // Pause button action (non-functional for now)
            System.out.println("Pause button clicked");
        });
    
        JButton exitButton = createButton("src/resources/images/exit_button.png", e -> {
            // Exit button action
            returnToGameOverScreen.execute();
        });
    
        buttonPanel.add(pauseButton);
        buttonPanel.add(exitButton);
        sidePanel.add(buttonPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
    
        // Timer display (below buttons)
        timerLabel = new JLabel("Time: " + timeRemaining + " seconds");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(timerLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
    
        // Hearts for lives (below timer)
        JPanel heartsPanel = new JPanel();
        heartsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        heartsPanel.setBackground(new Color(100, 70, 83)); // Match side panel color
        heartLabels = new JLabel[4]; // Max 4 hearts
        for (int i = 0; i < heartLabels.length; i++) {
            heartLabels[i] = new JLabel(new ImageIcon("src/resources/images/heart_full.png")); // Default full heart
            heartsPanel.add(heartLabels[i]);
        }
        updateHearts(); // Update hearts based on initial lives
        sidePanel.add(heartsPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
    
        // Inventory (below hearts)
        JLabel inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryLabel.setForeground(Color.WHITE);
        inventoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(inventoryLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
    
        // Inventory chest icon
        JLabel chestIcon = new JLabel(new ImageIcon("src/resources/images/Inventory.png"));
        chestIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(chestIcon);
        sidePanel.add(Box.createVerticalGlue()); // Push content to the top
    }

    private JButton createButton(String imagePath, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(new ImageIcon(imagePath));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        return button;
    }

    private void updateHearts() {
        int lives = hero.getLives();
        for (int i = 0; i < heartLabels.length; i++) {
            if (i < lives) {
                heartLabels[i].setIcon(new ImageIcon("src/resources/images/heart_full.png"));
            } else { // bos iconlari suanda olmayan bir seyle dolduruyor bu alana bir daha bakilmali
                heartLabels[i].setIcon(new ImageIcon("src/resources/images/heart_empty.png")); 
            }
        }
    }
    // usage of Timer Controller, instead of declaring all the time variables we have that class
    private void initializeTimers() {
        timerController.initializeGameTimers(
            () -> moveMonsters(),
            () -> addRandomMonster(),
            () -> teleportRune(),
            () -> checkArcherAttacks(),
            () -> updateTime()
        );
        timerController.startTimers();
    }

    private void updateTime() {
        timeRemaining--;
        timerLabel.setText("Time: " + timeRemaining + " seconds");

        if (timeRemaining <= 0) {
            stopGame();
            SoundPlayerUtil.playGameOverJingle();
            returnToGameOverScreen.execute();
        }
    }

    private void loadEarthHall() {
        Map<Point, GameObject> earthObjects = earthHall.getObjects();
    
        for (Map.Entry<Point, GameObject> entry : earthObjects.entrySet()) {
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
                    updateHearts();
                    System.out.println("Hero has been attacked by a Fighter monster, " + hero.getLives() + " lives remaining");
                    if (hero.getLives() <= 0) {
                        System.out.println("Hero has died. Returning to Main Menu...");
                        stopGame();
                        SoundPlayerUtil.playGameOverJingle();
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
                        gamePanel.showHeroDamagedEffect();
                        updateHearts();
                        System.out.println("Hero has been shot by an Archer monster, " + hero.getLives() + " lives remaining");
                        if (hero.getLives() <= 0) {
                            System.out.println("Hero has died. Returning to Main Menu...");
                            stopGame();
                            SoundPlayerUtil.playGameOverJingle();
                            returnToGameOverScreen.execute();
                        }else{
                            SoundPlayerUtil.playHurtSound();
                        }
                        
                    }
                }
            }
        }
    }
    
    private void stopGame() {
        timerController.cleanup();
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

        private void showHeroDamagedEffect() {
            try {
                BufferedImage damagedImage = ImageIO.read(new File("src/resources/images/playerDamaged.png"));
                heroImage = damagedImage; 
                repaint(); 
            } catch (IOException e) {
                System.err.println("Failed to load heroDamaged image.");
                e.printStackTrace();
            }
        
            Timer resetImageTimer = new Timer(500, e -> { // Reset image and timer after 0.5 seconds
                try {
                    heroImage = ImageIO.read(new File("src/resources/images/player.png"));
                    repaint();
                } catch (IOException ex) {
                    System.err.println("Failed to reload player image.");
                    ex.printStackTrace();
                }
            });
            resetImageTimer.setRepeats(false); // Timer runs only once
            resetImageTimer.start();
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

                    SoundPlayerUtil.playMoveSound();
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
