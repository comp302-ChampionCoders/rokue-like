package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
    
        int windowWidth = (int) (screenWidth * 0.35);
        int windowHeight = (int) (screenHeight * 0.2);
    

        JDialog warningDialog = new JDialog();
        warningDialog.setLayout(new BorderLayout());
        warningDialog.setUndecorated(true);
        warningDialog.setModal(true);
    

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
    

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 200)); 
        panel.add(messageLabel, BorderLayout.CENTER);
    

        JButton closeButton = new JButton("OK");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        closeButton.addActionListener(e -> warningDialog.dispose());
        panel.add(closeButton, BorderLayout.SOUTH);
    
        warningDialog.add(panel);
    
       
        warningDialog.setSize(windowWidth, windowHeight);
        warningDialog.setLocation(
            (screenWidth - windowWidth) / 2, 
            (screenHeight - windowHeight) / 2
        );
    
        
        warningDialog.setAlwaysOnTop(true); 
        warningDialog.setFocusableWindowState(true); 
    
        
        SwingUtilities.invokeLater(() -> {
            warningDialog.toFront();
            warningDialog.setVisible(true);
        });
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
