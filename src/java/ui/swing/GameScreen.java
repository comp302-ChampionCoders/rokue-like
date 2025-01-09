package ui.swing;

import controller.HallController;
import controller.ScreenTransition;
import controller.TimerController;
import controller.SpawnController;
import domain.behaviors.Direction;
import domain.enchantments.*;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;
import domain.monsters.ArcherMonster;
import domain.monsters.FighterMonster;
import domain.monsters.Monster;
import domain.monsters.WizardMonster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import ui.utils.CursorUtils;
import ui.utils.SoundPlayerUtil;


public class GameScreen extends JFrame {
    private final int GRID_ROWS = 12;
    private final int GRID_COLUMNS = 16;
    private final int CELL_SIZE = 50;

    private Hero hero; 
    private Rune rune;
    private List<Monster> monsters; 
    private List<Enchantment> enchantments;
    private Random random;

    private TimerController timerController;
    private SpawnController spawnController;

    private Point runePosition;
    private BufferedImage runeImage;

    private int timeRemaining;

    private final ScreenTransition returnToGameOverScreen;

    private GamePanel gamePanel;
    private JPanel sidePanel; 
    private JLabel[] heartLabels; 
    private JLabel timerLabel; 
    private Font timerFont;
    private HallController hallController;

    public GameScreen(ScreenTransition returnToGameOverScreen, HallController hallController) {

        this.returnToGameOverScreen = returnToGameOverScreen;
        this.hallController = hallController;
        initializeHeroPosition();
        initializeRunePosition();
        this.timerController = TimerController.getInstance();
        this.spawnController = SpawnController.getInstance();
        initializeTimers();
        timeRemaining = timerController.getRemainingGameTime(hallController.getCurrentHall().getHallType());

        try {
            timerFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/ThaleahFat.ttf")) .deriveFont(34f);
        } catch (FontFormatException | IOException e1) {
            timerFont = new Font("Arial", Font.BOLD, 24);
            e1.printStackTrace();
        }

        setSize(Toolkit.getDefaultToolkit().getScreenSize());

        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("win")) {
            configureForWindows();
        } else if (osName.contains("mac")) {
            configureForMacOS();
        } else {
            configureForOther();
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        setLocationRelativeTo(null);
        setCursor(CursorUtils.createCustomCursor("src/resources/images/pointer_a.png"));
        monsters = new ArrayList<>();
        random = new Random();
        enchantments = new ArrayList<>();
        loadHall();

       //loadRuneImage();

        gamePanel = new GamePanel(); 
        sidePanel = new JPanel();

        setupSidePanel();

        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);


        setVisible(true);
    }

    private void configureForMacOS(){
        setTitle("Game Screen");
        setUndecorated(false); 
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void configureForWindows(){
        setTitle("Game Screen");
        setUndecorated(true); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void configureForOther(){
        setTitle("GameScreenM");
        setUndecorated(false); 
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupSidePanel() {

        sidePanel.setLayout(null); 
        sidePanel.setBackground(new Color(66, 40, 53)); 
        sidePanel.setPreferredSize(new Dimension(350, getHeight()));
    
        int gridHeight = GRID_ROWS * CELL_SIZE; 
        int gridOffsetY = (getHeight() - gridHeight) / 2; 
    
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(new Color(100, 70, 83)); 
        containerPanel.setBounds(0, gridOffsetY, 200, gridHeight);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
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

        pauseButton.addActionListener(e -> 
        {System.out.println("Pause button clicked");
        SoundPlayerUtil.playClickSound();}
        );
    
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
            SoundPlayerUtil.playClickSound();
            returnToGameOverScreen.execute();
        });
    
        JButton nextHallButton = new JButton("Next");

        nextHallButton.addActionListener(e -> {
            if(hallController.canGoNextHall()){
                goNextHall();
            }
        });

        buttonPanel.add(nextHallButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(exitButton);
    
        // Timer display
        JLabel timerHeader = new JLabel();
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
        heartsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        heartsPanel.setBackground(new Color(100, 70, 83));
        heartLabels = new JLabel[4];
        for (int i = 0; i < heartLabels.length; i++) {
            heartLabels[i] = new JLabel(new ImageIcon("src/resources/images/heart_full.png"));
            heartsPanel.add(heartLabels[i]);
        }
        updateHearts();
    
        // Inventory chest icon
        JLabel chestIcon = new JLabel(new ImageIcon("src/resources/images/Inventory2x_3.png"));
        chestIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        containerPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        containerPanel.add(buttonPanel); 
        containerPanel.add(timerHeader);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
        containerPanel.add(timerLabel);
        containerPanel.add(heartsPanel);
        containerPanel.add(chestIcon);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 80)));
        containerPanel.add(Box.createVerticalGlue()); 

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

    // heart controller eklenmeli
    private void updateHearts() {
        int lives = hero.getLives();
        for (int i = 0; i < heartLabels.length; i++) {
            if (i < lives) {
                heartLabels[i].setIcon(new ImageIcon("src/resources/images/heart_full.png"));
            } else { 
                heartLabels[i].setIcon(new ImageIcon("src/resources/images/heart_empty.png")); 
            }
        }
    }
    // usage of Timer Controller, instead of declaring all the time variables we have that class
    private void initializeTimers() {
        timerController.initializeGameTimers(
            () -> moveMonsters(),
            () -> spawnController.spawnMonster(hallController.getCurrentHall()),
            () -> teleportRune(),
            () -> checkArcherAttacks(),
            () -> updateTime(),
            () -> spawnEnchantment(),
            () -> removeEnchantment()
        );
        timerController.startTimers();
    }

    private void initializeHeroPosition() {
        hallController.updateHero();
        hero = hallController.getHero();
        //terminaldeki H gride yazılacak farklı bi classta yapılıp buraya çekilmeli
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

    private void loadHall() {
        Map<Point, GameObject> earthObjects = hallController.getCurrentHall().getObjects();
        hallController.getCurrentHall().displayGrid();
        System.out.println(hallController.getCurrentHall().getObjects());
    
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

    private void goNextHall(){ 
        hallController.goNextHall();
        timerController.cleanup();
        timerController.resetGameTime();
        initializeHeroPosition();
        initializeTimers();
        timeRemaining = timerController.getRemainingGameTime(hallController.getCurrentHall().getHallType());

        initializeRunePosition();
        monsters = new ArrayList<>();
        random = new Random();
        enchantments = new ArrayList<>();
        loadHall();
    }
    
    private void initializeRunePosition() {
        hallController.updateRune();
        rune = hallController.getRune();
        Point point = new Point(rune.getX(), rune.getY());
        runePosition = point;
    }
    

    private void loadRuneImage() {
        try {
            runeImage = ImageIO.read(new File("src/resources/images/rune.png"));
        } catch (IOException | NullPointerException e) {
            System.err.println("Failed to load rune image.");
            e.printStackTrace();
        }
    }
    
    //bu classtan çıkarılmalı
    private void moveMonsters() {
        Direction[] directions = Direction.values();
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

    // addrandom monster'a bağlı. bu classtan çıakrılmalı
    private boolean isWithinHeroProximity(int x, int y) {
        int dx = Math.abs(x - hero.getX());
        int dy = Math.abs(y - hero.getY());
        return dx <= 3 && dy <= 3; // Hero'nun 3x3 alanını kontrol eder
    }

    // bu classtan çıakrılmalı
    private boolean isPositionOccupied(int x, int y) {
        if (hero != null) {
            if (hero.getX() == x && hero.getY() == y) return true;
        }
        if (runePosition != null){
            if (runePosition.x == x && runePosition.y == y) return true;
        }
        for (Monster monster : monsters) {
            if (monster.getX() == x && monster.getY() == y) return true;
        }
        for(GameObject obj : hallController.getCurrentHall().getObjects().values()){
            if(obj.getX() == x && obj.getY() == y){
                return true;
            }
        }
        return false;
    }


    // bu classtan çıakrılmalı
    private void teleportRune() {
        boolean wizardExists = monsters.stream().anyMatch(m -> m instanceof WizardMonster);
        if (wizardExists) {
            Map<Point, GameObject> objects = hallController.getCurrentHall().getObjects();
    
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


    // bu classtan çıakrılmalı
    private void checkHeroMonsterCollision() {
        for (Monster monster : monsters) {
            if (monster instanceof FighterMonster) {
                // Find distances of x's and y's
                int dx = Math.abs(monster.getX() - hero.getX());
                int dy = Math.abs(monster.getY() - hero.getY());
    
                if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                    hero.reduceLife();
                    hero.reduceLife();
                    hero.reduceLife();
                    hero.reduceLife(); // kills even if it has 4 lives
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


    // bu classtan çıakrılmalı
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


    // bu classtan çıakrılmalı
    // Add this new method for archer attacks
    private void checkArcherAttacks() {
        for (Monster monster : monsters) {
            if (monster instanceof ArcherMonster) {

                if (!hero.isVisible()) { // for cloak of protection
                    System.out.println("Hero is invisible. ArcherMonster cannot attack.");
                    continue;
                }
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

    private void spawnEnchantment() {
        Enchantment enchantment = spawnController.spawnEnchantment(hallController.getCurrentHall());
        enchantments.add(enchantment);
        repaint();
    }

    private void removeEnchantment(){
        spawnController.removeEnchantment(null);
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
            drawTopAndSideWalls(g, offsetX, offsetY);
            drawArcherRanges(g, offsetX, offsetY);
            drawHallObjects(g, offsetX, offsetY);
            drawHero(g, offsetX, offsetY);
            drawMonsters(g, offsetX, offsetY);
            drawRune(g, offsetX, offsetY);
            drawEnchantments(g, offsetX, offsetY);

            for (Enchantment enchantment : enchantments) { // needs to be moved out of this method
                if (enchantment instanceof Reveal) {
                    Reveal reveal = (Reveal) enchantment;
                    if (reveal.hasHighlight()) {
                        int highlightX = reveal.getHighlightX();
                        int highlightY = reveal.getHighlightY();
        
                        g.setColor(new Color(0, 255, 0, 128)); // Transparent green
                        for (int dx = -2; dx <= 1; dx++) {
                            for (int dy = -2; dy <= 1; dy++) {
                                int drawX = highlightX + dx;
                                int drawY = highlightY + dy;
                                if (drawX >= 0 && drawX < GRID_COLUMNS && drawY >= 0 && drawY < GRID_ROWS) {
                                    g.fillRect(
                                        offsetX + drawX * CELL_SIZE,
                                        offsetY + drawY * CELL_SIZE,
                                        CELL_SIZE,
                                        CELL_SIZE
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }

        private void drawGrid(Graphics g, int offsetX, int offsetY) {
            g.setColor(Color.GRAY);
            for (int i = 0; i <= GRID_ROWS; i++) {
                g.drawLine(offsetX, offsetY + i * CELL_SIZE, offsetX + GRID_COLUMNS * CELL_SIZE, offsetY + i * CELL_SIZE); 
            }
            for (int j = 0; j <= GRID_COLUMNS; j++) {
                g.drawLine(offsetX + j * CELL_SIZE, offsetY, offsetX + j * CELL_SIZE, offsetY + GRID_ROWS * CELL_SIZE); 
            }
        }

        private void drawHero(Graphics g, int offsetX, int offsetY) {
            if (heroImage != null) {
                g.drawImage(heroImage, offsetX + hero.getX() * CELL_SIZE, offsetY + hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.RED);
                g.fillRect(offsetX + hero.getX() * CELL_SIZE, offsetY + hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        private void updateHeroImage() {
            try {
                if (hero.isDamaged()) { // Hero hasar aldıysa
                    if (hero.getDirection().equals("RIGHT")) {
                        heroImage = ImageIO.read(new File("src/resources/images/playerDamaged.png"));
                    } else {
                        heroImage = ImageIO.read(new File("src/resources/images/playerDamagedReversed.png"));
                    }
                } else if (hero.isCloaked()) { // Hero cloak etkisindeyse
                    if (hero.getDirection().equals("RIGHT")) {
                        heroImage = ImageIO.read(new File("src/resources/images/playerCloak2.png"));
                    } else {
                        heroImage = ImageIO.read(new File("src/resources/images/playerCloak2_reversed.png"));
                    }
                } else { // Normal durumda
                    if (hero.getDirection().equals("RIGHT")) {
                        heroImage = ImageIO.read(new File("src/resources/images/player.png"));
                    } else {
                        heroImage = ImageIO.read(new File("src/resources/images/playerReversed.png"));
                    }
                }
                repaint(); // Görseli yeniden çiz
            } catch (IOException e) {
                System.err.println("Failed to update hero image.");
                e.printStackTrace();
            }
        }
        

        private void showHeroCloakedEffect() {
            hero.setIsCloaked(true);
            updateHeroImage(); 
            repaint();
        }
        

        private void showHeroDamagedEffect() {
            hero.setIsDamaged(true); 
            updateHeroImage();
            repaint();
        
            Timer resetImageTimer = new Timer(500, e -> {
                hero.setIsDamaged(false); // Hasar durumunu sıfırla
                updateHeroImage(); // Normal duruma dön
            });
            resetImageTimer.setRepeats(false);
            resetImageTimer.start();
        }

        private void drawTopAndSideWalls(Graphics g, int offsetX, int offsetY) {
            int wallOffset = 16;
            int topWallWidth = GRID_COLUMNS * CELL_SIZE; 
            int sideWallWidth = wallOffset;
            int sideWallHeight = GRID_ROWS * CELL_SIZE + (int)(1.5 * CELL_SIZE) - 20;
        
            try {
                BufferedImage sideWallImage = ImageIO.read(new File("src/resources/images/sidewall.png"));
                BufferedImage topWallImage = ImageIO.read(new File("src/resources/images/topwall.png"));
        
                Hall currentHall = hallController.getCurrentHall();
                Hall.HallType hallType = currentHall.getHallType();
        
                // Adjust wall properties based on hall type if needed
                Color hallColor;
                switch (hallType) {
                    case EARTH:
                        hallColor = new Color(34, 139, 34);
                        break;
                    case WATER:
                        hallColor = new Color(30, 144, 255);
                        break;
                    case FIRE:
                        hallColor = new Color(255, 69, 0);
                        break;
                    case AIR:
                        hallColor = new Color(135, 206, 250);
                        break;
                    default:
                        hallColor = Color.GRAY;
                }
        
                // Top wall positions
                int[][] topWallPositions = {
                    {offsetX, offsetY - CELL_SIZE},
                };
        
                // Side wall positions
                int[][] sideWallPositions = {
                    {offsetX - wallOffset, offsetY - CELL_SIZE}, // Extended upward to align with top wall
                    {offsetX + GRID_COLUMNS * CELL_SIZE, offsetY - CELL_SIZE} // Extended upward to align with top wall
                };
        
        
                // Draw top walls
                for (int[] pos : topWallPositions) {
                    g.drawImage(topWallImage, pos[0], pos[1], topWallWidth, CELL_SIZE, null);
                }
        
                // Draw side walls
                for (int[] pos : sideWallPositions) {
                    g.drawImage(sideWallImage, pos[0], pos[1], sideWallWidth, sideWallHeight, null);
                }
            } catch (IOException e) {
                System.err.println("Failed to load wall images: " + e.getMessage());
                e.printStackTrace();
            }
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
                // g.setColor(Color.YELLOW); // for test
                g.fillRect(offsetX + runePosition.x * CELL_SIZE, offsetY + runePosition.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        
        private void drawHallObjects(Graphics g, int offsetX, int offsetY) {
            Map<Point, GameObject> hallObjects = hallController.getCurrentHall().getObjects();
            for (Map.Entry<Point, GameObject> entry : hallObjects.entrySet()) {
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
        
        private void drawEnchantments(Graphics g, int offsetX, int offsetY) {
            for (Enchantment enchantment : enchantments) {
                if (enchantment != null){
                    BufferedImage enchantmentImage = enchantment.getImage();
                    if (enchantmentImage != null) {
                        g.drawImage(enchantmentImage, 
                                    offsetX + enchantment.getX() * CELL_SIZE, 
                                    offsetY + enchantment.getY() * CELL_SIZE, 
                                    CELL_SIZE, CELL_SIZE, 
                                    this);
                    } else {
                        g.setColor(Color.MAGENTA); // Default color for missing image
                        g.fillRect(offsetX + enchantment.getX() * CELL_SIZE, 
                                offsetY + enchantment.getY() * CELL_SIZE, 
                                CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int offsetX = (panelWidth - (GRID_COLUMNS * CELL_SIZE)) / 2; // X ofset
            int offsetY = (panelHeight - (GRID_ROWS * CELL_SIZE)) / 2;// Y ofset

            int x = (e.getX() - offsetX) / CELL_SIZE;
            int y = (e.getY() - offsetY) / CELL_SIZE;

            if (x < 0 || x >= GRID_COLUMNS || y < 0 || y >= GRID_ROWS) {

                return;
            }

            Point clickPoint = new Point(x, y);

            // Check if an enchantment was clicked
            for (Enchantment enchantment : enchantments) {
                if (enchantment.getX() == x && enchantment.getY() == y && enchantment.isAvailable()) {
                    System.out.println("Clicked on enchantment at: (" + x + ", " + y + ")");
                    handleEnchantmentClick(enchantment);
                    return; // Exit after handling the enchantment click
                }
            }

            Map<Point, GameObject> objects = hallController.getCurrentHall().getObjects();

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

        private void handleEnchantmentClick(Enchantment clickedEnchantment) {
            String enc = clickedEnchantment.getType();
            if (enc == "Extra Time") {
                activeExtraTime();
                System.out.println("5 seconds added!");
            }
            else if (enc == "Extra Life") {
                activateExtraLife();
                updateHearts();
            }
            else {
                hero.getInventory().addItem(clickedEnchantment);
                System.out.println("Added enchantment " + clickedEnchantment.getType() + " to inventory.");
                
            }
            enchantments.remove(clickedEnchantment);
            hallController.getCurrentHall().removeGridElement(clickedEnchantment.getX(), clickedEnchantment.getY());
            clickedEnchantment.disappear();
            System.out.println(hero.getInventory().getInventoryContents());
            System.out.println(hero.getInventory().getTotalCount());
        }

        private void activeExtraTime() {
            timeRemaining+=5;
        }

        private void activateExtraLife() {
            if (hero.getLives() <= 4) {
                hero.addLife();
                System.out.println("Life was gained!");
            }
            System.out.println("Max lives!");
        }

        private void activateReveal() {
            for (Enchantment enchantment : enchantments) {
                if (enchantment instanceof Reveal) {
                    Reveal reveal = (Reveal) enchantment;
                    System.out.println("Setting highlight center for Reveal...");
                    reveal.setHighlightCenter(runePosition.x, runePosition.y);
                    repaint(); // Trigger repaint to show the highlight
                    break;
                }
            }
        }
        
        private void activateCloakOfProtection() {
            hero.toggleVisibility(); // Set isVisible to false
            System.out.println("Hero is now invisible to archers for 20 seconds.");
        
            // Schedule the visibility to reset after 20 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(20000); // 20 seconds in milliseconds
                    hero.toggleVisibility(); // Reset isVisible to true
                    System.out.println("Hero is now visible to archers again.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
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
                    hero.setDirection("LEFT");
                    direction = Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    hero.setDirection("RIGHT");
                    direction = Direction.RIGHT;
                    break;
                case KeyEvent.VK_R: // Reveal
                    System.out.println("R key pressed. Checking for Reveal...");
                    if (hero.getInventory().hasItem("Reveal")) {
                        System.out.println("Reveal found. Using enchantment...");
                        hero.getInventory().useItem("Reveal");
                        activateReveal();
                    } else {
                        System.out.println("No Reveal enchantment found in inventory.");
                    }
                    break;
                case KeyEvent.VK_P: // Cloak of Protection
                    System.out.println("P key pressed. Checking for Cloak of Protection...");
                    if (hero.getInventory().hasItem("Cloak of Protection")) {
                        SoundPlayerUtil.playClothSound();
                        System.out.println("Using Cloak of Protection...");
                        hero.getInventory().useItem("Cloak of Protection");
                        activateCloakOfProtection();
                        gamePanel.showHeroCloakedEffect();
                    } else {
                        System.out.println("No Cloak of Protection found in inventory.");
                    }
                    break;
                
            }

            if (direction != null) {
                int newX = hero.getX() + direction.getDx();
                int newY = hero.getY() + direction.getDy();

                // Check boundaries and prevent overlap
                if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS && !isPositionOccupied(newX, newY)) {
                    hallController.moveHero(direction);
                    updateHeroImage();
                    SoundPlayerUtil.playMoveSound();
                }
            }
            checkHeroMonsterCollision();

            System.out.println("Hero Position: X=" + hero.getX() + ", Y=" + hero.getY());
            repaint();
            hallController.getCurrentHall().displayGrid(); // FOR TEST
        }

        @Override
        public void keyReleased(KeyEvent e) {
            isKeyPressed = false; 
        }

        @Override
        public void keyTyped(KeyEvent e) {}
    }
}
