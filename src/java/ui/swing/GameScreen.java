package ui.swing;

import controller.*;
import domain.behaviors.Collectible;
import domain.behaviors.Direction;
import domain.behaviors.GridElement;
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
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

import ui.utils.CursorUtils;
import ui.utils.SoundPlayerUtil;
import ui.utils.TaskBarIconUtil;
import utils.SaveLoadUtil;


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
    private final ScreenTransition onToWinScreen;

    private GamePanel gamePanel;
    private JPanel sidePanel; 
    private JLabel[] heartLabels;
    private JLabel[] enchantmentLabels = new JLabel[6];
    private JLabel timerLabel; 
    private Font timerFont;
    private HallController hallController;
    private boolean isPaused = false;
    public static boolean isLoaded = false;




    public GameScreen(ScreenTransition returnToGameOverScreen, ScreenTransition onToWinScreen,HallController hallController) {

        this.returnToGameOverScreen = returnToGameOverScreen;
        this.onToWinScreen = onToWinScreen;
        this.hallController = hallController;

        monsters = new ArrayList<>();
        enchantments = new ArrayList<>();

        Map<Point, GridElement> objects = hallController.getCurrentHall().getGridElements();

        for (Map.Entry<Point, GridElement> entry : objects.entrySet()) {
            Point position = entry.getKey();
            GridElement element = entry.getValue();

            if (element instanceof Enchantment) {
                Enchantment enchantment = (Enchantment) element;
                enchantments.add(enchantment);
            }
            else if (element instanceof Monster){
                Monster monster = (Monster) element;
                monsters.add(monster);
            }
        }

        initializeHeroPosition();
        initializeRunePosition();
        this.timerController = TimerController.getInstance();
        this.spawnController = SpawnController.getInstance();
        initializeTimers();

        if(!isLoaded){
            timeRemaining = hallController.getCurrentHall().getInitialTime();
        }else{
            timeRemaining = hallController.getCurrentHallRemainingTime();
        }


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

        random = new Random();
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
        TaskBarIconUtil.setMacTaskbarIcon();
        setCursor(CursorUtils.createCustomCursor("src/resources/images/pointer_a.png"));
    }

    public void configureForWindows(){
        setTitle("Game Screen");
        setUndecorated(true); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TaskBarIconUtil.setWindowsTaskbarIcon(this);
        setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0168.png"));
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


        JButton saveButton = createButton("src/resources/images/save_button.png");
        saveButton.setEnabled(false);

        JButton pauseButton = createPauseResumeButton(saveButton);

        saveButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                saveButton.setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0137.png"));;
            }
            public void mouseExited(MouseEvent e) {
                saveButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        Font customFont;

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/ThaleahFat.ttf")) .deriveFont(20f);
        } catch (FontFormatException | IOException e1) {
            customFont = new Font("Arial", Font.BOLD, 16);
            e1.printStackTrace();
        }
        saveButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            while (true) {
                String saveName = JOptionPane.showInputDialog(
                        this,
                        "Enter a name for your save:",
                        "Save Game",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (saveName == null) { // cancel
                    break;
                }
                saveName = saveName.trim();

                if (saveName.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Save name cannot be empty.",
                            "Save Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!saveName.matches("[a-zA-Z0-9_\\-]+")) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Save name can only contain letters, numbers, underscores, and hyphens.",
                            "Invalid Save Name",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (saveName.length() < 3) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Save name must be at least 3 characters long.",
                            "Invalid Save Name",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    GameState gameState = hallController.createGameState(timerController.getHallTimes(), timeRemaining);
                    SaveLoadUtil.saveGame(gameState, saveName);
                    JOptionPane.showMessageDialog(
                            this,
                            "Game saved successfully as: " + saveName,
                            "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
                }
            }
        });
        UIManager.put("OptionPane.background", new Color(66, 40, 53,255));
        UIManager.put("Panel.background", new Color(66, 40, 53,255));
        UIManager.put("OptionPane.messageForeground", Color.WHITE); //
        UIManager.put("Button.background", new Color(139, 69, 19));
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.font", customFont);
        UIManager.put("OptionPane.messageFont", customFont);



        pauseButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                pauseButton.setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0137.png"));;
            }
            public void mouseExited(MouseEvent e) {
                pauseButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    
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
            TimerController.getInstance().reset();
            returnToGameOverScreen.execute();
        });

        buttonPanel.add(pauseButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(saveButton);

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

        JLayeredPane inventoryLayeredPane = new JLayeredPane();
        inventoryLayeredPane.setPreferredSize(new Dimension(250, 150)); // Adjust size to match the inventory design
        inventoryLayeredPane.setLayout(null); // Use null layout for absolute positioning

        // Inventory chest icon
        JLabel chestIcon = new JLabel(new ImageIcon("src/resources/images/Inventory2x_3.png"));
        chestIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        inventoryLayeredPane.add(chestIcon, Integer.valueOf(0));

        // Add enchantment slots on top of the chest icon
        int slotSize = 30;
        int startX = 33; // Adjust to align with the inventory background
        int startY = 89; // Adjust to align with the inventory background
        int gap = 2;

        for (int i = 0; i < 6; i++) {
            enchantmentLabels[i] = new JLabel();
            enchantmentLabels[i].setBounds(startX + (i % 3) * (slotSize + gap), startY + (i / 3) * (slotSize + gap), slotSize, slotSize);
            //enchantmentLabels[i].setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1)); // Border for empty slots
            chestIcon.add(enchantmentLabels[i], Integer.valueOf(1)); // Add to the top layer (foreground)
        }
        updateInventory();

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
        sidePanel.add(inventoryLayeredPane);
        inventoryLayeredPane.setBounds(50, 300, 250, 150);
    }

    private JButton createPauseResumeButton(JButton saveButton) {
        JButton button = new JButton(new ImageIcon("src/resources/images/pause_button.png"));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        button.addActionListener(e -> togglePauseResume(button, saveButton));
        return button;
    }

    private void togglePauseResume(JButton button, JButton saveButton) {
        if (isPaused) {
            resumeGame(button, saveButton);
        } else {
            pauseGame(button, saveButton);
        }
    }

    private void pauseGame(JButton pauseButton, JButton saveButton) {
        SoundPlayerUtil.playClickSound();
        isPaused = true;
        pauseButton.setIcon(new ImageIcon("src/resources/images/play_button.png"));
        timerController.pauseTimers();
        saveButton.setEnabled(true);
        System.out.println("Game paused.");
    }

    private void resumeGame(JButton pauseButton, JButton saveButton) {
        SoundPlayerUtil.playClickSound();
        isPaused = false;
        pauseButton.setIcon(new ImageIcon("src/resources/images/pause_button.png"));
        timerController.resumeTimers();
        gamePanel.requestFocusInWindow();
        saveButton.setEnabled(false);
        System.out.println("Game resumed.");
    }

    private JButton createButton(String imagePath) {
        Icon resizedIcon = resizeIcon(new ImageIcon(imagePath), 32, 32);
        JButton button = new JButton(resizedIcon);
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

    private void updateInventory() {
        for (JLabel enchantmentLabel : enchantmentLabels) {
            enchantmentLabel.setIcon(null);
        }

        List<Collectible> inventoryItems = hero.getInventory().getAllItems(); // Get all items

        // Loop through inventory and update labels
        for (int i = 0; i < inventoryItems.size() && i < enchantmentLabels.length; i++) {
            Collectible item = inventoryItems.get(i);
            String type = item.getType();
            String imagePath = switch (type) {
                case "Cloak of Protection" -> "src/resources/images/cloak30x30.png";
                case "Luring Gem" -> "src/resources/images/lure30x30.png";
                case "Reveal" -> "src/resources/images/reveal30x30.png";
                default -> null;
            };

            if (imagePath != null) {
                enchantmentLabels[i].setIcon(new ImageIcon(imagePath));
            }
        }
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
                //() -> spawnController.spawnMonster(hallController.getCurrentHall())
                () -> spawnMonster(),
//            () -> teleportRune(),
                () -> handleWizardTeleportAction(),
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
        isLoaded = false;
        hallController.goNextHall();
        timerController.cleanup();
        timerController.resetGameTime();
        initializeHeroPosition();
        initializeTimers();
        initializeRunePosition();
        updateInventory();
        updateHearts();
        timeRemaining = hallController.getCurrentHall().getInitialTime();

        monsters = new ArrayList<>();
        random = new Random();
        enchantments = new ArrayList<>();
        rune.unCollect(hero);
        loadHall();
    }
    
    private void initializeRunePosition() {
        if(!isLoaded){
            hallController.updateRune();
        }
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

    private void moveMonsters() {
        Direction[] directions = Direction.values();
        for (Monster monster : monsters) {
            if ((monster instanceof FighterMonster)) { // Sadece fighter monster hareket ediyor
                if (((FighterMonster) monster).isLured()) {
                    int newX = ((FighterMonster) monster).getLureX();
                    int newY = ((FighterMonster) monster).getLureY();
                    hallController.getCurrentHall().removeGridElement(monster.getX(), monster.getY());
                    ((FighterMonster) monster).moveTowardsPoint(newX, newY, hallController.getCurrentHall());
                    hallController.getCurrentHall().addGridElement(monster, monster.getX(), monster.getY());
                }
                else {
                    Direction randomDirection = directions[random.nextInt(directions.length)];
                    int newX = monster.getX() + randomDirection.getDx();
                    int newY = monster.getY() + randomDirection.getDy();

                    // Check boundaries and prevent overlap
                    if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS && !hallController.getCurrentHall().isPositionOccupied(newX, newY)) {
                        hallController.getCurrentHall().removeGridElement(monster.getX(), monster.getY());
                        monster.move(randomDirection);
                        hallController.getCurrentHall().addGridElement(monster, monster.getX(), monster.getY());
                    }
                }
            }
            else if (monster instanceof ArcherMonster) {
                Direction randomDirection = directions[random.nextInt(directions.length)];
                int newX = monster.getX() + randomDirection.getDx();
                int newY = monster.getY() + randomDirection.getDy();
                // ArcherMonster always moves randomly
                if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS && !hallController.getCurrentHall().isPositionOccupied(newX, newY)) {
                    hallController.getCurrentHall().removeGridElement(monster.getX(), monster.getY());
                    monster.move(randomDirection);
                    hallController.getCurrentHall().addGridElement(monster, monster.getX(), monster.getY());
                }
            }
        }
        checkHeroMonsterCollision();
        repaint();
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
            Rune rune = hallController.getCurrentHall().getRune();
            if (rune != null && !hallController.getCurrentHall().getRune().isCollected()) {
                rune.setPosition(randomPosition.x, randomPosition.y); // Update the rune's position
                runePosition.setLocation(randomPosition); // Update runePosition to reflect the new location
                System.out.println("Rune teleported to an object at position: X=" + randomPosition.x + ", Y=" + randomPosition.y);
            }
            repaint(); // Update the game screen
        }
    }

    private void handleWizardTeleportAction() {
        boolean wizardExists = monsters.stream().anyMatch(m -> m instanceof WizardMonster);
        if (wizardExists) {
            // Find the wizard monster
            WizardMonster wizard = (WizardMonster) monsters.stream()
                    .filter(m -> m instanceof WizardMonster)
                    .findFirst()
                    .get();

            // Let the wizard's behavior handle the action
            wizard.setTimeInfo(hallController.getCurrentHall(), timeRemaining);
            wizard.performAction(hero);

            Rune rune = hallController.getCurrentHall().getRune();
            Point position = new Point(rune.getX(), rune.getY());
            runePosition = position;

            monsters = hallController.getCurrentHall().getMonsters();
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
                if (hallController.getCurrentHall().isPositionOccupied(archerX, y)) return true;
            }
        } else if (archerY == heroY) {
            // Check horizontal path
            int startX = Math.min(archerX, heroX);
            int endX = Math.max(archerX, heroX);
            for (int x = startX + 1; x < endX; x++) {
                if (hallController.getCurrentHall().isPositionOccupied(x, archerY)) return true;
            }
        }
        return false;
    }

    public JPanel getGamePanel() {
        return gamePanel;
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
    private void spawnMonster() {
        Monster monster = spawnController.spawnMonster(hallController.getCurrentHall());
        if (monster != null) {
            monsters.add(monster);
            repaint();
        }
    }

    private void removeEnchantment() {
        spawnController.removeEnchantment(hallController.getCurrentHall());
        enchantments.clear(); // to remove from ui
        repaint();
    }

    private void stopGame() {
        timerController.cleanup();
        dispose();
    }

    public class GamePanel extends JPanel implements KeyListener, MouseListener {
        private BufferedImage heroImage;
        private BufferedImage archerImage;
        private BufferedImage fighterImage;
        private BufferedImage wizardImage;
        private BufferedImage backgroundImage;
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
                backgroundImage = ImageIO.read(new File("src/resources/images/background.png"));
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

            if (backgroundImage != null) {
                for (int row = 0; row < GRID_ROWS; row++) {
                    for (int col = 0; col < GRID_COLUMNS; col++) {
                        g.drawImage(
                                backgroundImage,
                                offsetX + col * CELL_SIZE,
                                offsetY + row * CELL_SIZE,
                                CELL_SIZE,
                                CELL_SIZE,
                                this
                        );
                    }
                }
            }

           // drawGrid(g, offsetX, offsetY);
            drawTopAndSideWalls(g, offsetX, offsetY);
            drawBottomWall(g, offsetX, offsetY);
            drawArcherRanges(g, offsetX, offsetY);
            drawHallObjects(g, offsetX, offsetY);
            drawHero(g, offsetX, offsetY);
            drawMonsters(g, offsetX, offsetY);
            //drawRune(g, offsetX, offsetY);
            drawEnchantments(g, offsetX, offsetY);
            drawReveal(g, offsetX, offsetY);
        }

        private void drawReveal(Graphics g, int offsetX, int offsetY) {
            Hall currentHall = hallController.getCurrentHall(); // Access the current hall
            if (currentHall != null) {
                Rune rune = currentHall.getRune(); // Get the Rune from the current hall
                if (rune != null && rune.isHighlighted()) { // Check if the Rune is highlighted
                    int highlightX = rune.getX();
                    int highlightY = rune.getY();
                    System.out.println("Drawing Rune highlight at: (" + highlightX + ", " + highlightY + ")");
                    g.setColor(new Color(0, 255, 0, 128)); // Transparent green
                    for (int dx = -1; dx <= 2; dx++) { // 4x4 area
                        for (int dy = -1; dy <= 2; dy++) {
                            int drawX = highlightX + dx;
                            int drawY = highlightY + dy;

                            if (drawX >= 0 && drawX < GRID_COLUMNS && drawY >= 0 && drawY < GRID_ROWS) {
                                g.fillRect(
                                        offsetX + drawX * CELL_SIZE, // Screen X
                                        offsetY + drawY * CELL_SIZE, // Screen Y
                                        CELL_SIZE,                  // Width
                                        CELL_SIZE                   // Height
                                );
                            }
                        }
                    }
                } else {
                    // System.out.println("No Rune is highlighted in the current hall."); // debug
                }
            } else {
                System.out.println("No current hall available.");
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

        public void showRuneRevealedEffect() {
            Hall currentHall = hallController.getCurrentHall(); // Access the current hall
            if (currentHall != null) {
                Rune rune = currentHall.getRune(); // Get the Rune from the current hall
                if (rune != null && !hallController.getCurrentHall().getRune().isCollected()) { // second statement rune is NOT collected
                    rune.setHighlighted(true); // Set the rune as highlighted
                    System.out.println("Rune is now highlighted at: (" + rune.getX() + ", " + rune.getY() + ")");

                    // Schedule to remove the highlight after the duration
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000); // Highlight duration: 5 seconds
                            rune.setHighlighted(false); // Remove the highlight
                            System.out.println("Rune highlight expired.");
                            repaint(); // Redraw the panel to reflect the change
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();

                    repaint(); // Trigger an immediate redraw
                } else {
                    System.out.println("No Rune found in the current hall.");
                }
            } else {
                System.out.println("No current hall available for Rune highlight.");
            }
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
        private void drawBottomWall(Graphics g, int offsetX, int offsetY) {
            int bottomWallY = offsetY + GRID_ROWS * CELL_SIZE; // Alt duvarın Y koordinatı
            int bottomWallWidth = GRID_COLUMNS * CELL_SIZE + 35; // Alt duvarın genişliği
            int bottomWallHeight = CELL_SIZE + 20;
            String bottomWallImagePath;
            // Hall tipine göre uygun görseli seç
            switch (hallController.getCurrentHall().getHallType()) {
                case EARTH:
                    bottomWallImagePath = "src/resources/images/bottomearth.png";
                    break;
                case WATER:
                    bottomWallImagePath = "src/resources/images/bottomwater.png";
                    break;
                case FIRE:
                    bottomWallImagePath = "src/resources/images/bottomfire.png";
                    break;
                case AIR:
                    bottomWallImagePath = "src/resources/images/bottomair.png";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid hall type.");
            }
            try {
                BufferedImage bottomWallImage = ImageIO.read(new File(bottomWallImagePath));
                g.drawImage(bottomWallImage, offsetX-18, bottomWallY, bottomWallWidth, bottomWallHeight, null);
            } catch (IOException e) {
                System.err.println("Failed to load bottom wall image: " + e.getMessage());
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
                        if (monsterY - i >= 0 && !hallController.getCurrentHall().isPositionOccupied(monsterX, monsterY - i)) {
                            g.fillRect(offsetX + monsterX * CELL_SIZE, offsetY + (monsterY - i) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        // Down
                        if (monsterY + i < GRID_ROWS && !hallController.getCurrentHall().isPositionOccupied(monsterX, monsterY + i)) {
                            g.fillRect(offsetX + monsterX * CELL_SIZE, offsetY + (monsterY + i) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        // Left
                        if (monsterX - i >= 0 && !hallController.getCurrentHall().isPositionOccupied(monsterX - i, monsterY)) {
                            g.fillRect(offsetX + (monsterX - i) * CELL_SIZE, offsetY + monsterY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        // Right
                        if (monsterX + i < GRID_COLUMNS && !hallController.getCurrentHall().isPositionOccupied(monsterX + i, monsterY)) {
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

            if (isPaused) {
                System.out.println("Game is paused. Clicks are disabled.");
                return;
            }

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
            if (clickedObject.getX() == runePosition.x && clickedObject.getY() == runePosition.y && rune.isAvailable()) {

                SoundPlayerUtil.playOpenDoorSound();
                rune.collect(hero);

            } else {
                SoundPlayerUtil.playErrorSound();
            }
        }

        private void handleEnchantmentClick(Enchantment clickedEnchantment) {
            String enc = clickedEnchantment.getType();
            if (Objects.equals(enc, "Extra Time")) {
                activeExtraTime();
                System.out.println("5 seconds added!");
                collectEnchantment(clickedEnchantment);
            }
            else if (Objects.equals(enc, "Extra Life")) {
                activateExtraLife();
                updateHearts();
                collectEnchantment(clickedEnchantment);
            }
            else {
                if(hero.getInventory().hasEmptySpace()){
                    hero.getInventory().addItem(clickedEnchantment);
                    System.out.println("Added enchantment " + clickedEnchantment.getType() + " to inventory.");
                    collectEnchantment(clickedEnchantment);
                    updateInventory();
                }else{
                    System.out.println("Inventory is full! Cannot pick up " + clickedEnchantment.getType());
                    SoundPlayerUtil.playErrorSound();
                }
            }
        }

        private void collectEnchantment(Enchantment enchantment) {
            enchantments.remove(enchantment);
            hallController.getCurrentHall().removeGridElement(enchantment.getX(), enchantment.getY());
            enchantment.disappear();
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
            System.out.println("R key pressed. Checking for Reveal...");
            if (hero.getInventory().hasItem("Reveal")) {
                hero.getInventory().useItem("Reveal"); // Use the enchantment
                showRuneRevealedEffect(); // Trigger the highlight effect
            } else {
                System.out.println("No Reveal enchantment found in inventory.");
            }
        }
        
        private void activateCloakOfProtection() {
            hero.setIsVisible(false);
            hero.setIsCloaked(true);
            System.out.println("Hero is now invisible to archers for 20 seconds.");
        
            // Schedule the visibility to reset after 20 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(20000); // 20 seconds in milliseconds
                    hero.setIsVisible(true);
                    hero.setIsCloaked(false);
                    updateHeroImage();
                    System.out.println("Hero is now visible to archers again.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        private void activateLuringGem(String direction) {
            int x = 0;
            int y = 0;
            boolean isValid = false;
            if (direction == "up") {
                x = hero.getX();
                y = 0;
                while (!isValid) {
                    if (hallController.getCurrentHall().isValidPosition(x,y)) {
                        for (Monster monster : monsters) {
                            if (monster instanceof FighterMonster) {
                                ((FighterMonster) monster).setLuringGemLocation(x, y);
                            }
                        }
                        isValid = true;
                    }
                    else {
                        y++;
                    }
                }
            }
            else if (direction == "down") {
                x = hero.getX();
                y = 11;
                while (!isValid) {
                    if (hallController.getCurrentHall().isValidPosition(x,y)) {
                        for (Monster monster : monsters) {
                            if (monster instanceof FighterMonster) {
                                ((FighterMonster) monster).setLuringGemLocation(x, y);
                            }
                        }
                        isValid = true;
                    }
                    else {
                        y--;
                    }
                }
            }
            else if (direction == "left") {
                x = 0;
                y = hero.getY();
                while (!isValid) {
                    if (hallController.getCurrentHall().isValidPosition(x,y)) {
                        for (Monster monster : monsters) {
                            if (monster instanceof FighterMonster) {
                                ((FighterMonster) monster).setLuringGemLocation(x, y);
                            }
                        }
                        isValid = true;
                    }
                    else {
                        x++;
                    }
                }
            }
            else if (direction == "right") {
                x = 15;
                y = hero.getY();
                while (!isValid) {
                    if (hallController.getCurrentHall().isValidPosition(x,y)) {
                        for (Monster monster : monsters) {
                            if (monster instanceof FighterMonster) {
                                ((FighterMonster) monster).setLuringGemLocation(x, y);
                            }
                        }
                        isValid = true;
                    }
                    else {
                        x--;
                    }
                }
            }
            GridElement lure = new Lure(x, y) {
                @Override
                public int getX() {
                    return 0;
                }
                @Override
                public int getY() {
                    return 0;
                }
                @Override
                public void setPosition(int x, int y) {
                }
            };
            hallController.getCurrentHall().addGridElement(lure, x, y);
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
                        hero.setDirection("LEFT");
                    break;
                case KeyEvent.VK_RIGHT:
                        direction = Direction.RIGHT;
                        hero.setDirection("RIGHT");
                    break;
                case KeyEvent.VK_R: // Reveal
                    System.out.println("R key pressed. Checking for Reveal...");
                    if (hero.getInventory().hasItem("Reveal")) {
//                        System.out.println("Reveal found. Using enchantment...");
//                        hero.getInventory().useItem("Reveal");
                        activateReveal();
                        updateInventory();
                    } else {
                        System.out.println("No Reveal enchantment found in inventory.");
                        SoundPlayerUtil.playErrorSound();
                    }
                    break;
                case KeyEvent.VK_B: // Luring Gem
                    System.out.println("B key pressed. Checking for Luring Gem...");
                    if (hero.getInventory().hasItem("Luring Gem")) {
                        System.out.println("Luring Gem found. Using enchantment...");
                        hero.getInventory().useItem("Luring Gem");
                        hero.setThrowing(true);
                        updateInventory();
                    }
                    else {
                        System.out.println("No Luring Gem enchantment found in inventory.");
                        SoundPlayerUtil.playErrorSound();
                    }
                    break;
                case KeyEvent.VK_P: // Cloak of Protection
                    System.out.println("P key pressed. Checking for Cloak of Protection...");
                    if (hero.getInventory().hasItem("Cloak of Protection") && hero.isVisible()) {
                        SoundPlayerUtil.playClothSound();
                        System.out.println("Using Cloak of Protection...");
                        hero.getInventory().useItem("Cloak of Protection");
                        activateCloakOfProtection();
                        gamePanel.showHeroCloakedEffect();
                        updateInventory();
                    } else if (!hero.getInventory().hasItem("Cloak of Protection")) {
                        System.out.println("No Cloak of Protection found in inventory.");
                        SoundPlayerUtil.playErrorSound();
                    } else {
                        System.out.println("Cloak of Protection is already active");
                        SoundPlayerUtil.playErrorSound();
                    }
                    break;
                // Luring gem throw using WASD keys
                case KeyEvent.VK_W:
                    if (hero.isThrowing()) {
                        SoundPlayerUtil.playThrowSound();
                        activateLuringGem("up");
                        hero.setThrowing(false);
                    }
                    break;
                case KeyEvent.VK_A:
                    if (hero.isThrowing()) {
                        SoundPlayerUtil.playThrowSound();
                        activateLuringGem("left");
                        hero.setThrowing(false);
                    }
                    break;
                case KeyEvent.VK_S:
                    if (hero.isThrowing()) {
                        SoundPlayerUtil.playThrowSound();
                        activateLuringGem("down");
                        hero.setThrowing(false);
                    }
                    break;
                case KeyEvent.VK_D:
                    if (hero.isThrowing()) {
                        SoundPlayerUtil.playThrowSound();
                        activateLuringGem("right");
                        hero.setThrowing(false);
                    }
                    break;
            }

            if (direction != null) {
                int newX = hero.getX() + direction.getDx();
                int newY = hero.getY() + direction.getDy();

                if (hallController.getCurrentHall().getHallType() == Hall.HallType.EARTH &&
                        rune != null && rune.isCollected() &&  direction == Direction.DOWN && ((hero.getX() == 3 && hero.getY() == 11) || (hero.getX() == 4 && hero.getY() == 11))) {
                    if(hallController.canGoNextHall()){
                        goNextHall();
                    }
                    repaint();
                    return;
                }
                if (hallController.getCurrentHall().getHallType() == Hall.HallType.WATER &&
                        rune != null && rune.isCollected() &&  direction == Direction.DOWN && ((hero.getX() == 8 && hero.getY() == 11) || (hero.getX() == 9 && hero.getY() == 11))) {
                    if(hallController.canGoNextHall()){
                        goNextHall();
                    }
                    repaint();
                    return;
                }
                if (hallController.getCurrentHall().getHallType() == Hall.HallType.FIRE &&
                        rune != null && rune.isCollected() &&  direction == Direction.DOWN && ((hero.getX() == 9 && hero.getY() == 11) || (hero.getX() == 10 && hero.getY() == 11))) {
                    if(hallController.canGoNextHall()){
                        goNextHall();
                    }
                    repaint();
                    return;
                }
                if (hallController.getCurrentHall().getHallType() == Hall.HallType.AIR &&
                        rune != null && rune.isCollected() &&  direction == Direction.DOWN && ((hero.getX() == 8 && hero.getY() == 11) || (hero.getX() == 9 && hero.getY() == 11))) {
                    TimerController.getInstance().reset();
                    SoundPlayerUtil.playWinSound();
                    onToWinScreen.execute();
                    return;
                }


                if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS && !hallController.getCurrentHall().isPositionOccupied(newX, newY)) {
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
