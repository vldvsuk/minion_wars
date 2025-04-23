package Controllers;

import models.GameState;
import models.grond.Tile;

public class ActionController {
    private final GameState gameState;

    public ActionController(GameState gameState) {
        this.gameState = gameState;
    }

    public void handleMoveAction(Tile targetTile) {
        // Beweeglogica
    }

    public void handleAttackAction(Tile targetTile) {
        // Aanvalslogica
    }
}

