package view.ui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.GameState;
import view.hexagon.HexagonFactory;
import view.hexagon.TileManager;
import view.images.ImageLoader;

public class GameView {
    private final TileManager tileManager;
    private final GameState gameState;

    public GameView(GameState gameState, TileManager tileManager) {
        this.gameState = gameState;
        this.tileManager = tileManager;

    }

    public void initializeUI(SplitPane splitPane, ImageView coinImageView) {
        setupCoinImage(coinImageView);
        setupSplitPane(splitPane);
    }

    private void setupCoinImage(ImageView coinImageView) {
        coinImageView.setImage(ImageLoader.loadCoinIcon());
    }

    private void setupSplitPane(SplitPane splitPane) {
        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> splitPane.setDividerPositions(0.25)));
    }


    public Label makeMinionCountLabel(){
        Label minionCountLabel = new Label("0/2");
        minionCountLabel.setTextFill(Color.BLUE);
        minionCountLabel.setFont(Font.font("System Bold Italic", 24));
        return minionCountLabel;
    }

    public void replaceCoinsDisplay(Label minionCountLabel, HBox coinsHBox) {
        ImageView minionIcon = new ImageView(ImageLoader.loadUseMinionIcon());
        minionIcon.setFitHeight(26);
        minionIcon.setFitWidth(24);
        minionIcon.setPreserveRatio(true);

        coinsHBox.getChildren().clear();
        coinsHBox.getChildren().addAll(minionIcon, minionCountLabel);
    }

    public Button makeRustButton(){
        Button rustButton = new Button("Rust");
        rustButton.setPrefWidth(80);
        rustButton.setPrefHeight(35);
        rustButton.setFont(Font.font("System", FontWeight.BOLD, 15));
        return rustButton;
    }


    public void setupActionButtons(Button beurtButton, Button rustButton) {

        // Verwijder bestaande button
        HBox buttonContainer = (HBox) beurtButton.getParent();
        buttonContainer.getChildren().clear();
        beurtButton.setPrefHeight(35);
        buttonContainer.getChildren().addAll(rustButton, beurtButton);
        buttonContainer.setSpacing(5);
    }

    public void positionViewportForPlayer(ScrollPane gameScrollPane) {
        if (gameScrollPane != null) {
            Platform.runLater(() -> {
                if (gameState.isSpeler1AanZet()) {
                    // Naar linksboven
                    gameScrollPane.setHvalue(0);
                    gameScrollPane.setVvalue(0);
                } else {
                    // Naar rechtsonder
                    gameScrollPane.setHvalue(1);
                    gameScrollPane.setVvalue(1);
                }});
        }
    }
}
