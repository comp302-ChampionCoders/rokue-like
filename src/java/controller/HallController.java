package controller;

import domain.behaviors.Direction;
import domain.behaviors.GridElement;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;
import domain.gameobjects.Rune;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HallController implements Serializable {
    private List<Hall> halls;
    private Hero hero;
    private Rune rune;
    private Hall currentHall;
    int i;
    private int currentHallRemainingTime;
    private SpawnController spawnController;;
    private static HallController instance;

    public HallController() {
        initializeHalls();
    }

    public static HallController getInstance() {
        if (instance == null) {
            instance = new HallController();

        }
        return instance;
    }

    private void initializeHalls() {
        halls = new ArrayList<>();
        halls.add(new Hall(16, 12, Hall.HallType.EARTH));
        halls.add(new Hall(16, 12, Hall.HallType.WATER));
        halls.add(new Hall(16, 12, Hall.HallType.FIRE));
        halls.add(new Hall(16, 12, Hall.HallType.AIR));
        i = 0;
        currentHall = halls.get(i);
        spawnController = SpawnController.getInstance();
    }

    public void goNextHall(){
        if(canGoNextHall()){
            if(i < 3){
                i++;
            }
            currentHall = halls.get(i);
        }
    }

    public boolean canGoNextHall(){
        if(i < 3){
            return true;
        }
        return false;
    }

    public void resetToBuildModeVersions(){
        for(Hall hall: halls){
            for(GridElement gridElement : hall.getGridElements().values()){
                if(!(gridElement instanceof GameObject)){
                    hall.removeGridElement(gridElement.getX(), gridElement.getY());
                }
            }
            hall.setHero(null);
            hall.clearMonsters();
        }
    }

    public int getIndex(){
        return i;
    }

    public Hall getCurrentHall(){
        return currentHall;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public Hall getEarthHall() {
        return halls.get(0);
    }

    public Hall getWaterHall() {
        return halls.get(1);
    }

    public Hall getFireHall() {
        return halls.get(2);
    }

    public Hall getAirHall() {
        return halls.get(3);
    }

    public void updateHall(int index, Hall newHall) {
        if (index >= 0 && index < halls.size()) {
            halls.set(index, newHall);
        }
    }

    public void resetHalls() {
        halls.clear(); 
        initializeHalls();
    }

    public boolean addObjectToHall(Hall.HallType type, GameObject object) {
        return getHall(type).addObject(object, object.getX(), object.getY());
    }

    public void removeObjectFromHall(Hall.HallType type, int x, int y) {
        getHall(type).removeObject(x, y);
    }

    public Hall getHall(Hall.HallType type) {
        return halls.stream().filter(h -> h.getHallType() == type).findFirst().orElse(null);
    }

    public int getCurrentHallRemainingTime(){
        return currentHallRemainingTime;
    }

    public void updateHero(){
        this.hero = spawnController.initializeHeroPosition(currentHall);
    }

    public Hero getHero(){
        return hero;
    }

    public void updateRune(){
        this.rune = spawnController.initializeRune(currentHall);
        currentHall.setRune(rune);   
    }

    public Rune getRune(){
        return rune;
    }

    public void moveHero(Direction direction) {
        int newX = hero.getX() + direction.getDx();
        int newY = hero.getY() + direction.getDy();

        if (isValidMove(newX, newY)) {

            currentHall.removeGridElement(hero.getX(), hero.getY());


            hero.move(direction);
            currentHall.addGridElement(hero, hero.getX(), hero.getY());
        } else {
            System.out.println("Geçersiz hareket: Kahraman bu pozisyona geçemez.");
        }
    }

    public GameState createGameState(Map<Hall.HallType, Integer> hallTimes, int timeRemaining) {
        return new GameState(halls, i, hero, rune, hallTimes, timeRemaining);
    }

    public void loadGameState(GameState gameState) {
        this.halls = gameState.getHalls();
        this.i = gameState.getCurrentHallIndex();
        this.currentHall = halls.get(i);
        this.hero = gameState.getHero();
        this.rune = gameState.getRune();
        this.currentHallRemainingTime = gameState.getTimeRemaining();
        // If needed, reset any runtime-specific data here
    }

    private boolean isValidMove(int x, int y) {

        if (x < 0 || x >= currentHall.getWidth() || y < 0 || y >= currentHall.getHeight()) {
            return false;
        }

        if (currentHall.isPositionOccupied(x, y)) {
            return false;
        }

        return true;
    }

}
