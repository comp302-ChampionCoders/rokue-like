package ui.swing;

import javax.swing.*;

import controller.ModeController;
import controller.ScreenTransition;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JFrame {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private JPanel mainPanel;
    private BufferedImage backgroundImage;
    private BufferedImage logoImage;
    private final ScreenTransition onStartBuildMode;

    public MainMenu(ScreenTransition onStartBuildMode) {
        this.onStartBuildMode = onStartBuildMode;
        initializeFrame();
        loadImages();
        createMainPanel();
        addButtons();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Rokue-Like");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set window icon
        try {
            setIconImage(ImageIO.read(new File("src/resources/images/Rokue-likelogo4.png")));
        } catch (IOException e) {
            System.err.println("Failed to load logo: " + e.getMessage());
        }
    }

    private void loadImages() {
        try {
            // backgroundImage = ImageIO.read(new File("src/resources/images/menu-background.png"));
            logoImage = ImageIO.read(new File("src/resources/images/Rokue-likelogo4.png"));
        } catch (IOException e) {
            System.err.println("Failed to load images: " + e.getMessage());
        }
    }

    private void createMainPanel() {
        mainPanel = new JPanel(null) { // Using null layout for absolute positioning
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(66, 40, 53,255)); // Same color with the buildmode
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                // Draw logo
                if (logoImage != null) {
                    int logoWidth = 200;
                    int logoHeight = 150;
                    g.drawImage(logoImage, 
                              (getWidth() - logoWidth) / 2,
                              100,
                              logoWidth, logoHeight, this);
                }
                // Add group name title
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.setColor(Color.WHITE);
                String groupName = "ChampionCoders";
                FontMetrics fm = g.getFontMetrics();
                int titleWidth = fm.stringWidth(groupName);
                g.drawString(groupName, (getWidth() - titleWidth) / 2, 60); 
            }
        };
        mainPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        add(mainPanel);
    }

    private void addButtons() {
        // Button panel for centered vertical alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(SCREEN_WIDTH/4, SCREEN_HEIGHT/2, SCREEN_WIDTH/2, SCREEN_HEIGHT/3);

        JButton newGameButton = createStyledButton("Start New Game");
        JButton helpButton = createStyledButton("Help");
        JButton exitButton = createStyledButton("Exit");

        // Add action listeners
        newGameButton.addActionListener(e -> onStartBuildMode.execute());
        helpButton.addActionListener(e -> showHelpScreen());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to panel with spacing
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(helpButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createVerticalGlue());

        mainPanel.add(buttonPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(160, 82, 45));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(139, 69, 19));
            }
        });
        
        return button;
    }

    /*private void startNewGame() {
        // Hide main menu
        setVisible(false);
        
        // Initialize game state and start build mode
        SwingUtilities.invokeLater(() -> {
            try {
                BuildModeScreen buildMode = new BuildModeScreen();
                buildMode.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to start new game: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                setVisible(true); // Show main menu again if failed
            }
        });
    }*/

    private void showHelpScreen() {
        JDialog helpDialog = new JDialog(this, "How to Play", true);
        helpDialog.setSize(600, 400);
        helpDialog.setLocationRelativeTo(this);

        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setMargin(new Insets(10, 10, 10, 10));
        helpText.setFont(new Font("Arial", Font.PLAIN, 14));
        
        helpText.setText(
            "Rokue-Like Game Instructions:\n\n" +
            "1. Build Mode:\n" +
            "   - Place objects in each hall (minimum requirements apply)\n" +
            "   - Earth Hall: 6 objects\n" +
            "   - Air Hall: 9 objects\n" +
            "   - Water Hall: 13 objects\n" +
            "   - Fire Hall: 17 objects\n\n" +
            "2. Game Controls:\n" +
            "   - Arrow keys to move the hero\n" +
            "   - Click objects to search for runes\n" +
            "   - Click pause button to halt the game\n\n" +
            "3. Enchantments:\n" +
            "   - Collect enchantments for special powers\n" +
            "   - Use 'R' for Reveal enchantment\n" +
            "   - Use 'P' for Cloak of Protection\n" +
            "   - Use 'B' + direction for Luring Gem\n\n" +
            "4. Objective:\n" +
            "   - Find the rune in each hall\n" +
            "   - Avoid or outsmart monsters\n" +
            "   - Complete all halls to win!"
        );

        JScrollPane scrollPane = new JScrollPane(helpText);
        helpDialog.add(scrollPane);
        
        helpDialog.setVisible(true);
    }
}