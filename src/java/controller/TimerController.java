package controller;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

import domain.gameobjects.Hall.HallType;

public class TimerController {
    private static TimerController instance;
    private Map<String, Timer> timers;
    private boolean isPaused;
    private int remainingTime;

    // Timer constants
    private static final int MONSTER_MOVE_DELAY = 500;
    private static final int MONSTER_SPAWN_DELAY = 8000;
    private static final int RUNE_TELEPORT_DELAY = 5000;
    private static final int ARCHER_ATTACK_DELAY = 1000;
    private static final int GAME_TIMER_DELAY = 1000;
    private static final int ENCHANTMENT_SPAWN_DELAY = 12000;
    private static final int ENCHANTMENT_REMOVE_DELAY = 6000;

    private static final int DEFAULT_GAME_TIME = 50;
    private static final int HERO_MOVE_DELAY = 200;
    private Map<HallType, Integer> hallRemainingTimes;

    private Timer heroMoveTimer;
    private Runnable heroMoveAction;

    private TimerController() {
        timers = new HashMap<>();
        isPaused = false;
        remainingTime = DEFAULT_GAME_TIME;
    }

    public static TimerController getInstance() {
        if (instance == null) {
            instance = new TimerController();
        }
        return instance;
    }

    public void initializeGameTimers(Runnable monsterMoveAction, 
                                   Runnable monsterSpawnAction,
                                   Runnable runeTeleportAction,
                                   Runnable archerAttackAction,
                                   Runnable timeUpdateAction,
                                   Runnable enchantmentSpawnAction,
                                   Runnable enchantmentRemoveAction) {
                                    
        timers.put("monsterMove", new Timer(MONSTER_MOVE_DELAY, e -> {
            if (!isPaused) monsterMoveAction.run();
        }));

        timers.put("monsterSpawn", new Timer(MONSTER_SPAWN_DELAY, e -> {
            if (!isPaused) monsterSpawnAction.run();
        }));

        timers.put("runeTeleport", new Timer(RUNE_TELEPORT_DELAY, e -> {
            if (!isPaused) runeTeleportAction.run();
        }));

        timers.put("archerAttack", new Timer(ARCHER_ATTACK_DELAY, e -> {
            if (!isPaused) archerAttackAction.run();
        }));

        timers.put("gameTimer", new Timer(GAME_TIMER_DELAY, e -> {
            if (!isPaused) timeUpdateAction.run();
        }));

        timers.put("enchantmentSpawn", new Timer(ENCHANTMENT_SPAWN_DELAY, e -> {
            if (!isPaused) enchantmentSpawnAction.run();
        }));

        timers.put("enchantmentRemove", new Timer(ENCHANTMENT_REMOVE_DELAY, e -> {
            if (!isPaused) enchantmentRemoveAction.run();
        }));
    }

    public void startTimers() {
        timers.values().forEach(Timer::start);
    }

    public void stopTimers() {
        timers.values().forEach(Timer::stop);
    }

    public void cleanup() {
        stopTimers();
        timers.clear();
    }

    public void resetGameTime() {
        remainingTime = DEFAULT_GAME_TIME; 
    }

    public int getRemainingGameTime(HallType hall) {
        return hallRemainingTimes.get(hall);
    }

    public void initializeHeroTimer(Runnable moveAction) {
        this.heroMoveAction = moveAction;
        heroMoveTimer = new Timer(HERO_MOVE_DELAY, e -> {
            if (!isPaused && heroMoveAction != null) {
                heroMoveAction.run();
            }
        });
    }
    
    public void startHeroTimer() {
        if (heroMoveTimer != null) {
            heroMoveTimer.start();
        }
    }
    
    public void stopHeroTimer() {
        if (heroMoveTimer != null) {
            heroMoveTimer.stop();
        }
    }

    public void setRemainingTimeForHall(HallType hallType, int timeInSeconds) {
        if (hallRemainingTimes == null) {
            hallRemainingTimes = new HashMap<>();
        }
        hallRemainingTimes.put(hallType, timeInSeconds);
        System.out.println("Set remaining time for " + hallType + ": " + timeInSeconds + " seconds.");
}
    
}