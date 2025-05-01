package Controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import models.GameState;
import models.grond.Tile;
import models.minions.Minion;
import view.hexagon.TileManager;
import view.ui.GameView;
import view.ui.InfoPanel;

/**  View-helper, verandering van de info-panel en aantal processed minions **/

public class UIController {

    private final GameState gameState;
    private final GameView gameView;
    private final InfoPanel infoPanel;
    private final TileManager tileManager;


    public UIController(GameState gameState, GameView gameView, TileManager tileManager,SplitPane splitPane, ImageView coinImageView) {
        this.gameState = gameState;
        this.gameView = gameView;
        this.tileManager = tileManager;
        this.infoPanel = new InfoPanel(gameState);
        gameView.initializeUI(splitPane, coinImageView);
    }

    public void updateMinionCountLabel(Label label, int processed, int total) { // countLabel update
        label.setText(processed + "/" + total);
    }

    public void setupActionButtons(Button beurtButton, Button rustButton) {
        gameView.setupActionButtons(beurtButton, rustButton);
    }

    public void handleMinionInfo(Tile tile, Minion clickedMinion, VBox labelBox) { // minion info panel
        labelBox.getChildren().clear();
        VBox nieuweVBox = infoPanel.generateMinionInfo(tile, clickedMinion);
        labelBox.getChildren().add(nieuweVBox);
        int aantalEffecten = clickedMinion.getActiveEffects().size();
        labelBox.setMinHeight(100 + (aantalEffecten * 22));
    }

    public void handleTileInfo(Tile tile, Polygon overlayHex, VBox labelBox) { // tile info panel
        gameState.setSelectedTile(null);
        tileManager.resetAllOverlays();
        tileManager.markSelected(overlayHex);
        labelBox.getChildren().clear();
        HBox nieuweHBox = infoPanel.generateTileInfo(tile);
        labelBox.getChildren().addFirst(nieuweHBox);

    }
}