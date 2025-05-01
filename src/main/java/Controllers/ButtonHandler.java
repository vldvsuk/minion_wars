package Controllers;

import javafx.scene.control.Button;
import javafx.scene.shape.Polygon;
import models.GameState;
import models.GameActions;
import models.grond.Tile;
import models.minions.Minion;
import models.powers.Power;
import view.hexagon.TileManager;
import java.util.List;

/**Controller voor button management en de interacties met de model**/
public class ButtonHandler {
    private final GameState gameState;
    private final TileManager tileManager;
    private final ActionController actionController;
    private final GameActions actions;

    public ButtonHandler(GameState gameState, TileManager tileManager, ActionController actionController) {
        this.gameState = gameState;
        this.tileManager = tileManager;
        this.actionController = actionController;
        this.actions = gameState.getGameActions();
    }

    public void handleBonus(){
        actionController.handlePower(actions.getPowerTiles());
        gameState.setSelectedPower(null);
        gameState.setPowerBoolean(true);
        gameState.powerUse();
        tileManager.resetAllOverlays();
    }

    public void handleSpecialAttack(){
        actions.setBasisAttacked(false);
        actions.setSpecialAttack(true);
    }

    public void handleOnStayButton() {
        gameState.setSelectedPower(null);
        if (gameState.getSelectedTile() != null) {
            Minion minion = gameState.getPlacedMinion(gameState.getSelectedTile());
            if (actions.hasAttacked()) {
                gameState.addProcessedMinion(minion);
                actions.oneMoreMinionProcessed();
                gameState.setCurrentMinion(null);
            }
            actions.setHasMoved(true);
            tileManager.resetAllOverlays();
            gameState.setCurrentlySelectedHex(null);
        }
    }
    public void handleBasicAttack(){
        gameState.setSelectedPower(null);
        tileManager.resetAllOverlays();
        actions.setBasisAttacked(true);
        actions.setSpecialAttack(false);
    }

    public void handleHeal(){
        gameState.setSelectedPower(null);
        if (gameState.getSelectedTile() != null) {
            Minion minion = gameState.getPlacedMinion(gameState.getSelectedTile());
            if (minion != null) {
                int newDefence = Math.min(
                        minion.getCurrentDefence() + 2,   //controle zodat een minion niet meer defence heeft dan in de xml
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
    public void handlePowerSelect(Power power){
        // Toggle
        if (gameState.getSelectedPower() == power) {
            gameState.setSelectedPower(null);
        } else {
            gameState.setSelectedPower(power);
        }
        // Update UI
        actions.clearPowerTiles();
    }
    public void handleRustButton(){
        Tile tile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(tile);

        if (minion != null && !gameState.getProcessedMinions().contains(minion)) {
            actions.setHasAttacked(true);
            actions.setHasMoved(true);
            actions.oneMoreMinionProcessed();
            gameState.addProcessedMinion(minion);

            if (minion.isSpecialAttackUsed()) {
                // Als 2 keer gerust, reset speciale aanval
                minion.setRestCount(minion.getRestCount() + 1);
                if (minion.getRestCount() >= 2) {
                    minion.setSpecialAttackUsed(false);
                    minion.setRestCount(0);
                }
            }
        }
        tileManager.resetAllOverlays();
    }

    public void updateButtonStates(List<Button> minionButtons) { // de uitzicht van de minions buttons
        boolean anyAffordable = false;

        for (Button button : minionButtons) {
            Minion minion = (Minion) button.getUserData();
            button.getStyleClass().removeAll("unaffordable", "selected");

            if (!gameState.canAffordMinion(minion)) {
                button.getStyleClass().add("unaffordable");
            } else {
                anyAffordable = true;
                if (gameState.getSelectedMinion() == minion) {
                    button.getStyleClass().add("selected");
                } else {
                    button.getStyleClass().remove("selected");
                }
            }
        }

        if (!anyAffordable && gameState.getSelectedMinion() != null) {
            gameState.setSelectedMinion(null);
            minionButtons.forEach(btn -> btn.getStyleClass().remove("selected"));
        }
    }

    public void handleBeurtButton(List<Polygon> hexList) { // de reset na elke beurt
        gameState.switchPlayer();
        actionController.getEffectProcessor().processEffects(hexList);
        gameState.resetBeurtButton();
        actions.resetActions();
        gameState.resetProcessedMinions();
        tileManager.resetAllOverlays();
        tileManager.markHomebases();
    }
}