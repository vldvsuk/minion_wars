package Controllers;

import models.GameLogic;
import models.GameState;
import models.grond.Tile;
import models.minions.Minion;
import view.hexagon.TileManager;

public class ActionController {

    private final GameState gameState;
    private final GameLogic gameLogic;
    private final TileManager tileManager;

    public ActionController(GameState gameState, GameLogic gameLogic, TileManager tileManager) {
        this.gameState = gameState;
        this.gameLogic = gameLogic;
        this.tileManager = tileManager;
    }

    public void handleMove(Tile targetTile) {
        // Beweeglogica
    }

    public void handleAttack(Minion attacker, Minion defender) {
        // Aanvalslogica
    }
}

