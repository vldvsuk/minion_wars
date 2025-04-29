package Controllers;

import models.GameActions;
import models.GameLogic;
import models.GameState;
import models.effects.Effect;
import models.grond.Tile;
import models.minions.Minion;
import view.hexagon.TileManager;

import java.util.Set;

public class ActionController {

    private final GameState gameState;
    private final GameLogic gameLogic;
    private final TileManager tileManager;
    private GameActions actions;

    public ActionController(GameState gameState, GameLogic gameLogic, TileManager tileManager) {
        this.gameState = gameState;
        this.gameLogic = gameLogic;
        this.tileManager = tileManager;
        this.actions = gameState.getGameActions();
    }

    public boolean canMove(Tile tile) {
        return "Bewegen".equals(actions.getCurrentTab())
                && gameState.getSelectedTile() != null
                && actions.getReachableTiles().contains(tile)
                && !actions.hasMoved();
    }

    public boolean canAttack(Tile tile) {
        return "Aanvallen".equals(actions.getCurrentTab())
                && gameState.getSelectedTile() != null
                && actions.getAttackableTiles().contains(tile)
                && gameState.isOccupied(tile)
                && !gameState.isMinionOwnedByCurrentPlayer(tile)
                && !actions.hasAttacked()
                && (actions.isBasisAttacked() || actions.isSpecialAttack());
    }

    public void handleMove(Tile targetTile) {
        Tile originalTile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(originalTile);
        gameState.removeMinion(originalTile);
        gameState.placeMinion(targetTile, minion);
        // Update visuals
        tileManager.resetTileVisual(originalTile);
        tileManager.updateMinionVisual(targetTile, minion);

        if(actions.hasAttacked()){
            gameState.addProcessedMinion(minion);
            actions.oneMoreMinionProcessed();
            gameState.setCurrentMinion(null);
        }
        actions.setHasMoved(true);
        // Reset selection
        actions.clearReachableTiles();
        tileManager.resetAllOverlays();
    }

    public void handleAttack(Tile tile) {
        Minion attacker = gameState.getPlacedMinion(gameState.getSelectedTile());
        Minion defender = gameState.getPlacedMinion(tile);

        if (attacker != null && defender != null) {
            defender.verminderCurrentDefence(attacker.getAttack());

            if (actions.isSpecialAttack() && attacker.hasSpecialAbility()) {
                String effectName = attacker.getEffect();
                Effect effect = gameState.findEffectByName(effectName);
                attacker.setSpecialAttackUsed(true);
                attacker.setRestCount(0);
                // effect gevonden
                if (effect != null) {
                    // de kopie
                    Effect appliedEffect = new Effect(
                            effect.getType(),
                            effect.getName(),
                            effect.getDuration(),
                            attacker.getEffectValue() != 0 ? attacker.getEffectValue() : effect.getValue()
                    );
                    if ("rage".equals(effect.getType()) || "heal".equals(effect.getType())) {
                        attacker.addEffect(appliedEffect);
                    } else {
                        defender.addEffect(appliedEffect);
                    }
                }
            }

            if (defender.getCurrentDefence() <= 0) {
                gameState.removeMinion(tile);
                tileManager.resetTileVisual(tile);
            }

            if (actions.hasMoved()) {
                gameState.addProcessedMinion(attacker);
                actions.oneMoreMinionProcessed();
            }
            actions.setHasAttacked(true);
            gameState.setCurrentMinion(null);
            tileManager.resetAllOverlays();
        }
    }
    public void handlePower(Set<Tile> powerTiles) {

    }
}

