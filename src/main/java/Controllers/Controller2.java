package Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.GameState;
import models.Minion;
import parsers.FieldParser;
import parsers.MinionParser;
import java.util.ArrayList;
import java.util.List;

import grond.Tile;

public class Controller2 {
    private Stage stage;
    private GameState gameState;
    private final List<Button> minionButtons = new ArrayList<>();
    private final List<Polygon> hexList = new ArrayList<>();
    private Polygon currentlySelectedHex = null;

    @FXML
    private SplitPane splitPane;
    @FXML
    private Label naamLabel;
    @FXML
    private Label coinsLabel;
    @FXML
    private ImageView coinImageView;
    @FXML
    private VBox minionsContainer;
    @FXML
    private AnchorPane gameBoardContainer;


    @FXML
    public void initialize() {

        if (!hexList.isEmpty()) {
            return;
        }

        Image coinImage = new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/coin-FFB900.png"));
        coinImageView.setImage(coinImage);

        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> splitPane.setDividerPositions(0.25));
        });

        MinionParser parser = new MinionParser();
        List<Minion> minions = parser.parseMinions();
        for (Minion minion : minions) {
            Button minionButton = createMinionButton(minion);
            minionsContainer.getChildren().add(minionButton);
        }

        FieldParser fieldParser = new FieldParser();
        List<Tile> tiles = fieldParser.parseField();
        for (Tile tile : tiles) {
            createHexagon(tile);
        }
        gameBoardContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
            }
        });
    }

    @FXML
    private void handleBeurtButton() {
        gameState.switchPlayer();
        gameState.setSelectedMinion(null);
        gameState.setSelectedTile(null);
        currentlySelectedHex = null;
        updateUI();

        // Forceer update van alle overlays
        for (Polygon hex : hexList) {
            HexData data = (HexData) hex.getUserData();
            data.overlay.setFill(Color.TRANSPARENT);
            data.overlay.setOpacity(0);
        }
        markHomebases();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInfo(String speler1, String speler2, int munten) {
        this.gameState = new GameState(speler1, speler2, munten);
        updateUI();
    }

    private void updateUI() {
        // Update labels en buttons
        naamLabel.setText(gameState.getCurrentPlayerName());
        coinsLabel.setText(String.valueOf(gameState.getCurrentCoins()));
        updateButtonStates();


        updateMinionVisibility();
        markHomebases();
    }

    private void updateButtonStates() {
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
                }
            }
        }

        if (!anyAffordable && gameState.getSelectedMinion() != null) {
            gameState.setSelectedMinion(null);
            minionButtons.forEach(btn -> btn.getStyleClass().remove("selected"));
        }
    }


    private Button createMinionButton(Minion minion) {
        Button button = new Button();
        button.getStyleClass().add("minion-button");
        minionButtons.add(button);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(125);
        button.setPrefHeight(125);
        button.setUserData(minion);

        HBox content = new HBox(17);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/" + minion.getType() + ".png"));
            imageView.setImage(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            Circle clip = new Circle(50, 50, 50);
            imageView.setClip(clip);

        } catch (Exception e) {
            System.err.println("Image not found for: " + minion.getType());
            Image fallback = new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/trebuchet.png"));
            imageView.setImage(fallback);
        }

        HBox mainContent = new HBox(22);
        mainContent.setAlignment(Pos.CENTER_LEFT);
        mainContent.setStyle("-fx-padding: 0 0 0 10;");

        // Minion name
        Label nameLabel = new Label(minion.getName());
        nameLabel.getStyleClass().add("minion-name");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPrefWidth(200);

        VBox statsBox = new VBox(5);
        statsBox.setAlignment(Pos.CENTER_RIGHT);

        // Coin
        HBox coinBox = new HBox(5);
        ImageView coinIcon = new ImageView();
        coinIcon.setFitHeight(20);
        coinIcon.setFitWidth(20);
        coinIcon.setPreserveRatio(true);
        try {
            coinIcon.setImage(new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/coin-FFB900.png")));
        } catch (Exception e) {
            System.err.println("Coin icon not found");
        }
        Label costLabel = new Label(String.valueOf(minion.getCost()));
        costLabel.getStyleClass().add("cost-stat");
        coinBox.getChildren().addAll(coinIcon, costLabel);

        // Attack
        HBox attackBox = new HBox(5);
        ImageView attackIcon = new ImageView();
        attackIcon.setFitHeight(20);
        attackIcon.setFitWidth(20);
        attackIcon.setPreserveRatio(true);
        try {
            attackIcon.setImage(new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/attack-D60000.png")));
        } catch (Exception e) {
            System.err.println("Attack icon not found");
        }
        Label attackLabel = new Label(String.valueOf(minion.getAttack()));
        attackLabel.getStyleClass().add("attack-stat");
        attackBox.getChildren().addAll(attackIcon, attackLabel);

        // Defense
        HBox defenseBox = new HBox(5);
        ImageView defenseIcon = new ImageView();
        defenseIcon.setFitHeight(20);
        defenseIcon.setFitWidth(20);
        defenseIcon.setPreserveRatio(true);
        try {
            defenseIcon.setImage(new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/health-D60000.png")));
        } catch (Exception e) {
            System.err.println("Defense icon not found");
        }
        Label defenceLabel = new Label(String.valueOf(minion.getDefence()));
        defenceLabel.getStyleClass().add("defence-stat");


        defenseBox.getChildren().addAll(defenseIcon, defenceLabel);
        statsBox.getChildren().addAll(coinBox, attackBox, defenseBox);
        mainContent.getChildren().addAll(nameLabel, statsBox);
        content.getChildren().addAll(imageView, mainContent);
        button.setGraphic(content);


        button.setOnAction(e -> {
            if (gameState.getSelectedMinion() == minion) {
                gameState.setSelectedMinion(null);
                button.getStyleClass().remove("selected");
            } else if (gameState.canAffordMinion(minion)) {
                gameState.setSelectedMinion(minion);

                minionButtons.forEach(btn -> btn.getStyleClass().remove("selected"));

                button.getStyleClass().add("selected");
            }
        });

        return button;
    }


    public void createHexagon(Tile tile) {
        double hexSize = 74.0;
        double n = Math.sqrt(hexSize * hexSize * 0.75);
        double hexWidth = 2 * n;
        double hexHeight = 2 * hexSize;

        double xCoord = tile.getX() * hexWidth + (tile.getY() % 2) * n;
        double yCoord = tile.getY() * hexHeight * 0.75;

        xCoord += 80;
        yCoord += 40;

        // Main hexagon
        Polygon hex = new Polygon(
                xCoord, yCoord,
                xCoord + n, yCoord + hexSize * 0.5,
                xCoord + n, yCoord + hexSize * 1.5,
                xCoord, yCoord + 2 * hexSize,
                xCoord - n, yCoord + hexSize * 1.5,
                xCoord - n, yCoord + hexSize * 0.5
        );


        try {
            String imagePath = "/be/ugent/objprog/minionwars/images/tiles/" + tile.getType().toLowerCase() + ".png";
            ImagePattern imagePattern = new ImagePattern(
                    new Image(getClass().getResourceAsStream(imagePath))
            );
            hex.setFill(imagePattern);
        } catch (Exception e) {
            hex.setFill(Color.LIGHTGRAY);
        }

        hex.setStroke(Color.BLACK);
        hex.setStrokeWidth(1.5);
        hexList.add(hex);

        // Overlay hexagon
        Polygon overlayHex = new Polygon();
        overlayHex.getPoints().addAll(
                xCoord, yCoord,
                xCoord + n, yCoord + hexSize * 0.5,
                xCoord + n, yCoord + hexSize * 1.5,
                xCoord, yCoord + 2 * hexSize,
                xCoord - n, yCoord + hexSize * 1.5,
                xCoord - n, yCoord + hexSize * 0.5
        );

        overlayHex.setFill(Color.TRANSPARENT);
        overlayHex.setStroke(Color.BLACK);
        overlayHex.setStrokeWidth(1.5);
        overlayHex.setOpacity(0);
        overlayHex.setStroke(Color.TRANSPARENT);

        // Store references
        hex.setUserData(new HexData(tile, overlayHex));
        gameBoardContainer.getChildren().addAll(hex, overlayHex);

        overlayHex.setOnMouseClicked(e -> handleTileClick(hex));
    }


    private static class HexData {
        final Tile tile;
        final Polygon overlay;

        HexData(Tile tile, Polygon overlay) {
            this.tile = tile;
            this.overlay = overlay;
        }
    }


    private void handleTileClick(Polygon hex) {
        HexData data = (HexData) hex.getUserData();
        Tile tile = data.tile;
        Polygon overlayHex = data.overlay;

        // Reset vorige selectie
        if (currentlySelectedHex != null) {
            HexData prevData = (HexData) currentlySelectedHex.getUserData();
            prevData.overlay.setFill(Color.TRANSPARENT);
            prevData.overlay.setOpacity(0);
        }

        if (currentlySelectedHex == hex) {
            // Deselecteren
            currentlySelectedHex = null;
            gameState.setSelectedTile(null);
            return;
        }

        if (gameState.getSelectedMinion() != null) {
            if (gameState.isValidPlacement(tile)) {
                placeMinion(tile, hex);
            } else if (gameState.isOccupied(tile) && gameState.isMinionOwnedByCurrentPlayer(tile)) {
                selectMinion(tile, overlayHex);
                currentlySelectedHex = hex;
            }
        } else if (gameState.isOccupied(tile)) {
            if (gameState.isMinionOwnedByCurrentPlayer(tile)) {
                selectMinion(tile, overlayHex);
                currentlySelectedHex = hex;
            }
        }
    }



    public void placeMinion(Tile tile, Polygon hex) {

        gameState.deductCoins(gameState.getSelectedMinion().getCost());
        try {
            ImagePattern minionImage = new ImagePattern(
                    new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/" + gameState.getSelectedMinion().getType() + ".png"))
            );
            hex.setFill(minionImage);
        } catch (Exception e) {
            hex.setFill(Color.RED); // Foutkleur
        }

        gameState.placeMinion(tile, gameState.getSelectedMinion());
        updateUI();

    }

    private void selectMinion(Tile tile, Polygon overlayHex) {
        if (gameState.isMinionOwnedByCurrentPlayer(tile)) {
            gameState.setSelectedTile(tile);
            overlayHex.setFill(Color.rgb(0, 255, 0, 0.2));
            overlayHex.setStyle("-fx-stroke-width: 5;");
            overlayHex.setOpacity(0.5);
            overlayHex.setStroke(Color.GREEN);
        }
    }

    private void markHomebases() {
        for (Polygon hex : hexList) {
            HexData data = (HexData) hex.getUserData();
            Tile tile = data.tile;
            Polygon overlayHex = data.overlay;

            // Reset overlay
            overlayHex.setFill(Color.TRANSPARENT);
            overlayHex.setOpacity(0);
            overlayHex.setStroke(Color.BLACK);
            overlayHex.setStrokeWidth(1.5);

            if (gameState.isPlacementPhase()) {
                if (gameState.isOccupied(tile)) {
                    if (!gameState.isMinionOwnedByCurrentPlayer(tile)) {
                        overlayHex.setFill(gameState.isSpeler1AanZet() ? Color.RED : Color.RED);
                        overlayHex.setOpacity(0.4);
                    }
                } else {
                    if (gameState.isSpeler1AanZet()) {
                        overlayHex.setFill(tile.getHomebase() == 1 ? Color.YELLOW : Color.RED);
                    } else {
                        overlayHex.setFill(tile.getHomebase() == 2 ? Color.YELLOW : Color.RED);
                    }
                    overlayHex.setOpacity(0.4);
                }
            } else {
                if (hex == currentlySelectedHex && gameState.isOccupied(tile) &&
                        gameState.isMinionOwnedByCurrentPlayer(tile)) {
                    overlayHex.setFill(Color.rgb(0, 255, 0, 0.2));
                    overlayHex.setOpacity(0.5);
                }
            }
        }
    }


    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            Tile selectedTile = gameState.getSelectedTile();

            if (selectedTile != null && gameState.isOccupied(selectedTile)) {
                Minion removedMinion = gameState.getPlacedMinion(selectedTile);
                gameState.refundCoins(removedMinion.getCost());

                resetTileVisual(selectedTile);
                gameState.removeMinion(selectedTile);

                gameState.setSelectedTile(null);
                currentlySelectedHex = null;

                updateUI();

                // Voorkom verdere verwerking
                event.consume();
            }
        }
    }

    private void resetTileVisual(Tile tile) {
        for (Polygon hex : hexList) {
            HexData data = (HexData) hex.getUserData();
            if (data.tile.equals(tile)) {
                try {
                    String imagePath = "/be/ugent/objprog/minionwars/images/tiles/" +
                            tile.getType().toLowerCase() + ".png";
                    ImagePattern original = new ImagePattern(
                            new Image(getClass().getResourceAsStream(imagePath))
                    );
                    hex.setFill(original);
                    hex.setStroke(Color.BLACK);
                    hex.setStrokeWidth(1.5);
                    hex.setStyle("");

                    // Reset overlay
                    data.overlay.setFill(Color.TRANSPARENT);
                    data.overlay.setOpacity(0);
                    data.overlay.setStroke(Color.BLACK);
                    data.overlay.setStrokeWidth(1.5);
                    data.overlay.setStyle("");
                    markHomebases();

                } catch (Exception e) {
                    hex.setFill(Color.LIGHTGRAY);
                    hex.setStroke(Color.BLACK);
                }
                break;
            }
        }
    }

    private void updateMinionVisibility() {
        for (Polygon hex : hexList) {
            HexData data = (HexData) hex.getUserData();
            Tile tile = data.tile;

            if (gameState.isOccupied(tile)) {
                if (gameState.isPlacementPhase()) {
                    boolean showMinion = gameState.isMinionOwnedByCurrentPlayer(tile);

                    if (showMinion) {
                        Minion minion = gameState.getPlacedMinion(tile);
                        try {
                            ImagePattern minionImage = new ImagePattern(
                                    new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/" + minion.getType() + ".png"))
                            );
                            hex.setFill(minionImage);
                        } catch (Exception e) {
                            hex.setFill(Color.RED);
                        }
                    } else {
                        resetTileToOriginal(hex, tile);
                    }
                } else {
                    // Na 2 alle minions tonen
                    Minion minion = gameState.getPlacedMinion(tile);
                    try {
                        ImagePattern minionImage = new ImagePattern(
                                new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/" + minion.getType() + ".png"))
                        );
                        hex.setFill(minionImage);
                    } catch (Exception e) {
                        hex.setFill(Color.RED);
                    }
                }
            }
        }
    }

    private void resetTileToOriginal(Polygon hex, Tile tile) {
        try {
            ImagePattern original = new ImagePattern(
                    new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/tiles/" + tile.getType().toLowerCase() + ".png"))
            );
            hex.setFill(original);
        } catch (Exception e) {
            hex.setFill(Color.LIGHTGRAY);
        }
    }

}
