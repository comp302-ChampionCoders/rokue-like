package ui.swing;

import controller.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import utils.SaveLoadUtil;
import ui.utils.SoundPlayerUtil;

public class LoadGameScreen extends JFrame {
    private static final double WIDTH_RATIO = 0.6;
    private static final double HEIGHT_RATIO = 0.6;
    private static final Color BACKGROUND_COLOR = new Color(66, 40, 53);
    private static final Color BUTTON_COLOR = new Color(139, 69, 19);
    private static final Color BUTTON_HOVER_COLOR = new Color(160, 82, 45);
    private Font customFont;
    private TimerController timerController;

    public LoadGameScreen(ScreenTransition onExit, ScreenTransition onLoadGame, HallController hallController) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * WIDTH_RATIO);
        int height = (int) (screenSize.height * HEIGHT_RATIO);

        loadCustomFont();

        setTitle("Load Game");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel saveListPanel = new JPanel();
        saveListPanel.setLayout(new BoxLayout(saveListPanel, BoxLayout.Y_AXIS));
        saveListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        saveListPanel.setBackground(BACKGROUND_COLOR);

        String[] saveFiles = SaveLoadUtil.listSaveFiles();
        if (saveFiles.length == 0) {
            JLabel noSavesLabel = new JLabel("No saved games found.");
            noSavesLabel.setForeground(Color.WHITE);
            noSavesLabel.setFont(customFont.deriveFont(20f));
            noSavesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            saveListPanel.add(noSavesLabel);
        } else {
            for (String save : saveFiles) {
                JButton loadButton = createStyledButton(save.replace(".dat", ""));
                loadButton.addActionListener(e -> {
                    SoundPlayerUtil.playClickSound();
                    var gameState = SaveLoadUtil.loadGame(save);
                    if (gameState != null) {
                        hallController.loadGameState(gameState);
                        GameScreen.isLoaded = true;
                        onLoadGame.execute();
                        dispose();
                    }
                });
                saveListPanel.add(loadButton);
                saveListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(saveListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI()); // Özelleştirilmiş scrollbar

        JButton exitButton = createStyledButton("EXIT");
        exitButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            onExit.execute();
            dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(exitButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    private void loadCustomFont() {
        InputStream fontStream = getClass().getResourceAsStream("/fonts/ThaleahFat.ttf");
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(24f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 16);
        }
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(350, 60));
        button.setMaximumSize(new Dimension(350, 60));
        button.setFont(customFont);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }


    private static class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(100, 150, 200);
            this.trackColor = new Color(66, 40, 53);
        }
    }
}
