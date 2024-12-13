package src.main.java.domain.states;

import src.main.java.domain.builders.GameBuilder;

public interface GameState {
    void update();
    void render();
    void handleInput(String input);
}
