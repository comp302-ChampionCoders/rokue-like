package ui.swing;

import javax.swing.*;

import controller.ScreenTransition;
import ui.utils.CursorUtils;
import ui.utils.SoundPlayerUtil;
import ui.utils.TaskBarIconUtil;

import java.awt.*;
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
    private final ScreenTransition onLoadGame;
    private Font customFont;

    public MainMenu(ScreenTransition onStartBuildMode, ScreenTransition onLoadGame) {
        this.onStartBuildMode = onStartBuildMode;
        this.onLoadGame = onLoadGame;
        initializeFrame();

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            configureForWindows();
        } else if (osName.contains("mac")) {
            configureForMacOS();
        } else {
            configureForOther();
        }

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
        setCursor(CursorUtils.createCustomCursor("src/resources/images/pointer_scifi_a.png"));
    }

    private void configureForMacOS(){
        TaskBarIconUtil.setMacTaskbarIcon();
    }

    public void configureForWindows(){
        TaskBarIconUtil.setWindowsTaskbarIcon(this);
    }

    public void configureForOther(){
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

        JButton newGameButton = createStyledButton("START NEW GAME");
        JButton loadScreenButton = createStyledButton("LOAD GAME");
        JButton helpButton = createStyledButton("HELP");
        JButton exitButton = createStyledButton("EXIT");

        // Add action listeners
        newGameButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            onStartBuildMode.execute();
        });

        loadScreenButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            onLoadGame.execute();
        });

        helpButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            showHelpScreen();
        }
        );
        exitButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            System.exit(0);
        });

        // Add buttons to panel with spacing
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(loadScreenButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(helpButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createVerticalGlue());

        mainPanel.add(buttonPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(250, 60));

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/FantasyRPGtext.ttf")) .deriveFont(24f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 16);
        }

        button.setFont(customFont);
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

    private void showHelpScreen() {
        JDialog helpDialog = new JDialog(this, "How to Play", true);
        helpDialog.setSize(600, 400);
        helpDialog.setLocationRelativeTo(this);
      

        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setBackground(new Color(66, 40, 53,255));
        helpText.setMargin(new Insets(10, 10, 10, 10));
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/FantasyRPGtext.ttf")) .deriveFont(20f);
            helpText.setFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            helpText.setFont(new Font("Arial", Font.PLAIN, 20));
        }
        helpText.setForeground(Color.WHITE);
        
        helpText.setText(
            "ROKUE-LIKE GAME INSTRUCTIONS:\n\n" +
            "1. BUILD MODE:\n" +
            "   - PLACE OBJECTS IN EACH HALL (MINIMUM REQUIREMENTS APPLY)\n" +
            "   - EARTH HALL: 6 OBJECTS\n" +
            "   - AIR HALL: 9 OBJECTS\n" +
            "   - WATER HALL: 13 OBJECTS\n" +
            "   - FIRE HALL: 17 OBJECTS\n" +
            "   - RANDOM!: YOU DO NOT HAVE TO BUILD EVERYTHING\n\n" +
            "2. GAME CONTROLS:\n" +
            "   - ARROW KEYS TO MOVE THE HERO\n" +
            "   - CLICK OBJECTS TO SEARCH FOR RUNES\n\n" +
            "3. ENCHANTMENTS:\n" +
            "   - COLLECT ENCHANTMENTS FOR SPECIAL POWERS\n" +
            "   - USE 'R' FOR REVEAL ENCHANTMENT TO REVEAL A 4X4 AREA WHICH THE RUNE IS LOCATED WITHIN\n" +
            "   - USE 'P' FOR CLOAK OF PROTECTION TO AVOID ARCHER MONSTERS\n" +
            "   - USE 'B' + DIRECTION FOR LURING GEM TO DISTRACT FIGHTER MONSTERS\n\n" +
            "4. OBJECTIVE:\n" +
            "   - FIND THE RUNE IN EACH HALL\n" +
            "   - AVOID OR OUTSMART MONSTERS\n" +
            "   - COMPLETE ALL HALLS TO WIN!\n\n" +
            "5. SAVE AND LOAD GAME:\n" +
            "   - IN PLAY MODE, CLICK ON SAVE BUTTON AFTER PAUSING THE GAME\n" +
            "   - CLICK ON LOAD GAME AT MAIN MENU AND SELECT THE SAVED GAME YOU WOULD LIKE TO LOAD"

            
        );

        JScrollPane scrollPane = new JScrollPane(helpText);
        helpDialog.add(scrollPane);
        
        helpDialog.setVisible(true);
    }
}