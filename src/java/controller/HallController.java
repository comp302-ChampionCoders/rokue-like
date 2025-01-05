package controller;

import domain.gameobjects.GameObject;
import domain.gameobjects.Hall;
import domain.gameobjects.Hero;

import java.util.ArrayList;

public class HallController {
    private ArrayList<Hall> halls;
    private Hero hero;

    public HallController(Hero hero) {
        this.hero = hero;
        initializeHalls();
    }

    private void initializeHalls() {
        halls = new ArrayList<>();
        halls.add(new Hall(16, 12, hero, Hall.HallType.EARTH));
        halls.add(new Hall(16, 12, hero, Hall.HallType.WATER));
        halls.add(new Hall(16, 12, hero, Hall.HallType.FIRE));
        halls.add(new Hall(16, 12, hero, Hall.HallType.AIR));
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
}
