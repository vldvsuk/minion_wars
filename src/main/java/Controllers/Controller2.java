package Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import models.GameState;
import models.minions.Minion;
import parsers.FieldParser;
import parsers.MinionParser;
import java.util.ArrayList;
import java.util.List;
import models.grond.Tile;
import view.button.MinionButtonFactory;
import view.hexagon.HexagonData;
import view.hexagon.HexagonFactory;
import view.images.ImageLoader;
import view.images.ImagePatternHelper;
import view.ui.UIManager;

public class Controller2 {
    private GameState gameState;
    private UIManager uiManager;
    private MinionButtonFactory buttonFactory;
    private HexagonFactory hexagonFactory;

    private final List<Button> minionButtons = new ArrayList<>();
    private final List<Polygon> hexList = new ArrayList<>();
    private Polygon currentlySelectedHex = null;

    @FXML private SplitPane splitPane;
    @FXML private Label naamLabel;
    @FXML private Label coinsLabel;
    @FXML private ImageView coinImageView;
    @FXML private VBox minionsContainer;
    @FXML private AnchorPane gameBoardContainer;
    @FXML private ScrollPane gameScrollPane;

    @FXML
    public void initialize() {
        if (!hexList.isEmpty()) return;
        hexagonFactory = new HexagonFactory();


        setupCoinImage();
        setupSplitPane();
        setupKeyListener();
    }

    private void setupCoinImage() {
        coinImageView.setImage(ImageLoader.loadCoinIcon());
    }

    private void setupSplitPane() {
        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> splitPane.setDividerPositions(0.25));
        });
    }

    private void createMinionButtons() {
        MinionParser parser = new MinionParser();
        List<Minion> minions = parser.parseMinions();
        for (Minion minion : minions) {
            Button button = buttonFactory.createMinionButton(minion);
            minionsContainer.getChildren().add(button);
            minionButtons.add(button);
        }
    }

    private void createHexagons() {
        FieldParser fieldParser = new FieldParser();
        List<Tile> tiles = fieldParser.parseField();
        for (Tile tile : tiles) {
            HexagonData hexData = hexagonFactory.createHexagon(tile);
            hexList.add(hexData.getHex());
            gameBoardContainer.getChildren().addAll(hexData.getHex(), hexData.getOverlay());
            hexData.getHex().setUserData(hexData);


            hexData.getOverlay().setOnMouseClicked(e -> handleTileClick(hexData.getHex()));
        }
    }

    private void setupKeyListener() {
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
        positionViewportForPlayer();
        updateUI();
        resetAllOverlays();
        markHomebases();
    }

    private void resetAllOverlays() {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            data.getOverlay().setFill(Color.TRANSPARENT);
            data.getOverlay().setOpacity(0);
        }
    }


    public void setInfo(String speler1, String speler2, int munten) {
        this.gameState = new GameState(speler1, speler2, munten);

        // Initialiseer factories en UI
        this.buttonFactory = new MinionButtonFactory(gameState, minionButtons);
        this.uiManager = new UIManager(gameState, naamLabel, coinsLabel);

        updateUI();
        createMinionButtons();
        createHexagons();
        markHomebases();
    }

    private void updateUI() {
        uiManager.updateUI();


        if (!gameState.isPlacementPhase() && !minionButtons.isEmpty()) {
            removeMinionButtons();
        }else{
            updateButtonStates();
        }

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
                }else{
                    button.getStyleClass().remove("selected");
                }
            }
        }

        if (!anyAffordable && gameState.getSelectedMinion() != null) {
            gameState.setSelectedMinion(null);
            minionButtons.forEach(btn -> btn.getStyleClass().remove("selected"));
        }
    }

    private void handleTileClick(Polygon hex) {
        HexagonData data = (HexagonData) hex.getUserData();
        Tile tile = data.getTile();
        Polygon overlayHex = data.getOverlay();

        // Reset previous selection
        if (currentlySelectedHex != null) {
            HexagonData prevData = (HexagonData) currentlySelectedHex.getUserData();
            prevData.getOverlay().setFill(Color.TRANSPARENT);
            prevData.getOverlay().setOpacity(0);
        }

        if (currentlySelectedHex == hex) {
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

    private void placeMinion(Tile tile, Polygon hex) {
        gameState.deductCoins(gameState.getSelectedMinion().getCost());
        try {
            hex.setFill(ImagePatternHelper.createMinionPattern(gameState.getSelectedMinion().getType()));
        } catch (Exception e) {
            hex.setFill(Color.RED);
        }

        gameState.placeMinion(tile, gameState.getSelectedMinion());
        currentlySelectedHex = null;
        updateUI();
    }

    private void selectMinion(Tile tile, Polygon overlayHex) {
        if (gameState.isMinionOwnedByCurrentPlayer(tile)) {
            gameState.setSelectedTile(tile);
            overlayHex.setFill(Color.rgb(0, 255, 0, 0.2));
            overlayHex.setOpacity(0.7);
            overlayHex.setStroke(Color.GREEN);
        }
    }

    private void markHomebases() {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();
            Polygon overlayHex = data.getOverlay();

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
                    overlayHex.setFill(gameState.isSpeler1AanZet() ?
                            (tile.getHomebase() == 1 ? Color.YELLOW : Color.RED) :
                            (tile.getHomebase() == 2 ? Color.YELLOW : Color.RED));
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

                event.consume();
            }
        }
    }

    private void resetTileVisual(Tile tile) {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            if (data.getTile().equals(tile)) {
                try {
                    hex.setFill(ImagePatternHelper.createTilePattern(tile.getType().toLowerCase()));
                } catch (Exception e) {
                    hex.setFill(Color.LIGHTGRAY);
                }

                // Reset overlay
                data.getOverlay().setFill(Color.TRANSPARENT);
                data.getOverlay().setOpacity(0);
                data.getOverlay().setStroke(Color.BLACK);
                data.getOverlay().setStrokeWidth(1.5);
                data.getOverlay().setStyle("");
                markHomebases();
                break;
            }
        }
    }

    private void updateMinionVisibility() {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();

            if (gameState.isOccupied(tile)) {
                if (gameState.isPlacementPhase()) {
                    boolean showMinion = gameState.isMinionOwnedByCurrentPlayer(tile);

                    if (showMinion) {
                        Minion minion = gameState.getPlacedMinion(tile);
                        try {
                            hex.setFill(ImagePatternHelper.createMinionPattern(minion.getType()));
                        } catch (Exception e) {
                            hex.setFill(Color.RED);
                        }
                    } else {
                        resetTileToOriginal(hex, tile);
                    }
                } else {
                    // Show all minions after placement phase
                    Minion minion = gameState.getPlacedMinion(tile);
                    try {
                        hex.setFill(ImagePatternHelper.createMinionPattern(minion.getType()));
                    } catch (Exception e) {
                        hex.setFill(Color.RED);
                    }
                }
            }
        }
    }

    private void resetTileToOriginal(Polygon hex, Tile tile) {
        try {
            hex.setFill(ImagePatternHelper.createTilePattern(tile.getType().toLowerCase()));
        } catch (Exception e) {
            hex.setFill(Color.LIGHTGRAY);
        }
    }

    private void positionViewportForPlayer() {
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

    private void removeMinionButtons() {
        minionsContainer.getChildren().clear();  // Verwijder alle children uit de VBox
        minionButtons.clear();                   // Maak de lijst met buttons leeg
        gameState.setSelectedMinion(null);       // Reset de geselecteerde minion
    }
}