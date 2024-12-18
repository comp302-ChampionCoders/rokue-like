package ui.swing;

import javax.swing.*;

import domain.gameobjects.Hall;

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
    
    private boolean gridVisible = false; 
    //private Hall earthHall = new Hall(9, 11, null);

    private final String[] spriteFiles = {
            "src/resources/rokue-like assets/chest.png",
            "src/resources/rokue-like assets/skull.png",
            "src/resources/rokue-like assets/box.png",
            "src/resources/rokue-like assets/pipe.png",
            "src/resources/rokue-like assets/stair.png",
            "src/resources/rokue-like assets/object.png",
            "src/resources/rokue-like assets/elixir.png"
    };

    private BufferedImage topWallImage; 
    private BufferedImage chestImage; 

    public BuildModeScreen() {
        setTitle("Build Mode Screen");
        setUndecorated(true); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
    
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            System.err.println("Tam ekran modu desteklenmiyor.");
            setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }
        
        setTaskbarIcon();
        loadImages();
        initializeScreen();
    }

    // Set application icon in the window and taskbar
    private void setAppIcon() {
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/resources/rokue-like assets/Rokue-like logo 4.png"));
            setIconImage(logoImage);
        } catch (IOException e) {
            System.err.println("Failed to load the logo image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTaskbarIcon() {
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/resources/rokue-like assets/Rokue-like logo 4.png"));
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
            chestImage = ImageIO.read(new File("src/resources/rokue-like assets/Buildmodechest.png"));
            topWallImage = ImageIO.read(new File("src/resources/rokue-like assets/topwall.png"));
        } catch (IOException e) {
            System.err.println("Failed to load chest image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel background; // Background panel for the entire screen

    private void initializeScreen() {
        background = new JPanel(); 
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setLayout(null);
        background.setBackground(new Color(62, 41, 52)); // Set the background color
        add(background);

        // Hide grid when clicking the background
        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gridVisible = false; 
                background.repaint(); 
            }
        });

        addHallLabels(background); // Add hall labels
        addGrid(GRID_START_X, GRID_START_Y, background); // Top-left grid
        addGrid(GRID_START_X + 403, GRID_START_Y, background); // Top-right grid
        addGrid(GRID_START_X, GRID_START_Y + 403, background); // Bottom-left grid
        addGrid(GRID_START_X + 403, GRID_START_Y + 403, background); // Bottom-right grid
        addObjectSection(background); // Add object section
        addTopWallAndSideWalls(background); // Add top walls
    }

    
    private void addTopWallAndSideWalls(JPanel parent) {
        int wallOffset = 8;
        int topWallWidth = GRID_COLUMNS * GRID_CELL_SIZE; // Topwall genişliği
        int sideWallWidth = wallOffset; // Sidewall width (8 piksel)
        int sideWallHeight = GRID_ROWS * GRID_CELL_SIZE + 60;  // Sidewall height (grid boyunca uzanacak)
    
        try {
            BufferedImage sideWallImage = ImageIO.read(new File("src/resources/rokue-like assets/sidewall.png"));
            BufferedImage topWallImage = ImageIO.read(new File("src/resources/rokue-like assets/topwall.png"));
    
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

    

    // Draw the grid lines dynamically
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
        objectSection.setLayout(new GridLayout(0, 1, 0, 5)); 
    
        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        objectSection.add(spacerPanel);
    
        for (String spriteFile : spriteFiles) {
            try {
                BufferedImage sprite = ImageIO.read(new File(spriteFile));
                Image resizedImage = sprite.getScaledInstance(GRID_CELL_SIZE, GRID_CELL_SIZE, Image.SCALE_SMOOTH);
                Image resizedImageChest = sprite.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
    
                JLabel itemLabel = new JLabel(new ImageIcon(resizedImageChest));
                itemLabel.setHorizontalAlignment(SwingConstants.CENTER);
                makeDraggableCopyOnPress(itemLabel, resizedImage);
                objectSection.add(itemLabel);
    
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
        // Define positions for the labels (adjusted downwards by +40 pixels)
        int[][] labelPositions = {
                {GRID_START_X + 20, GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE + 40},        // Hall Of Water
                {GRID_START_X + 403 + 20, GRID_START_Y + GRID_ROWS * GRID_CELL_SIZE + 40},  // Hall Of Earth
                {GRID_START_X + 20, GRID_START_Y + 403 + GRID_ROWS * GRID_CELL_SIZE + 40},  // Hall Of Fire
                {GRID_START_X + 403 + 20, GRID_START_Y + 403 + GRID_ROWS * GRID_CELL_SIZE + 40} // Hall Of Air
        };
    
        String[] labelFiles = {
                "src/resources/rokue-like assets/hallofwater.png",
                "src/resources/rokue-like assets/hallofearth.png",
                "src/resources/rokue-like assets/halloffire.png",
                "src/resources/rokue-like assets/hallofair.png"
        };
    
        int labelWidth = 120; // Smaller width for the labels
        int labelHeight = 30; // Smaller height for the labels
    
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

    // Add drag-and-drop functionality for items
    private void makeDraggableCopyOnPress(JLabel staticLabel, Image image) {
        staticLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gridVisible = true;
                background.repaint();
                JLabel copyLabel = new JLabel(new ImageIcon(image));

                Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                Point frameLocation = background.getLocationOnScreen();

                int adjustedX = mouseLocation.x - frameLocation.x - GRID_CELL_SIZE / 2;
                int adjustedY = mouseLocation.y - frameLocation.y - GRID_CELL_SIZE / 2;

                copyLabel.setBounds(adjustedX, adjustedY, GRID_CELL_SIZE, GRID_CELL_SIZE);
                background.add(copyLabel);
                background.setComponentZOrder(copyLabel, 0);
                makeDraggableAndSnap(copyLabel);
                background.repaint();
            }
        });
    }

    // Add snap-to-grid behavior
    private void makeDraggableAndSnap(JLabel label) {
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
                } else {
                    // move back if not in grid
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
    
    // check grid boundaries
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BuildModeScreen buildModeScreen = new BuildModeScreen();
            buildModeScreen.setVisible(true);
        });
    }
}