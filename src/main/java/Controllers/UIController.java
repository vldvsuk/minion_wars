package Controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import models.GameState;

import view.panel.ActionPanel;
import view.ui.GameView;
import view.ui.InfoPanel;
import view.ui.UIManager;

public class UIController {

    private final GameView gameView;


    public UIController(GameView gameView,SplitPane splitPane, ImageView coinImageView) {
        this.gameView = gameView;
        gameView.initializeUI(splitPane, coinImageView);
    }

    public void updateMinionCountLabel(Label label, int processed, int total) {
        label.setText(processed + "/" + total);
    }

    public void setupActionButtons(Button beurtButton, Button rustButton) {
        gameView.setupActionButtons(beurtButton, rustButton);
    }
}