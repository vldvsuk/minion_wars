package Controllers;


import models.GameActions;
import models.GameState;
import models.minions.Minion;
import models.powers.Power;
import view.hexagon.TileManager;
import view.panel.ActionPanel;
import models.grond.Tile;

public class ButtonController {
    private final GameState gameState;
    private final GameActions actions;
    private final ActionPanel actionPanel;
    private final TileManager tileManager;
    private final EffectProcessor effectProcessor;

    public ButtonController(GameState gameState, GameActions actions, ActionPanel actionPanel,
                            TileManager tileManager, EffectProcessor effectProcessor) {
        this.gameState = gameState;
        this.actions = actions;
        this.actionPanel = actionPanel;
        this.tileManager = tileManager;
        this.effectProcessor = effectProcessor;
    }

    public void handleBeurtButton() {
        gameState.switchPlayer();
        gameState.resetBeurtButton();
        gameState.resetProcessedMinions();
        tileManager.resetAllOverlays();
        tileManager.markHomebases();

        if (!gameState.isPlacementPhase()) {
            actions.resetActions();
            //effectProcessor.processEffects();
            actionPanel.updatePowerButtonsStyle();
        }
    }

    public void handleStayAction() {
        gameState.setSelectedPower(null);
        Tile selectedTile = gameState.getSelectedTile();
        if (selectedTile != null) {
            Minion minion = gameState.getPlacedMinion(selectedTile);
            if (actions.hasAttacked()) {
                gameState.addProcessedMinion(minion);
                actions.oneMoreMinionProcessed();
                gameState.setCurrentMinion(null);
            }
            actions.setHasMoved(true);
        }
    }

    public void handleBasicAttack() {
        gameState.setSelectedPower(null);
        actions.setBasisAttacked(true);
        actions.setSpecialAttack(false);
        tileManager.resetAllOverlays();
        // Attack range highlighting zou in UI controller kunnen
    }

    public void handleHeal() {
        gameState.setSelectedPower(null);
        Tile selectedTile = gameState.getSelectedTile();
        if (selectedTile != null) {
            Minion minion = gameState.getPlacedMinion(selectedTile);
            if (minion != null) {
                int newDefence = Math.min(
                        minion.getCurrentDefence() + 2,
                        minion.getDefence()
                );
                minion.setCurrentDefence(newDefence);
                minion.setHealCount(minion.getHealCount() + 1);
                if (actions.hasMoved()) {
                    gameState.addProcessedMinion(minion);
                    actions.oneMoreMinionProcessed();
                    gameState.setCurrentMinion(null);
                }
                actions.setHasAttacked(true);
                gameState.setCurrentlySelectedHex(null);
                tileManager.resetAllOverlays();
            }
        }
    }

    public void handlePowerSelect(Power power) {
        if (gameState.getSelectedPower() == power) {
            gameState.setSelectedPower(null);
        } else {
            gameState.setSelectedPower(power);
        }
        actionPanel.updatePowerButtonsStyle();
        actions.clearPowerTiles();
    }

    // Andere methodes zoals handleTabChange, handleSpecialAttack, etc.
}