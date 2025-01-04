package controller;

import javax.swing.Timer;
import java.util.HashMap;
import java.util.Map;

public class TimerController {
    private static TimerController instance;
    private Map<String, Timer> timers;
    private boolean isPaused;

    // Timer constants
    private static final int MONSTER_MOVE_DELAY = 500;
    private static final int MONSTER_SPAWN_DELAY = 8000;
    private static final int RUNE_TELEPORT_DELAY = 5000;
    private static final int ARCHER_ATTACK_DELAY = 1000;
    private static final int GAME_TIMER_DELAY = 1000;

    private TimerController() {
        timers = new HashMap<>();
        isPaused = false;
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
                                   Runnable timeUpdateAction) {
                                    
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
}