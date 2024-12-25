package controller;

import ui.swing.BuildModeScreen;
import ui.swing.GameOverScreen;
import ui.swing.GameScreen;
import ui.swing.MainMenu;
import ui.swing.PauseScreen;

public class ModeController {
    private BuildModeScreen buildModeScreen;
    private GameScreen gameScreen;
    private MainMenu mainMenu;
    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;

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

    public void pauseScreen(){
        if (pauseScreen == null) {
            pauseScreen = new PauseScreen(null, null, null);
        }
        pauseScreen.setVisible(true);
    }


    public void switchToBuildMode() {
        closeActiveScreens();
        if (buildModeScreen == null) {
            buildModeScreen = new BuildModeScreen(this::showMainMenu, this::switchToPlayMode);
        }
        buildModeScreen.setVisible(true);
    }

    public void switchToPlayMode() {
        closeActiveScreens(); 
        if (gameScreen == null) {
            gameScreen = new GameScreen(this::switchToGameOverScreen);
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
