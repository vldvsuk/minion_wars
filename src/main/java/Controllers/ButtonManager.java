package Controllers;

import javafx.scene.control.Button;
import models.GameActions;
import models.GameLogic;
import models.GameState;
import models.grond.Tile;
import models.minions.Minion;
import view.panel.ActionPanel;
/** Controller die kijkt of er een bepaalde button kan gebruikt worden **/

public class ButtonManager {
    private final ActionPanel actionPanel;
    private final Button rustButton;
    private final GameState gameState;
    private final GameLogic gameLogic;
    private final GameActions actions;


    public ButtonManager(ActionPanel actionPanel, Button rustButton,
                         GameState gameState, GameLogic gameLogic) {
        this.actionPanel = actionPanel;
        this.rustButton = rustButton;
        this.gameState = gameState;
        this.gameLogic = gameLogic;
        this.actions = gameState.getGameActions();
    }

    public void updateButtons() {
        if (gameState.isPlacementPhase()) return;

        Tile tile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(tile);

        if (minion == null) return;

        boolean isParalized = minion.isParalized();
        boolean isDefenceFull = minion.getCurrentDefence() >= minion.getDefence();
        boolean healLimitReached = minion.getHealCount() >= 2;
        boolean enemiesInRange = !isParalized && gameLogic.hasEnemyInAttackRange(actions.getAttackableTiles());
        boolean canUseSpecial = !minion.isSpecialAttackUsed() && !actions.hasAttacked();

        // Bepaal knopstatussen
        boolean disableHeal = isDefenceFull || isParalized || actions.hasAttacked() || healLimitReached;
        boolean disableAttacks = isParalized || actions.hasAttacked() || !enemiesInRange;
        boolean disableRust = isParalized || actions.hasAttacked() || actions.hasMoved();

        // Update knop UI
        actionPanel.setHealDisabled(disableHeal);
        actionPanel.setBasicAndSpecialAttackDisabled(disableAttacks);
        actionPanel.setSpecialAttackVisible(canUseSpecial && minion.hasSpecialAbility() && enemiesInRange);
        actionPanel.setStayButtonDisabled(isParalized || actions.hasMoved());
        rustButton.setDisable(disableRust);

        // Check of alle acties geblokkeerd zijn

        if (disableHeal && disableAttacks && disableRust
                && !gameState.getProcessedMinions().contains(minion)) {
            actions.setHasNoAction(true); // Flag om aan te geven dat er geen acties mogelijk zijn
        } else {
            actions.setHasNoAction(false);
        }
    }
}

