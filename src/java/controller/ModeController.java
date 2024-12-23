package controller;

import ui.swing.BuildModeScreen;
import ui.swing.GameScreen;

public class ModeController {
    private BuildModeScreen buildModeScreen;
    private GameScreen gameScreen;
    private String currentState;

    public ModeController() {
        currentState = "MainMenu";
    }

    public void switchToBuildMode() {
        if (buildModeScreen == null) {
            buildModeScreen = new BuildModeScreen();
        }
        if (gameScreen != null) {
            gameScreen.setVisible(false);
        }
        buildModeScreen.setVisible(true);
        currentState = "BuildMode";
    }

    public void switchToPlayMode() {
        if (gameScreen == null) {
            gameScreen = new GameScreen();
        }
        if (buildModeScreen != null) {
            buildModeScreen.setVisible(false);
        }
        gameScreen.setVisible(true);
        currentState = "PlayMode";
    }

    public String getCurrentState() {
        return currentState;
    }
}
