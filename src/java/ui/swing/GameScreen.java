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
import java.awt.event.MouseAdapter;
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
    private final int CELL_SIZE = 50;

    private Hero hero; 
    private List<Monster> monsters; 
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
    private Font timerFont;

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

        try {
            timerFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/ThaleahFat.ttf")) .deriveFont(32f);
        } catch (FontFormatException | IOException e1) {
            timerFont = new Font("Arial", Font.BOLD, 24);
            e1.printStackTrace();
        }

        setUndecorated(true); 
        setTitle("Game Screen");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
    
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            System.err.println("Full Screen Not Supported");
            setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }
        //setSize(Toolkit.getDefaultToolkit().getScreenSize());
        
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

        gamePanel = new GamePanel(); 
        sidePanel = new JPanel();

        setupSidePanel();

        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);


        setVisible(true);
    }

    private void setupSidePanel() {

        sidePanel.setLayout(null); 
        sidePanel.setBackground(new Color(66, 40, 53)); 
        sidePanel.setPreferredSize(new Dimension(350, getHeight()));

        //sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));
    
        int gridHeight = GRID_ROWS * CELL_SIZE; 
        int gridOffsetY = (getHeight() - gridHeight) / 2; 
    
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(new Color(100, 70, 83)); 
        containerPanel.setBounds(0, gridOffsetY, 200, gridHeight);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(100, 70, 83));
    
        JButton pauseButton = createButton("src/resources/images/pause_button.png");

        pauseButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                pauseButton.setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0137.png"));;
            }
            public void mouseExited(MouseEvent e) {
                pauseButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        pauseButton.addActionListener(e -> System.out.println("Pause button clicked"));
    
        JButton exitButton = createButton("src/resources/images/exit_button.png");

        exitButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                exitButton.setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0137.png"));;
            }
            public void mouseExited(MouseEvent e) {
                exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        exitButton.addActionListener(e -> {
            stopGame();
            returnToGameOverScreen.execute();
        });
    
        buttonPanel.add(pauseButton);
        buttonPanel.add(exitButton);
    
        // Timer display
        // Timer header (icon + "Time")
        JLabel timerHeader = new JLabel();
        //timerHeader.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        timerHeader.setIcon(resizeIcon(new ImageIcon("src/resources/images/clock_icon.png"), 32, 32));
        
        timerHeader.setText("TIME: ");
        timerHeader.setFont(timerFont); 
        timerHeader.setForeground(Color.WHITE); 
        timerHeader.setHorizontalTextPosition(SwingConstants.RIGHT); 
        timerHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Timer Label (kalan süre)
        timerLabel = new JLabel(timeRemaining + " seconds");
        timerLabel.setFont(timerFont); 
        timerLabel.setForeground(Color.WHITE); 
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    
        // Hearts panel
        JPanel heartsPanel = new JPanel();
        heartsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        heartsPanel.setBackground(new Color(100, 70, 83));
        heartLabels = new JLabel[4];
        for (int i = 0; i < heartLabels.length; i++) {
            heartLabels[i] = new JLabel(new ImageIcon("src/resources/images/heart_full.png"));
            heartsPanel.add(heartLabels[i]);
        }
        updateHearts();
        heartsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
    
        // Inventory label
      /*   JLabel inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryLabel.setForeground(Color.WHITE);8
        inventoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);*/
    
        // Inventory chest icon
        JLabel chestIcon = new JLabel(new ImageIcon("src/resources/images/Inventory.png"));
        chestIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
    

        containerPanel.add(buttonPanel); 
        containerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        containerPanel.add(timerHeader);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
        containerPanel.add(timerLabel);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        containerPanel.add(heartsPanel);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        //containerPanel.add(inventoryLabel);
        //containerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        containerPanel.add(chestIcon);
        containerPanel.add(Box.createVerticalGlue()); // Alt boşluk
    
        // SidePanel'e container panelini ekleme
        sidePanel.add(containerPanel);
    }
    

    private JButton createButton(String imagePath) {
        JButton button = new JButton(new ImageIcon(imagePath));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        return button;
    }

    private Icon resizeIcon(Icon icon, int width, int height) {
        Image img = ((ImageIcon) icon).getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
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
        timerLabel.setText(timeRemaining + " seconds");

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

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int offsetX = (panelWidth - (GRID_COLUMNS * CELL_SIZE)) / 2; 
            int offsetY = (panelHeight - (GRID_ROWS * CELL_SIZE)) / 2; 

            drawGrid(g, offsetX, offsetY);
            drawArcherRanges(g, offsetX, offsetY);
            drawEarthHallObjects(g, offsetX, offsetY);
            drawHero(g, offsetX, offsetY);
            drawMonsters(g, offsetX, offsetY);
            drawRune(g, offsetX, offsetY);
        }

        // Grid çizimi için güncellenmiş metot
        private void drawGrid(Graphics g, int offsetX, int offsetY) {
            g.setColor(Color.GRAY);
            for (int i = 0; i <= GRID_ROWS; i++) {
                g.drawLine(offsetX, offsetY + i * CELL_SIZE, offsetX + GRID_COLUMNS * CELL_SIZE, offsetY + i * CELL_SIZE); 
            }
            for (int j = 0; j <= GRID_COLUMNS; j++) {
                g.drawLine(offsetX + j * CELL_SIZE, offsetY, offsetX + j * CELL_SIZE, offsetY + GRID_ROWS * CELL_SIZE); 
            }
        }

        // Kahramanı çizerken offset kullanımı
        private void drawHero(Graphics g, int offsetX, int offsetY) {
            if (heroImage != null) {
                g.drawImage(heroImage, offsetX + hero.getX() * CELL_SIZE, offsetY + hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.RED);
                g.fillRect(offsetX + hero.getX() * CELL_SIZE, offsetY + hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
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
        
        
        

        private void drawMonsters(Graphics g, int offsetX, int offsetY) {
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
                    g.drawImage(monsterImage, offsetX + monster.getX() * CELL_SIZE, offsetY + monster.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(offsetX + monster.getX() * CELL_SIZE, offsetY + monster.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        
        private void drawRune(Graphics g, int offsetX, int offsetY) {
            if (runeImage != null) {
                g.drawImage(runeImage, offsetX + runePosition.x * CELL_SIZE, offsetY + runePosition.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(offsetX + runePosition.x * CELL_SIZE, offsetY + runePosition.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        
        private void drawEarthHallObjects(Graphics g, int offsetX, int offsetY) {
            Map<Point, GameObject> earthObjects = earthHall.getObjects();
            for (Map.Entry<Point, GameObject> entry : earthObjects.entrySet()) {
                Point position = entry.getKey();
                GameObject gameObject = entry.getValue();
                if (gameObject.getImage() != null) {
                    g.drawImage(
                        gameObject.getImage(),
                        offsetX + position.x * CELL_SIZE,
                        offsetY + position.y * CELL_SIZE,
                        CELL_SIZE,
                        CELL_SIZE,
                        this
                    );
                }
            }
        }
        
        private void drawArcherRanges(Graphics g, int offsetX, int offsetY) {
            g.setColor(new Color(255, 0, 0, 80));  // Semi-transparent red
        
            for (Monster monster : monsters) {
                if (monster instanceof ArcherMonster) {
                    int monsterX = monster.getX();
                    int monsterY = monster.getY();
        
                    for (int i = 1; i <= 3; i++) {
                        // Up
                        if (monsterY - i >= 0 && !isPositionOccupied(monsterX, monsterY - i)) {
                            g.fillRect(offsetX + monsterX * CELL_SIZE, offsetY + (monsterY - i) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        // Down
                        if (monsterY + i < GRID_ROWS && !isPositionOccupied(monsterX, monsterY + i)) {
                            g.fillRect(offsetX + monsterX * CELL_SIZE, offsetY + (monsterY + i) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        // Left
                        if (monsterX - i >= 0 && !isPositionOccupied(monsterX - i, monsterY)) {
                            g.fillRect(offsetX + (monsterX - i) * CELL_SIZE, offsetY + monsterY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        // Right
                        if (monsterX + i < GRID_COLUMNS && !isPositionOccupied(monsterX + i, monsterY)) {
                            g.fillRect(offsetX + (monsterX + i) * CELL_SIZE, offsetY + monsterY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                    }
                }
            }
        }
        

        @Override
        public void mouseClicked(MouseEvent e) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int offsetX = (panelWidth - (GRID_COLUMNS * CELL_SIZE)) / 2 - 50; // X ofset
            int offsetY = (panelHeight - (GRID_ROWS * CELL_SIZE)) / 2;        // Y ofset

            int x = (e.getX() - offsetX) / CELL_SIZE;
            int y = (e.getY() - offsetY) / CELL_SIZE;


            if (x < 0 || x >= GRID_COLUMNS || y < 0 || y >= GRID_ROWS) {

                return;
            }

            Point clickPoint = new Point(x, y);
            Map<Point, GameObject> objects = currentHall.getObjects();

            int dx = Math.abs(clickPoint.x - hero.getX());
            int dy = Math.abs(clickPoint.y - hero.getY());

            // Kahramanın 1 birim yakınında olma kontrolü
            if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                if (objects.containsKey(clickPoint)) {
                    GameObject clickedObject = objects.get(clickPoint);
                    System.out.println("Clicked on object at: (" + clickPoint.y + ", " + clickPoint.x + ")");
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
