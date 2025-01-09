package controller;

import domain.behaviors.Direction;
import domain.behaviors.GridElement;
import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;

import java.util.ArrayList;

public class HallController {
    private ArrayList<Hall> halls;
    private Hero hero;
    private Hall currentHall;
    int i;

    public HallController() {
        initializeHalls();
    }

    private void initializeHalls() {
        halls = new ArrayList<>();
        halls.add(new Hall(16, 12, Hall.HallType.EARTH));
        halls.add(new Hall(16, 12, Hall.HallType.WATER));
        halls.add(new Hall(16, 12, Hall.HallType.FIRE));
        halls.add(new Hall(16, 12, Hall.HallType.AIR));
        i = 0;
        currentHall = halls.get(i);
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
        }
    }

    public int getIndex(){
        return i;
    }

    public Hall getCurrentHall(){
        return currentHall;
    }

    public ArrayList<Hall> getHalls() {
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

    public void updateHero(){
        this.hero = SpawnController.getInstance(currentHall).initializeHeroPosition();
    }

    public Hero getHero(){
        return hero;
    }

    public void moveHero(Direction direction){
        currentHall.removeGridElement(hero.getX(), hero.getY());
        hero.move(direction);
        currentHall.addGridElement(hero,hero.getX(), hero.getY());
        
    }

}
