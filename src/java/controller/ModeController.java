package controller;

import ui.swing.BuildModeScreen;
import ui.swing.GameScreen;
import ui.swing.MainMenu;

public class ModeController {
    private BuildModeScreen buildModeScreen;
    private GameScreen gameScreen;
    private MainMenu mainMenu;

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
        closeActiveScreens(); 
        if (gameScreen == null) {
            gameScreen = new GameScreen(this::showMainMenu);
        }
        gameScreen.setVisible(true);
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
    }
}
