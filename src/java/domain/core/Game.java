package domain.core;

import controller.HallController;
import controller.ModeController;

public class Game {
    private ModeController modeController;
    private HallController hallController;
    private boolean isRunning;

    public Game() {
        initializeGame();
    }

    private void initializeGame() {
        hallController = HallController.getInstance();
        modeController = new ModeController(hallController);
        startGame();   
    }

    public void startGame() {
        isRunning = true;
        modeController.showMainMenu();
    }

    public void stopGame() {
        isRunning = false;
    }

    public ModeController getModeController() {
        return modeController;
    }

    public HallController getHallController() {
        return hallController;
    }
}
