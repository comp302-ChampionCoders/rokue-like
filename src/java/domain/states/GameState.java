package domain.states;

import domain.builders.GameBuilder;

public interface GameState {
    void update();
    void render();
    void handleInput(String input);
}
