package Controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.GameState;
import models.grond.Tile;
import models.minions.Minion;
import view.ui.InfoPanel;
import view.ui.UIManager;

public class UIController {

    private final GameState gameState;
    private final UIManager uiManager;
    private final InfoPanel infoPanel;

    public UIController(GameState gameState, UIManager uiManager, InfoPanel infoPanel) {
        this.gameState = gameState;
        this.uiManager = uiManager;
        this.infoPanel = infoPanel;
    }

    public void updateMainUI(Label minionCountLabel, HBox coinsHBox, Button beurtButton) {
            uiManager.updateUI();
            updateMinionCountLabel(minionCountLabel);
            updateTurnButton(beurtButton);
    }

    private void updateMinionCountLabel(Label label) {
        label.setText(gameState.getGameActions().getMinionProcessed() + "/" +  gameState.getTotalMinions());
    }

    private void updateTurnButton(Button button) {
        button.setDisable(gameState.getGameActions().getMinionProcessed() < 2);
    }

    public void updateTileInfo(VBox labelBox, Tile tile) {
        labelBox.getChildren().clear();
        labelBox.getChildren().add(infoPanel.generateTileInfo(tile));

    }
    public void updateMinionInfo(VBox labelBox, Minion minion){

    }
}