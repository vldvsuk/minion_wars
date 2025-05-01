package view.ui;

import javafx.scene.control.Label;
import models.GameState;

/** Klasse voor de udating vain de names en coins**/

public class UIManager {
    private final GameState gameState;
    private final Label naamLabel;
    private final Label coinsLabel;

    public UIManager(GameState gameState, Label naamLabel, Label coinsLabel) {
        this.gameState = gameState;
        this.naamLabel = naamLabel;
        this.coinsLabel = coinsLabel;
    }

    public void updateUI() {
        naamLabel.setText(gameState.getCurrentPlayerName());
        coinsLabel.setText(String.valueOf(gameState.getCurrentCoins()));
    }
}
