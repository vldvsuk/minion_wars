package Controllers;

import javafx.scene.shape.Polygon;
import models.GameState;
import models.effects.Effect;
import models.grond.Tile;
import models.minions.Minion;
import view.hexagon.HexagonData;
import view.hexagon.TileManager;

import java.util.ArrayList;
import java.util.List;

public class EffectProcessor {
    private final GameState gameState;
    private final TileManager tileManager;

    public EffectProcessor(GameState gameState, TileManager tileManager) {
        this.gameState = gameState;
        this.tileManager = tileManager;
    }

    public void processEffects(List<Polygon> hexList) {
        for (Polygon hex :hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();
            if (gameState.isOccupied(tile)) {
                Minion minion = gameState.getPlacedMinion(tile);
                List<Effect> effectsToProcess = new ArrayList<>(minion.getActiveEffects());
                if (effectsToProcess.isEmpty()) {
                    continue;
                }
                for (Effect effect : effectsToProcess) {
                    effect.verminderDuration();
                    minion.applyEffect(effect);

                    if (effect.getDuration() <= 0) {
                        minion.removeEffect(effect);
                    }
                    if (minion.getCurrentDefence() <= 0) {
                        gameState.removeMinion(tile);
                        tileManager.resetTileVisual(tile);
                    }
                }
            }
        }
    }

}
