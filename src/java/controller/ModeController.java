package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import domain.gameobjects.Hall;
import ui.swing.BuildModeScreen;
import ui.swing.GameOverScreen;
import ui.swing.GameScreen;
import ui.swing.MainMenu;

public class ModeController {
    private BuildModeScreen buildModeScreen;
    private GameScreen gameScreen;
    private MainMenu mainMenu;
    private GameOverScreen gameOverScreen;
    private ArrayList<Hall> allHalls;
    private HallController hallController;
    private MinObjectController minObjectController;

    public ModeController(HallController hallController) {
        this.hallController = hallController;
        this.minObjectController = new MinObjectController(hallController);
        showMainMenu();
    }

    public void showMainMenu() {
        closeActiveScreens();
        if (mainMenu == null) {
            mainMenu = new MainMenu(this::switchToBuildMode);
        }
        mainMenu.setVisible(true);
    }

    public void switchToBuildMode() {
        closeActiveScreens();
        if (buildModeScreen == null) {
            buildModeScreen = new BuildModeScreen(this::showMainMenu, this::switchToPlayMode, hallController);
        }
        buildModeScreen.setVisible(true);
    }

    public void switchToPlayMode() {
        if (!minObjectController.areAllMinimumRequirementsMet()) {
            showWarning("Minimum requirements not met for all Halls. Please check and try again.");
            return;
        }else{
            closeActiveScreens();
            if (gameScreen == null) {
                gameScreen = new GameScreen(this::switchToGameOverScreen, hallController);
        }
            gameScreen.setVisible(true);
        }
    }

    private void showWarning(String message) {
        JWindow warningWindow = new JWindow();
        warningWindow.setLayout(new BorderLayout());
    
        // Uyarı mesajı etiketi
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
    
        // Kaplama paneli
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 200)); // Yarı saydam siyah arka plan
        panel.add(messageLabel, BorderLayout.CENTER);
    
        // "OK" düğmesi
        JButton closeButton = new JButton("OK");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        closeButton.addActionListener(e -> warningWindow.dispose());
        panel.add(closeButton, BorderLayout.SOUTH);
    
        warningWindow.add(panel);
    
        // Pencere boyutu ve konumu
        warningWindow.setSize(400, 200);
        warningWindow.setLocationRelativeTo(null);
        warningWindow.setAlwaysOnTop(true); // Her zaman üstte
        warningWindow.setVisible(true);
    
        // Uyarıyı belirli bir süre ekranda tut
        Timer timer = new Timer(5000, e -> {
            if (warningWindow.isVisible()) {
                warningWindow.toFront(); // Her durumda üstte tut
            }
        });
        timer.setRepeats(false); 
        timer.start();
    }
    
    

    public void switchToGameOverScreen() {
        closeActiveScreens();
        if (gameOverScreen == null) {
            gameOverScreen = new GameOverScreen(this::switchToPlayMode, this::showMainMenu, hallController);
        }
        gameOverScreen.setVisible(true);
    }

    private void closeActiveScreens() {
        if (mainMenu != null) {
            mainMenu.dispose();
            mainMenu = null;
        }
        if (buildModeScreen != null) {
            buildModeScreen.dispose();
            buildModeScreen = null;
        }
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
        if (gameOverScreen != null) {
            gameOverScreen.dispose();
            gameOverScreen = null;
        }
    }
}
