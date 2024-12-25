package controller;

import java.util.ArrayList;

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

    public ModeController() {
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
            buildModeScreen = new BuildModeScreen(this::showMainMenu, this::switchToPlayMode);
        }
        buildModeScreen.setVisible(true);
    }

    public void switchToPlayMode() {
        if (gameScreen == null) {
            if (buildModeScreen != null) {
                allHalls = buildModeScreen.getAllHalls(); 
                closeActiveScreens(); 
            }
            gameScreen = new GameScreen(this::switchToGameOverScreen, allHalls);
        }
        gameScreen.setVisible(true);
    }
    

    public void switchToGameOverScreen() {
        closeActiveScreens(); 
        if (gameOverScreen == null) {
            gameOverScreen = new GameOverScreen(this::switchToPlayMode, this::showMainMenu);
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
