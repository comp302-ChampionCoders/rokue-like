package ui.swing;


import javax.swing.*;

import controller.ModeController;
import controller.ScreenTransition;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hall.HallType;
import ui.utils.CursorUtils;
import ui.utils.SoundPlayerUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class BuildModeScreen extends JFrame {
    private final int GRID_CELL_SIZE = 24; 
    private final int GRID_START_X = 128; 
    private final int GRID_START_Y = 70; 
    private final int GRID_COLUMNS = 16; 
    private final int GRID_ROWS = 12; 
    private final int RIGHT_GRID_START_FROM_X = 432;

    private final int SCREEN_WIDTH = 1200;  
    private final int SCREEN_HEIGHT = 850;  


    private final int OBJECT_SECTION_WIDTH = 150; 
    private final int OBJECT_SECTION_HEIGHT = SCREEN_HEIGHT - GRID_START_Y - 100;
    private final int OBJECT_SECTION_START_X = SCREEN_WIDTH - OBJECT_SECTION_WIDTH - 50; 
    private final int OBJECT_SECTION_START_Y = GRID_START_Y + 25; 
    
    private JButton exitButton;
    private JButton playButton;
    private final int EXIT_BUTTON_SIZE = 32;

    private boolean gridVisible = false; 

    private Hall waterHall = new Hall(16, 12, null, HallType.WATER);
    private Hall earthHall = new Hall(16, 12, null, HallType.EARTH);
    private Hall fireHall = new Hall(16, 12, null, HallType.FIRE);
    private Hall airHall = new Hall(16, 12, null, HallType.AIR);

    private final String[] spriteFiles = {
            "src/resources/images/chest_50x50.png",
            "src/resources/images/skull_50x50.png",
            "src/resources/images/box_50x50_nbg.png",
            "src/resources/images/pipe_50x50.png",
            "src/resources/images/stair_50x50.png",
            "src/resources/images/object_50x50.png",
            "src/resources/images/elixir_50x50.png"
    };

    private BufferedImage topWallImage; 
    private BufferedImage chestImage; 
    private final ScreenTransition onExit;
    private final ScreenTransition onSwitchToPlayMode;
    private ArrayList<Hall> allHalls = new ArrayList<>();

    
    public BuildModeScreen(ScreenTransition onExit, ScreenTransition onSwitchToPlayMode) {
        this.onExit = onExit;
        this.onSwitchToPlayMode = onSwitchToPlayMode;
        addHalls();
        setTitle("Build Mode");
        setUndecorated(true); 
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
    
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setTaskbarIcon();
        loadImages();
        initializeScreen();
    }

    public ArrayList<Hall> getAllHalls() {
        return allHalls;
    }
    
    private void addHalls(){
        allHalls.add(earthHall);
        allHalls.add(waterHall);
        allHalls.add(fireHall);
        allHalls.add(airHall);
    }

    /*private void setAppIcon() {
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/resources/images/Rokue-likelogo4.png"));
            setIconImage(logoImage);
        } catch (IOException e) {
            System.err.println("Failed to load the logo image: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    private void setTaskbarIcon() {
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/resources/images/Rokue-likelogo4.png"));
            Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(logoImage);
        } catch (UnsupportedOperationException e) {
            System.err.println("The Taskbar feature is not supported on this platform: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to load the taskbar icon image: " + e.getMessage());
        }
    }

    // Load images for the object section and top wall
    private void loadImages() {
        try {
            chestImage = ImageIO.read(new File("src/resources/images/Buildmodechest.png"));
            topWallImage = ImageIO.read(new File("src/resources/images/topwall.png"));
        } catch (IOException e) {
            System.err.println("Failed to load chest image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel background; 

    private void initializeScreen() {
        background = new JPanel(); 
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setLayout(null);
        background.setBackground(new Color(66, 40, 53,255)); 
        add(background);
        addRandomizeButtonWithImage(background);
        setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0168.png"));

        // Add exit button
        try {
            BufferedImage exitImg = ImageIO.read(new File("src/resources/images/exit_button.png"));
            BufferedImage playImg = ImageIO.read(new File("src/resources/images/play_button.png"));


            Image scaledExitImg = exitImg.getScaledInstance(EXIT_BUTTON_SIZE, EXIT_BUTTON_SIZE, Image.SCALE_SMOOTH);
            Image scaledPlayImg = playImg.getScaledInstance(EXIT_BUTTON_SIZE, EXIT_BUTTON_SIZE, Image.SCALE_SMOOTH);

            playButton = new JButton(new ImageIcon(scaledPlayImg));
            exitButton = new JButton(new ImageIcon(scaledExitImg));

            
            int startX = OBJECT_SECTION_START_X + OBJECT_SECTION_WIDTH/2 - (EXIT_BUTTON_SIZE * 3 + 20) / 2;
            int y = OBJECT_SECTION_START_Y - EXIT_BUTTON_SIZE - 10;
            
            playButton.setBounds(startX, y, EXIT_BUTTON_SIZE, EXIT_BUTTON_SIZE);
            exitButton.setBounds(startX + (EXIT_BUTTON_SIZE + 10) * 2, y, EXIT_BUTTON_SIZE, EXIT_BUTTON_SIZE);
            
            exitButton.setBorderPainted(false);
            exitButton.setContentAreaFilled(false);
            exitButton.setFocusPainted(false);
            
            
            playButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    playButton.setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0137.png"));;
                }
                public void mouseExited(MouseEvent e) {
                    playButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } 
            });

            // Add hover effect
            exitButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    exitButton.setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0137.png"));;
                }
                public void mouseExited(MouseEvent e) {
                    exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            
            exitButton.addActionListener(e -> {
                SoundPlayerUtil.playClickSound();
                onExit.execute();
            });
            playButton.addActionListener(e -> {
                SoundPlayerUtil.playClickSound();
                onSwitchToPlayMode.execute();});
            
            background.add(exitButton);
            background.add(playButton);
        } catch (IOException e) {
            System.err.println("Failed to load exit button image: " + e.getMessage());
            e.printStackTrace();
        }

        addHallLabels(background); 
        addGrid(GRID_START_X, GRID_START_Y, background); // Top-left grid
        addGrid(GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y, background); // Top-right grid
        addGrid(GRID_START_X, GRID_START_Y + RIGHT_GRID_START_FROM_X, background); // Bottom-left grid
        addGrid(GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y + RIGHT_GRID_START_FROM_X, background); // Bottom-right grid
        addObjectSection(background); 
        addTopWallAndSideWalls(background); 
        addBottomWalls(background);
    }

    private void addRandomizeButtonWithImage(JPanel parent) {
        try {
            BufferedImage randomizeImg = ImageIO.read(new File("src/resources/images/randomize_button.png"));
            Image scaledRandomizeImg = randomizeImg.getScaledInstance(144, 40, Image.SCALE_DEFAULT);
    
            JButton randomizeButton = new JButton(new ImageIcon(scaledRandomizeImg));
            randomizeButton.setBounds(OBJECT_SECTION_START_X + 3, OBJECT_SECTION_START_Y + 1 + OBJECT_SECTION_HEIGHT + 10, 144, 40);
            randomizeButton.setBorderPainted(false);
            randomizeButton.setContentAreaFilled(false);
            randomizeButton.setFocusPainted(false);

            randomizeButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    randomizeButton.setCursor(CursorUtils.createCustomCursor("src/resources/images/tile_0137.png"));;
                }
                public void mouseExited(MouseEvent e) {
                    randomizeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            randomizeButton.addActionListener(e -> {
                SoundPlayerUtil.playClickSound();
            });

            parent.add(randomizeButton);
        } catch (IOException e) {
            System.err.println("Failed to load randomize button image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

/*private void randomizeObjects() {
    // Example logic: Randomize objects in all halls
    Random random = new Random();
    for (Hall hall : allHalls) {
        hall.getObjects().forEach((position, gameObject) -> {
            int newX = random.nextInt(GRID_COLUMNS);
            int newY = random.nextInt(GRID_ROWS);
            if (hall.isValidPosition(newX, newY)) {
                hall.removeObject(gameObject.getX(), gameObject.getY());
                gameObject.setPosition(newX, newY);
                hall.addObject(gameObject, newX, newY);
            }
        });
    }
    System.out.println("Objects randomized!");
    background.repaint(); // Refresh the screen
}*/


    private void addBottomWalls(JPanel parent) {
        int bottomWallY = GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE; 
        int wallWidth = GRID_COLUMNS * GRID_CELL_SIZE + 17; 
    
        int[][] hallPositions = {
                {GRID_START_X - 8, bottomWallY},                      // Top-left grid
                {GRID_START_X + RIGHT_GRID_START_FROM_X - 8, bottomWallY},                // Top-right grid
                {GRID_START_X - 8, bottomWallY + RIGHT_GRID_START_FROM_X},                // Bottom-left grid
                {GRID_START_X + RIGHT_GRID_START_FROM_X - 8, bottomWallY + RIGHT_GRID_START_FROM_X}           // Bottom-right grid
                
        };

        String[] bottomWallImages = {
            "src/resources/images/bottomwater.png", 
            "src/resources/images/bottomearth.png",  
            "src/resources/images/bottomfire.png",   
            "src/resources/images/bottomair.png"     
    };
    
        try {
            for (int i = 0; i < hallPositions.length; i++) {
                BufferedImage bottomWallImage = ImageIO.read(new File(bottomWallImages[i]));

                JLabel bottomWall = new JLabel(new ImageIcon(
                        bottomWallImage.getScaledInstance(wallWidth, GRID_CELL_SIZE + 17, Image.SCALE_SMOOTH)));
                bottomWall.setBounds(hallPositions[i][0] - 1, hallPositions[i][1], wallWidth, GRID_CELL_SIZE + 16);
                parent.add(bottomWall);
            }
        } catch (IOException e) {
            System.err.println("Failed to load bottom wall images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    
    private void addTopWallAndSideWalls(JPanel parent) {
        int wallOffset = 8;
        int topWallWidth = GRID_COLUMNS * GRID_CELL_SIZE; 
        int sideWallWidth = wallOffset; // Sidewall width (8 piksel)
        int sideWallHeight = GRID_ROWS * GRID_CELL_SIZE + 40;  // Sidewall height (grid boyunca uzanacak)
    
        try {
            BufferedImage sideWallImage = ImageIO.read(new File("src/resources/images/sidewall.png"));
            BufferedImage topWallImage = ImageIO.read(new File("src/resources/images/topwall.png"));
    
            // Top wall positions (tam grid başlangıcından başlıyor)
            int[][] topWallPositions = {
                    {GRID_START_X, GRID_START_Y - GRID_CELL_SIZE},           // Top-left
                    {GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y - GRID_CELL_SIZE},     // Top-right
                    {GRID_START_X, GRID_START_Y + RIGHT_GRID_START_FROM_X - GRID_CELL_SIZE},     // Bottom-left
                    {GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y + RIGHT_GRID_START_FROM_X - GRID_CELL_SIZE} // Bottom-right
            };
    
            // Side wall positions (topwall ile grid'in başlangıcından önce başlıyor)
            int[][] sideWallPositions = {
                    {GRID_START_X - wallOffset, GRID_START_Y - GRID_CELL_SIZE},                       // Sol Top-left
                    {GRID_START_X + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y - GRID_CELL_SIZE},    // Sağ Top-left
                    {GRID_START_X + RIGHT_GRID_START_FROM_X - wallOffset, GRID_START_Y - GRID_CELL_SIZE},                 // Sol Top-right
                    {GRID_START_X + RIGHT_GRID_START_FROM_X + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y - GRID_CELL_SIZE}, // Sağ Top-right
                    {GRID_START_X - wallOffset, GRID_START_Y + RIGHT_GRID_START_FROM_X - GRID_CELL_SIZE},                 // Sol Bottom-left
                    {GRID_START_X + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y + RIGHT_GRID_START_FROM_X - GRID_CELL_SIZE}, // Sağ Bottom-left
                    {GRID_START_X + RIGHT_GRID_START_FROM_X - wallOffset, GRID_START_Y + RIGHT_GRID_START_FROM_X - GRID_CELL_SIZE},           // Sol Bottom-right
                    {GRID_START_X + RIGHT_GRID_START_FROM_X + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y + RIGHT_GRID_START_FROM_X - GRID_CELL_SIZE} // Sağ Bottom-right
            };
    
            // Add top walls
            for (int[] pos : topWallPositions) {
                JLabel topWallLabel = new JLabel(new ImageIcon(
                        topWallImage.getScaledInstance(topWallWidth, GRID_CELL_SIZE, Image.SCALE_SMOOTH)));
                topWallLabel.setBounds(pos[0], pos[1], topWallWidth, GRID_CELL_SIZE);
                parent.add(topWallLabel);
            }
    
            // Add side walls
            for (int[] pos : sideWallPositions) {
                JLabel sideWallLabel = new JLabel(new ImageIcon(
                        sideWallImage.getScaledInstance(sideWallWidth, sideWallHeight, Image.SCALE_SMOOTH)));
                sideWallLabel.setBounds(pos[0], pos[1], sideWallWidth, sideWallHeight);
                parent.add(sideWallLabel);
            }
    
        } catch (IOException e) {
            System.err.println("Failed to load wall images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    

    private void addGrid(int x, int y, JPanel parent) {
        JPanel gridPanel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLUMNS)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gridVisible) { 
                    g.setColor(Color.GRAY); 
                    for (int i = 0; i <= GRID_COLUMNS; i++) {
                        int xPos = i * GRID_CELL_SIZE;
                        g.drawLine(xPos, 0, xPos, GRID_ROWS * GRID_CELL_SIZE); 
                    }
                    for (int j = 0; j <= GRID_ROWS; j++) {
                        int yPos = j * GRID_CELL_SIZE;
                        g.drawLine(0, yPos, GRID_COLUMNS * GRID_CELL_SIZE, yPos);
                    }
                }
            }
        };
        gridPanel.setBounds(x, y, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE);
        gridPanel.setOpaque(false);
        parent.add(gridPanel);
    }

    private boolean copyInProgress = false;

    private void addObjectSection(JPanel parent) {
        JPanel objectSection = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (chestImage != null) {
                    int chestWidth = OBJECT_SECTION_WIDTH;
                    int chestHeight = OBJECT_SECTION_HEIGHT;
    
                    int topHeight = chestImage.getHeight() / 2;
                    int bottomHeight = chestImage.getHeight() / 2;
                    int middleHeight = chestHeight - topHeight - bottomHeight;
    
                    BufferedImage top = chestImage.getSubimage(0, 0, chestImage.getWidth(), topHeight);
                    BufferedImage bottom = chestImage.getSubimage(0, chestImage.getHeight() - bottomHeight, chestImage.getWidth(), bottomHeight);
                    BufferedImage middle = chestImage.getSubimage(0, topHeight, chestImage.getWidth(), chestImage.getHeight() - topHeight - bottomHeight);
    
                    g.drawImage(top, 0, 0, chestWidth, topHeight, null);
                    g.drawImage(middle, 0, topHeight, chestWidth, middleHeight, null);
                    g.drawImage(bottom, 0, topHeight + middleHeight, chestWidth, bottomHeight, null);
                }
            }
        };
    
        objectSection.setBounds(OBJECT_SECTION_START_X, OBJECT_SECTION_START_Y, OBJECT_SECTION_WIDTH, OBJECT_SECTION_HEIGHT);
        objectSection.setLayout(new GridLayout(0, 1, 10, 15));
    
        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        objectSection.add(spacerPanel);

        for (String spriteFile : spriteFiles) {
            try {
                BufferedImage sprite = ImageIO.read(new File(spriteFile));
                Image resizedImage = sprite.getScaledInstance(GRID_CELL_SIZE, GRID_CELL_SIZE, Image.SCALE_SMOOTH);
                Image resizedImageChest = sprite.getScaledInstance(50, 50, Image.SCALE_SMOOTH);

                int gameModeCellSize = 50;

                Image gameModeVersion = sprite.getScaledInstance(gameModeCellSize, gameModeCellSize, Image.SCALE_SMOOTH);
    
                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT + OBJECT_SECTION_WIDTH, 15, 5));
                itemPanel.setOpaque(false);
    
                JLabel itemLabel = new JLabel(new ImageIcon(resizedImageChest));
                JButton addButton = new JButton();
                addButton.setPreferredSize(new Dimension(16, 16)); 
                addButton.setMargin(new Insets(0, 0, 0, 0));
                addButton.setBackground(new Color(200, 200, 200));
                addButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                try {
                    ImageIcon addIcon = new ImageIcon("src/resources/images/blueAdd4.png");
                    Image scaledImage = addIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    addButton.setIcon(new ImageIcon(scaledImage));
                } catch (Exception ex) {
                    System.err.println("Could not load the image: " + ex.getMessage());
                }

                addButton.addActionListener(e -> {
                    SoundPlayerUtil.playAddSound();

                    if (!copyInProgress) {
                        copyInProgress = true;
                        makeDraggableCopyOnPress(addButton,resizedImage,gameModeVersion);
                    }
                });

               
                itemPanel.add(itemLabel);
                itemPanel.add(addButton); 

                objectSection.add(itemPanel);
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        JPanel bottomSpacerPanel = new JPanel();
        bottomSpacerPanel.setOpaque(false);
        objectSection.add(bottomSpacerPanel);

        parent.add(objectSection);
    }
    
    
    private void addHallLabels(JPanel parent) {
        int[][] labelPositions = {
                {GRID_START_X , GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE+40},        // Hall Of Water
                {GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE + 40},  // Hall Of Earth
                {GRID_START_X , GRID_START_Y + RIGHT_GRID_START_FROM_X + GRID_ROWS * GRID_CELL_SIZE + 40},  // Hall Of Fire
                {GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y + RIGHT_GRID_START_FROM_X + GRID_ROWS * GRID_CELL_SIZE+40} // Hall Of Air
        };
    
        String[] labelFiles = {
                "src/resources/images/waterlabel.png",
                "src/resources/images/earthlabel.png",
                "src/resources/images/firelabel.png",
                "src/resources/images/airlabel.png"
        };
    
        int labelWidth = 200; 
        int labelHeight = 50; 
    
        for (int i = 0; i < labelFiles.length; i++) {
            try {
                BufferedImage labelImage = ImageIO.read(new File(labelFiles[i]));
                Image resizedLabel = labelImage.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
    
                JLabel label = new JLabel(new ImageIcon(resizedLabel));
                label.setBounds(labelPositions[i][0], labelPositions[i][1], labelWidth, labelHeight);
                parent.add(label);
            } catch (IOException e) {
                System.err.println("Failed to load hall label image: " + labelFiles[i]);
                e.printStackTrace();
            }
        }
    }
    private String getTargetHall(int x, int y) {
        if (new Rectangle(GRID_START_X, GRID_START_Y, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE).contains(x, y)) {
            return "waterHall" ; 
        } else if (new Rectangle(GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE).contains(x, y)) {
            return "earthHall";
        } else if (new Rectangle(GRID_START_X, GRID_START_Y + RIGHT_GRID_START_FROM_X, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE).contains(x, y)) {
            return "fireHall";
        } else if (new Rectangle(GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y + RIGHT_GRID_START_FROM_X, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE).contains(x, y)) {
            return "airHall";
        }
        return null;
    }
    

    
    private void makeDraggableCopyOnPress(JButton plusButton, Image image, Image gameImage) {

        JLabel copyLabel = new JLabel(new ImageIcon(image));
    
        Point buttonLocation = plusButton.getLocation();
        SwingUtilities.convertPointToScreen(buttonLocation, plusButton.getParent());
    
        Point backgroundLocation = background.getLocationOnScreen();
    
        
        int spawnX = buttonLocation.x - backgroundLocation.x + plusButton.getWidth() + 10; 
        int spawnY = buttonLocation.y - backgroundLocation.y - 4;
        
        copyLabel.setBounds(spawnX, spawnY, GRID_CELL_SIZE, GRID_CELL_SIZE);
        background.add(copyLabel);
        background.setComponentZOrder(copyLabel, 0);
    
        makeDraggableAndSnap(copyLabel, image, gameImage);
        background.repaint();
    }
    
    private void removeAfterDrag(Point position, Point exPosition ,String lastHall, int lastX, int lastY){
        if(isInsideAnyGrid(exPosition)){
            if(lastHall.equals("waterHall")){
                lastX = (exPosition.x - GRID_START_X) / GRID_CELL_SIZE;
                lastY = (exPosition.y - GRID_START_Y) / GRID_CELL_SIZE;
                waterHall.removeObject(lastX, lastY);
            }
            else if(lastHall.equals("earthHall")){
                lastX = (exPosition.x - (GRID_START_X + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;
                lastY = (exPosition.y - GRID_START_Y) / GRID_CELL_SIZE;
                earthHall.removeObject(lastX, lastY);
            }
            else if(lastHall.equals("fireHall")){
                lastX = (exPosition.x - GRID_START_X) / GRID_CELL_SIZE;
                lastY = (exPosition.y - (GRID_START_Y + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;
                fireHall.removeObject(lastX, lastY);
            }
            else if(lastHall.equals("airHall")){
                lastX = (exPosition.x - (GRID_START_X + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;
                lastY = (exPosition.y - (GRID_START_Y + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;
                airHall.removeObject(lastX, lastY);
            }     
        }
    }
    
    

    private void makeDraggableAndSnap(JLabel label, Image image, Image gameImage) {
        final Point[] lastLocation = {label.getLocation()}; // last location of the object
    
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gridVisible = true; 
                background.repaint();
                lastLocation[0] = label.getLocation(); // save last location
            }
    
            

            @Override
            public void mouseReleased(MouseEvent e) {
                Component c = e.getComponent();
                Point position = c.getLocation();
    

                // check
                if (isInsideAnyGrid(position)) {
                    int snappedX = ((position.x - GRID_START_X) / GRID_CELL_SIZE) * GRID_CELL_SIZE + GRID_START_X;
                    int snappedY = ((position.y - GRID_START_Y) / GRID_CELL_SIZE) * GRID_CELL_SIZE + GRID_START_Y;

                    String targetedHall = getTargetHall(snappedX, snappedY);
                    String lastHall = getTargetHall(lastLocation[0].x,lastLocation[0].y);

                    int gridX = -1;
                    int gridY = -1;

                    int lastX = -1;
                    int lastY = -1;
                
                    if(targetedHall.equals("waterHall")){
                        gridX = (position.x - GRID_START_X) / GRID_CELL_SIZE;
                        gridY = (position.y - GRID_START_Y) / GRID_CELL_SIZE;
                        removeAfterDrag(position, lastLocation[0], lastHall, lastX, lastY);

                        GameObject newobjectWater = new GameObject(gridX, gridY,gameImage);
                        
                        if(waterHall.addObject(newobjectWater, gridX, gridY)){
                            c.setLocation(snappedX, snappedY);
                            SoundPlayerUtil.playObjectPlacedSound();
                            copyInProgress = false;
                        }
                        else{
                            c.setLocation(lastLocation[0]);
                            SoundPlayerUtil.playMisplacedSound();
                            waterHall.addObject(newobjectWater, lastX, lastY);
                            
                        }

                        System.out.println("Water Hall");
                        waterHall.displayGrid();
                        System.out.println(waterHall.getObjects());

                    }

                    if(targetedHall.equals("earthHall")){
                        gridX = (snappedX - (GRID_START_X + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;
                        gridY = (snappedY - GRID_START_Y) / GRID_CELL_SIZE;
                        removeAfterDrag(position, lastLocation[0], lastHall, lastX, lastY);

                        GameObject newobjectEarth = new GameObject(gridX, gridY, gameImage);


                        if(earthHall.addObject(newobjectEarth, gridX, gridY)){
                            c.setLocation(snappedX, snappedY);
                            SoundPlayerUtil.playObjectPlacedSound();
                            copyInProgress = false;
                        }
                        else{
                            c.setLocation(lastLocation[0]);
                            SoundPlayerUtil.playMisplacedSound();
                            earthHall.addObject(newobjectEarth, lastX, lastY);
                        }

                        System.out.println("Earth Hall");
                        earthHall.displayGrid();
                        System.out.println(earthHall.getObjects());
                    }

                    if(targetedHall.equals("fireHall")){
                        gridX = (snappedX - GRID_START_X) / GRID_CELL_SIZE;
                        gridY = (snappedY - (GRID_START_Y + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;
                        removeAfterDrag(position, lastLocation[0], lastHall, lastX, lastY);

                        GameObject newobjectFire = new GameObject(gridX, gridY, gameImage);

                        if(fireHall.addObject(newobjectFire, gridX, gridY)){
                            c.setLocation(snappedX, snappedY);
                            SoundPlayerUtil.playObjectPlacedSound();
                            copyInProgress = false;
                        }
                        else{
                            c.setLocation(lastLocation[0]);
                            SoundPlayerUtil.playMisplacedSound();
                            fireHall.addObject(newobjectFire, lastX, lastY);
                        }

                        System.out.println("Fire Hall");
                        fireHall.displayGrid();
                        System.out.println(fireHall.getObjects());
                    }

                    if(targetedHall.equals("airHall")){
                        gridX = (snappedX - (GRID_START_X + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;
                        gridY = (snappedY - (GRID_START_Y + RIGHT_GRID_START_FROM_X)) / GRID_CELL_SIZE;

                        removeAfterDrag(position, lastLocation[0], lastHall, lastX, lastY);
                        GameObject newobject = new GameObject(gridX, gridY, gameImage);

                        if(airHall.addObject(newobject, gridX, gridY)){
                            c.setLocation(snappedX, snappedY);
                            SoundPlayerUtil.playObjectPlacedSound();
                            copyInProgress = false;
                        }
                        else{
                            c.setLocation(lastLocation[0]);
                            SoundPlayerUtil.playMisplacedSound();
                            airHall.addObject(newobject, lastX, lastY);
                        }

                        System.out.println("Air hall");
                        airHall.displayGrid();
                        System.out.println(airHall.getObjects());
                    }
                    
                } 
                else {
                    // move back if not in grid
                    SoundPlayerUtil.playMisplacedSound();
                    c.setLocation(lastLocation[0]);
                    
                }
                gridVisible = false;
                background.repaint();
            }
        });
    
        label.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Component c = e.getComponent();
                c.setLocation(c.getX() + e.getX() - c.getWidth() / 2, c.getY() + e.getY() - c.getHeight() / 2);
            }
        });
    }
    
    private boolean isInsideAnyGrid(Point position) {
        Rectangle[] gridBounds = {
                new Rectangle(GRID_START_X, GRID_START_Y, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE), // Top-left
                new Rectangle(GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE), // Top-right
                new Rectangle(GRID_START_X, GRID_START_Y + RIGHT_GRID_START_FROM_X, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE), // Bottom-left
                new Rectangle(GRID_START_X + RIGHT_GRID_START_FROM_X, GRID_START_Y + RIGHT_GRID_START_FROM_X, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE) // Bottom-right
        };
    
        // check if new position is in the grid
        for (Rectangle grid : gridBounds) {
            if (grid.contains(position)) {
                return true;
            }
        }
        return false;
    }

}
