package src.main.java.domain.builders;

import src.main.java.domain.gameobjects.*;
import java.util.HashMap;
import java.util.Map;

public class GameBuilder {
    private static final Map<String, Integer> MIN_OBJECTS = new HashMap<>() {{
        put("EARTH", 6);
        put("AIR", 9);
        put("WATER", 13);
        put("FIRE", 17);
    }};

    private Hall currentHall;
    private final String hallType;
    private int objectCount;

    public GameBuilder(String hallType) {
        this.hallType = hallType.toUpperCase();
        this.objectCount = 0;
        Hero hero = new Hero(0, 0); // Default position
        this.currentHall = new Hall(20, 20, hero); // Standard size
    }

    public GameBuilder placeObject(GameObject object, int x, int y) {
        if (currentHall.isValidPosition(x, y)) {
            currentHall.addObject(object, x, y);
            objectCount++;
        }
        return this;
    }

    public GameBuilder placeHero(int x, int y) {
        if (currentHall.isValidPosition(x, y)) {
            currentHall.setHeroPosition(x, y);
        }
        return this;
    }

    public boolean validateHall() {
        return objectCount >= MIN_OBJECTS.getOrDefault(hallType, 0);
    }

    public Hall build() {
        if (!validateHall()) {
            throw new IllegalStateException(
                "Hall " + hallType + " requires minimum " + 
                MIN_OBJECTS.get(hallType) + " objects."
            );
        }
        return currentHall;
    }
}