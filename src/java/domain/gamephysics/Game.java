package domain.gamephysics;
import java.util.ArrayList;
import java.util.List;
import domain.gameobjects.*;
import domain.enchantments.*;
import domain.monsters.*;
import technicalservices.*;

public class Game {
    private Hero hero;
    private List<Monster> monsters;
    private Timer timer;
    private String hallName;

    public Game() {
        this.hero = new Hero(0, 0);
        this.monsters = new ArrayList<>();
        this.timer = new Timer(60); // 60 seconds
        this.hallName = "Hall of Earth";
    }

    public void startGame() {
        // Main game loop
    }

public void spawnMonster() {
    int randomType = (int) (Math.random() * 3); // Randomly choose monster type
    int x = (int) (Math.random() * 10); // Random grid position
    int y = (int) (Math.random() * 10);

    Monster monster;
    switch (randomType) {
        case 0:
            monster = new ArcherMonster(x, y);
            break;
        case 1:
            monster = new FighterMonster(x, y);
            break;
        case 2:
        default:
            monster = new WizardMonster(x, y);
            break;
    }
    monsters.add(monster);
    System.out.println(monster.getType() + " spawned at (" + x + ", " + y + ")");
}

public void updateGame() {
    for (Monster monster : monsters) {
        if (monster.isActive()) {
            monster.performAction(hero);
        }
    }
}


}
