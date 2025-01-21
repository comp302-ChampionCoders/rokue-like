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

    private Map<String, Integer> remainingTimes; // Timer'ların kalan sürelerini tutar
    private Map<String, Long> lastPausedTimes; // Timer'ların durdurulduğu zamanı tutar


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
        remainingTimes = new HashMap<>();
        lastPausedTimes = new HashMap<>();
                                    
        timers.put("monsterMove", new Timer(MONSTER_MOVE_DELAY, e -> {
            if (!isPaused) monsterMoveAction.run();
        }));
        remainingTimes.put("monsterMove", MONSTER_MOVE_DELAY);

        timers.put("monsterSpawn", new Timer(MONSTER_SPAWN_DELAY, e -> {
            if (!isPaused) monsterSpawnAction.run();
        }));
        remainingTimes.put("monsterSpawn", MONSTER_SPAWN_DELAY);

        timers.put("runeTeleport", new Timer(RUNE_TELEPORT_DELAY, e -> {
            if (!isPaused) runeTeleportAction.run();
        }));
        remainingTimes.put("runeTeleport", RUNE_TELEPORT_DELAY);

        timers.put("archerAttack", new Timer(ARCHER_ATTACK_DELAY, e -> {
            if (!isPaused) archerAttackAction.run();
        }));
        remainingTimes.put("archerAttack", ARCHER_ATTACK_DELAY);

        timers.put("gameTimer", new Timer(GAME_TIMER_DELAY, e -> {
            if (!isPaused) timeUpdateAction.run();
        }));
        remainingTimes.put("gameTimer", GAME_TIMER_DELAY);

        // Spawn timer (every 12 seconds)
        timers.put("enchantmentSpawn", new Timer(ENCHANTMENT_SPAWN_DELAY, e -> {
            if (!isPaused) {
                enchantmentSpawnAction.run();
                // Start removal timer when spawning
                Timer removalTimer = new Timer(ENCHANTMENT_REMOVE_DELAY, e2 -> {
                    if (!isPaused) enchantmentRemoveAction.run();
                });
                removalTimer.setRepeats(false);  // One-time use
                removalTimer.start();
            }
        }));

//        timers.put("enchantmentRemove", new Timer(ENCHANTMENT_REMOVE_DELAY, e -> {
//            if (!isPaused) enchantmentRemoveAction.run();
//        }));
//        remainingTimes.put("enchantmentRemove", ENCHANTMENT_REMOVE_DELAY);
    }

    public void startTimers() {
        timers.values().forEach(Timer::start);
    }

    public void stopTimers() {
        timers.values().forEach(Timer::stop);
    }

    public void pauseTimers() {
        isPaused = true;
        timers.values().forEach(Timer::stop); // Tüm timer'lar durdurulur
    }

    public void resumeTimers() {
        if (!isPaused) return; // Eğer zaten çalışıyorsa, hiçbir şey yapma
        isPaused = false;
        timers.values().forEach(Timer::start); // Tüm timer'lar tekrar başlatılır
    }

    public Map<HallType, Integer> getHallTimes() {
        if (hallRemainingTimes == null) {
            hallRemainingTimes = new HashMap<>();
        }
        return new HashMap<>(hallRemainingTimes);
    }


    public void cleanup() {
        stopTimers();
        timers.clear();
    }

    public void reset() {
        cleanup();
        remainingTimes.clear();
        lastPausedTimes.clear();
        isPaused = false;
    }

    public void resetGameTime() {
        remainingTime = DEFAULT_GAME_TIME; 
    }

    public int getRemainingGameTime(HallType hall) {
        return hallRemainingTimes.get(hall);
    }

    public void setRemainingTimeForHall(HallType hallType, int timeInSeconds) {
        if (hallRemainingTimes == null) {
            hallRemainingTimes = new HashMap<>();
        }
        hallRemainingTimes.put(hallType, timeInSeconds);
        System.out.println("Set remaining time for " + hallType + ": " + timeInSeconds + " seconds.");
}
    
}