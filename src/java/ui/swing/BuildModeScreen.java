package ui.swing;

import javax.swing.*;

import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hall.HallType;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class BuildModeScreen extends JFrame {
    private final int GRID_CELL_SIZE = 31; 
    private final int GRID_START_X = 128; 
    private final int GRID_START_Y = 90; 
    private final int GRID_COLUMNS = 11; 
    private final int GRID_ROWS = 9; 

    private final int SCREEN_WIDTH = 1200;  
    private final int SCREEN_HEIGHT = 850;  


    private final int OBJECT_SECTION_WIDTH = 150; 
    private final int OBJECT_SECTION_HEIGHT = SCREEN_HEIGHT - GRID_START_Y - 100;
    private final int OBJECT_SECTION_START_X = SCREEN_WIDTH - OBJECT_SECTION_WIDTH - 100;; 
    private final int OBJECT_SECTION_START_Y = GRID_START_Y + 25; 
    
    private JButton exitButton;
    private final int EXIT_BUTTON_SIZE = 30;

    private boolean gridVisible = false; 

    private Hall waterHall = new Hall(11, 9, null, HallType.WATER);
    private Hall earthHall = new Hall(11, 9, null, HallType.EARTH);
    private Hall fireHall = new Hall(11, 9, null, HallType.FIRE);
    private Hall airHall = new Hall(11, 9, null, HallType.AIR);

    private final String[] spriteFiles = {
            "src/resources/images/chest.png",
            "src/resources/images/skull.png",
            "src/resources/images/box.png",
            "src/resources/images/pipe.png",
            "src/resources/images/stair.png",
            "src/resources/images/object.png",
            "src/resources/images/elixir.png"
    };

    private BufferedImage topWallImage; 
    private BufferedImage chestImage; 

    public BuildModeScreen() {
        setTitle("Build Mode Screen");
        setUndecorated(true); 
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
    
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            System.err.println("Full Screen Not Supported");
            setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }
        
        setTaskbarIcon();
        loadImages();
        initializeScreen();
    }


    private void setAppIcon() {
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/resources/images/Rokue-likelogo4.png"));
            setIconImage(logoImage);
        } catch (IOException e) {
            System.err.println("Failed to load the logo image: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
        background.setBackground(new Color(62, 41, 52)); 
        add(background);

        // Add exit button
        try {
            BufferedImage exitImg = ImageIO.read(new File("src/resources/images/exit_button.png"));
            Image scaledExitImg = exitImg.getScaledInstance(EXIT_BUTTON_SIZE, EXIT_BUTTON_SIZE, Image.SCALE_SMOOTH);
            exitButton = new JButton(new ImageIcon(scaledExitImg));
            exitButton.setBounds(OBJECT_SECTION_START_X + OBJECT_SECTION_WIDTH/2 - EXIT_BUTTON_SIZE/2, 
                            OBJECT_SECTION_START_Y - EXIT_BUTTON_SIZE - 10, 
                            EXIT_BUTTON_SIZE, 
                            EXIT_BUTTON_SIZE);
            
            // Style the button
            exitButton.setBorderPainted(false);
            exitButton.setContentAreaFilled(false);
            exitButton.setFocusPainted(false);
            
            // Add hover effect
            exitButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                public void mouseExited(MouseEvent e) {
                    exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            
            // Add click action
            exitButton.addActionListener(e -> {
                dispose(); // Close the BuildModeScreen
                SwingUtilities.invokeLater(() -> new MainMenu()); // Open MainMenu
            });
            
            background.add(exitButton);
        } catch (IOException e) {
            System.err.println("Failed to load exit button image: " + e.getMessage());
            e.printStackTrace();
        }

        addHallLabels(background); 
        addGrid(GRID_START_X, GRID_START_Y, background); // Top-left grid
        addGrid(GRID_START_X + 403, GRID_START_Y, background); // Top-right grid
        addGrid(GRID_START_X, GRID_START_Y + 403, background); // Bottom-left grid
        addGrid(GRID_START_X + 403, GRID_START_Y + 403, background); // Bottom-right grid
        addObjectSection(background); 
        addTopWallAndSideWalls(background); 
        addBottomWalls(background);
    }

    private void addBottomWalls(JPanel parent) {
        int bottomWallY = GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE; 
        int wallWidth = GRID_COLUMNS * GRID_CELL_SIZE + 16; 
    
        int[][] hallPositions = {
                {GRID_START_X - 8, bottomWallY},                      // Top-left grid
                {GRID_START_X + 403 - 8, bottomWallY},                // Top-right grid
                {GRID_START_X - 8, bottomWallY + 403},                // Bottom-left grid
                {GRID_START_X + 403 - 8, bottomWallY + 403}           // Bottom-right grid
                
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
                        bottomWallImage.getScaledInstance(wallWidth, GRID_CELL_SIZE + 16, Image.SCALE_SMOOTH)));
                bottomWall.setBounds(hallPositions[i][0], hallPositions[i][1], wallWidth, GRID_CELL_SIZE + 16);
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
                    {GRID_START_X + 403, GRID_START_Y - GRID_CELL_SIZE},     // Top-right
                    {GRID_START_X, GRID_START_Y + 403 - GRID_CELL_SIZE},     // Bottom-left
                    {GRID_START_X + 403, GRID_START_Y + 403 - GRID_CELL_SIZE} // Bottom-right
            };
    
            // Side wall positions (topwall ile grid'in başlangıcından önce başlıyor)
            int[][] sideWallPositions = {
                    {GRID_START_X - wallOffset, GRID_START_Y - 31},                       // Sol Top-left
                    {GRID_START_X + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y - 31},    // Sağ Top-left
                    {GRID_START_X + 403 - wallOffset, GRID_START_Y - 31},                 // Sol Top-right
                    {GRID_START_X + 403 + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y - 31}, // Sağ Top-right
                    {GRID_START_X - wallOffset, GRID_START_Y + 403 - 31},                 // Sol Bottom-left
                    {GRID_START_X + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y + 403 - 31}, // Sağ Bottom-left
                    {GRID_START_X + 403 - wallOffset, GRID_START_Y + 403 - 31},           // Sol Bottom-right
                    {GRID_START_X + 403 + GRID_COLUMNS * GRID_CELL_SIZE, GRID_START_Y + 403 - 31} // Sağ Bottom-right
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
                Image resizedImageChest = sprite.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
    
                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT + OBJECT_SECTION_WIDTH, 15, 5));
                itemPanel.setOpaque(false);
    
                JLabel itemLabel = new JLabel(new ImageIcon(resizedImageChest));
                JButton addButton = new JButton("+");
                addButton.setPreferredSize(new Dimension(20, 20)); 
                addButton.setMargin(new Insets(0, 0, 0, 0));
                addButton.setBackground(new Color(200, 200, 200));
                addButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                addButton.addActionListener(e -> {
                    if (!copyInProgress) {
                        copyInProgress = true;
                        makeDraggableCopyOnPress(addButton,resizedImage);
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
        // Define positions for the labels (adjusted downwards by +50 pixels)
        int[][] labelPositions = {
                {GRID_START_X + 20, GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE + 50},        // Hall Of Water
                {GRID_START_X + 403 + 20, GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE + 50},  // Hall Of Earth
                {GRID_START_X + 20, GRID_START_Y + 403 + GRID_ROWS * GRID_CELL_SIZE + 50},  // Hall Of Fire
                {GRID_START_X + 403 + 20, GRID_START_Y + 403 + GRID_ROWS * GRID_CELL_SIZE + 50} // Hall Of Air
        };
    
        String[] labelFiles = {
                "src/resources/images/hallofwater.png",
                "src/resources/images/hallofearth.png",
                "src/resources/images/halloffire.png",
                "src/resources/images/hallofair.png"
        };
    
        int labelWidth = 120; 
        int labelHeight = 30; 
    
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
        } else if (new Rectangle(GRID_START_X + 403, GRID_START_Y, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE).contains(x, y)) {
            return "earthHall";
        } else if (new Rectangle(GRID_START_X, GRID_START_Y + 403, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE).contains(x, y)) {
            return "fireHall";
        } else if (new Rectangle(GRID_START_X + 403, GRID_START_Y + 403, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE).contains(x, y)) {
            return "airHall";
        }
        return null;
    }
    

    
    private void makeDraggableCopyOnPress(JButton plusButton, Image image) {

        JLabel copyLabel = new JLabel(new ImageIcon(image));
    
        Point buttonLocation = plusButton.getLocation();
        SwingUtilities.convertPointToScreen(buttonLocation, plusButton.getParent());
    
        Point backgroundLocation = background.getLocationOnScreen();
    
        
        int spawnX = buttonLocation.x - backgroundLocation.x + plusButton.getWidth() + 5; 
        int spawnY = buttonLocation.y - backgroundLocation.y - 5;
        
        copyLabel.setBounds(spawnX, spawnY, GRID_CELL_SIZE, GRID_CELL_SIZE);
        background.add(copyLabel);
        background.setComponentZOrder(copyLabel, 0);
    
        makeDraggableAndSnap(copyLabel, image);
        background.repaint();
    }
    
    
    

    private void makeDraggableAndSnap(JLabel label, Image image) {
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
                    

                    c.setLocation(snappedX, snappedY);


                    String targetedHall = getTargetHall(snappedX, snappedY);
                    int gridX = -1;
                    int gridY = -1;

                    if(targetedHall.equals("waterHall")){
                        gridX = (position.x - GRID_START_X) / GRID_CELL_SIZE;
                        gridY = (position.y - GRID_START_Y) / GRID_CELL_SIZE;

                        GameObject newobjectWater = new GameObject(gridX, gridY, image);

                        waterHall.addObject(newobjectWater, gridX, gridY);
                        System.out.println("Water Hall");
                        waterHall.displayGrid();
                        System.out.println(waterHall.getObjects());

                    }

                    if(targetedHall.equals("earthHall")){
                        gridX = (snappedX - (GRID_START_X + 403)) / GRID_CELL_SIZE;
                        gridY = (snappedY - GRID_START_Y) / GRID_CELL_SIZE;

                        GameObject newobjectEarth = new GameObject(gridX, gridY, image);

                        
                        earthHall.addObject(newobjectEarth, gridX, gridY);
                        System.out.println("Earth Hall");
                        earthHall.displayGrid();
                        System.out.println(earthHall.getObjects());
                    }

                    if(targetedHall.equals("fireHall")){
                        gridX = (snappedX - GRID_START_X) / GRID_CELL_SIZE;
                        gridY = (snappedY - (GRID_START_Y + 403)) / GRID_CELL_SIZE;

                        GameObject newobjectFire = new GameObject(gridX, gridY, image);

                        fireHall.addObject(newobjectFire, gridX, gridY);

                        System.out.println("Fire Hall");
                        fireHall.displayGrid();
                        System.out.println(fireHall.getObjects());
                    }

                    if(targetedHall.equals("airHall")){
                        gridX = (snappedX - (GRID_START_X + 403)) / GRID_CELL_SIZE;
                        gridY = (snappedY - (GRID_START_Y + 403)) / GRID_CELL_SIZE;

                        GameObject newobject = new GameObject(gridX, gridY, image);

                        airHall.addObject(newobject, gridX, gridY);
                        System.out.println("Air hall");
                        airHall.displayGrid();
                        System.out.println(airHall.getObjects());
                    }
                    
                } 
                else {
                    // move back if not in grid
                    c.setLocation(lastLocation[0]);
                    //background.remove(c);
                    
                }
                copyInProgress = false;
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
                new Rectangle(GRID_START_X + 403, GRID_START_Y, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE), // Top-right
                new Rectangle(GRID_START_X, GRID_START_Y + 403, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE), // Bottom-left
                new Rectangle(GRID_START_X + 403, GRID_START_Y + 403, GRID_COLUMNS * GRID_CELL_SIZE, GRID_ROWS * GRID_CELL_SIZE) // Bottom-right
        };
    
        // check if new position is in the grid
        for (Rectangle grid : gridBounds) {
            if (grid.contains(position)) {
                return true;
            }
        }
        return false;
    }

    private boolean squareOccupied(Point position) {
        Rectangle targetSquare = new Rectangle(
                (position.x - GRID_START_X) / GRID_CELL_SIZE * GRID_CELL_SIZE + GRID_START_X,
                (position.y - GRID_START_Y) / GRID_CELL_SIZE * GRID_CELL_SIZE + GRID_START_Y,
                GRID_CELL_SIZE, GRID_CELL_SIZE
        );
    
        
        for (Component component : background.getComponents()) {
            if (component instanceof JLabel && component.getBounds().intersects(targetSquare)) {
                return true; 
            }
        }
        return false;
    }
    
}