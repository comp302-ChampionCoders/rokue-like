package domain.states;
import domain.builders.GameBuilder;

public class BuildState implements GameState {
    private GameBuilder builder;
    private boolean isBuilding;

    public BuildState(String hallType) {
        this.builder = new GameBuilder(hallType);
        this.isBuilding = true;
    }

    @Override
    public void update() {
        // Update build mode logic
    }

    @Override
    public void render() {
        // Render build interface
    }

    @Override
    public void handleInput(String input) {
        if (!isBuilding) return;

        switch (input) {
            case "PLACE_OBJECT":
                // Handle object placement
                break;
            case "MOVE_CURSOR":
                // Handle cursor movement
                break;
            case "FINISH":
                if (builder.validateHall()) {
                    isBuilding = false;
                    // Transition to play state
                }
                break;
        }
    }
}