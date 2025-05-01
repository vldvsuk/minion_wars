package view.ui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.GameState;
import view.images.ImageLoader;

/** Handelt de UI-elementen van het spel af, inclusief lay-out en actieknoppen**/

public class GameView {
    private final GameState gameState;

    public GameView(GameState gameState) {
        this.gameState = gameState;
    }

    // Initialiseert hoofd-UI componenten (muntafbeelding en gesplitste paneel)
    public void initializeUI(SplitPane splitPane, ImageView coinImageView) {
        setupCoinImage(coinImageView);
        setupSplitPane(splitPane);
    }

    // Laadt en toont de munt-icoonafbeelding
    private void setupCoinImage(ImageView coinImageView) {
        coinImageView.setImage(ImageLoader.loadCoinIcon());
    }

    // Zorgt dat het splitpaneel altijd op 25% positie blijft
    private void setupSplitPane(SplitPane splitPane) {
        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> splitPane.setDividerPositions(0.25)));
    }

    // Maakt een teller-label voor minions met blauwe tekst
    public Label makeMinionCountLabel(){
        Label minionCountLabel = new Label("0/2");
        minionCountLabel.setTextFill(Color.BLUE);
        minionCountLabel.setFont(Font.font("System Bold Italic", 24));
        return minionCountLabel;
    }

    // Vervangt munten-weergave door minion-icoon en teller
    public void replaceCoinsDisplay(Label minionCountLabel, HBox coinsHBox) {
        ImageView minionIcon = new ImageView(ImageLoader.loadUseMinionIcon());
        minionIcon.setFitHeight(26);
        minionIcon.setFitWidth(24);
        minionIcon.setPreserveRatio(true);

        coinsHBox.getChildren().clear();
        coinsHBox.getChildren().addAll(minionIcon, minionCountLabel);
    }

    // Maakt een 'Rust'-knop met standaardstyling
    public Button makeRustButton(){
        Button rustButton = new Button("Rust");
        rustButton.setPrefWidth(80);
        rustButton.setPrefHeight(35);
        rustButton.setFont(Font.font("System", FontWeight.BOLD, 15));
        return rustButton;
    }

    // Configureert actieknoppen (vervangt bestaande knoppen)
    public void setupActionButtons(Button beurtButton, Button rustButton) {
        HBox buttonContainer = (HBox) beurtButton.getParent();
        buttonContainer.getChildren().clear();
        beurtButton.setPrefHeight(35);
        buttonContainer.getChildren().addAll(rustButton, beurtButton);
        buttonContainer.setSpacing(5);
    }

    // Past zichtgebied aan gebaseerd op actieve speler
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

