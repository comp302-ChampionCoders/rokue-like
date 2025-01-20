package controller;

import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int timeRemaining;
    private List<Hall> halls; // All halls in the game
    private int currentHallIndex; // Index of the current hall
    private Hero hero; // Hero object
    private Rune rune; // Rune object
    private Map<Hall.HallType, Integer> hallTimes; // Remaining time for each hall

    public GameState(List<Hall> halls, int currentHallIndex, Hero hero, Rune rune, Map<Hall.HallType, Integer> hallTimes, int timeRemaining) {
        this.halls = halls;
        this.currentHallIndex = currentHallIndex;
        this.hero = hero;
        this.rune = rune;
        this.timeRemaining = timeRemaining;
        if(hallTimes != null) {
            this.hallTimes = hallTimes;
        }
    }

    // Getters
    public List<Hall> getHalls() {
        return halls;
    }

    public int getCurrentHallIndex() {
        return currentHallIndex;
    }

    public int getTimeRemaining(){
        return timeRemaining;
    }

    public Hero getHero() {
        return hero;
    }

    public Rune getRune() {
        return rune;
    }

    public Map<Hall.HallType, Integer> getHallTimes() {
        return hallTimes;
    }
}

