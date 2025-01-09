package domain.core;

import controller.HallController;
import controller.ModeController;
import controller.TimerController;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private ModeController modeController;
    private ArrayList<Hall> halls;
    private TimerController timerController;
    private HallController hallController;
    private Hero hero;
    private boolean isRunning;

    public Game() {
        initializeGame();
    }

    private void initializeGame() {
        hallController = new HallController(hero);
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

    public Hall getWaterHall() {
        return halls.get(1);
    }

    public ArrayList<Hall> getHalls() {
        return halls;
    }

    public Hall getEarthHall() {
        return halls.get(0);
    }

    public Hall getFireHall() {
        return halls.get(2);
    }

    public Hall getAirHall() {
        return halls.get(3);
    }

    public Hero getHero() {
        return hero;
    }
}
