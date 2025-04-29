package Controllers;

import javafx.scene.control.Button;
import models.GameActions;
import models.GameLogic;
import models.GameState;
import models.grond.Tile;
import models.minions.Minion;
import view.panel.ActionPanel;

import java.util.Set;

public class ButtonManager {
    private final ActionPanel actionPanel;
    private final Button rustButton;
    private final GameState gameState;
    private final GameLogic gameLogic;

    public ButtonManager(ActionPanel actionPanel, Button rustButton,
                           GameState gameState, GameLogic gameLogic) {
        this.actionPanel = actionPanel;
        this.rustButton = rustButton;
        this.gameState = gameState;
        this.gameLogic = gameLogic;
    }

    public void updateButtons(Set<Tile> attackableTiles) {

        if (gameState.isPlacementPhase()) return;

        GameActions actions = gameState.getGameActions();
        Tile tile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(tile);
        boolean isParalized = minion != null && minion.isParalized();

        boolean enemiesInRange = !isParalized && gameLogic.hasEnemyInAttackRange(attackableTiles);
        boolean canHeal = !isParalized && (minion == null || minion.getHealCount() < 2);
        boolean canUseSpecial = minion != null &&
                !minion.isSpecialAttackUsed() &&
                !actions.hasAttacked();

        actionPanel.setBasicAndSpecialAttackDisabled(isParalized || actions.hasAttacked() || !enemiesInRange);
        actionPanel.setSpecialAttackVisible(canUseSpecial && minion.hasSpecialAbility() && enemiesInRange);
        actionPanel.setHealDisabled(isParalized || actions.hasAttacked() || !canHeal);
        actionPanel.setStayButtonDisabled(isParalized || actions.hasMoved());
        rustButton.setDisable(isParalized || actions.hasAttacked());
    }
}

