package Controllers;
import be.ugent.objprog.minionwars.MinionWars;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.GameActions;
import models.GameLogic;
import models.GameState;
import models.minions.Minion;
import models.parsers.FieldParser;
import models.parsers.MinionParser;
import java.io.IOException;
import java.util.*;
import models.grond.Tile;
import models.powers.Power;
import view.button.MinionButtonFactory;
import view.hexagon.HexagonData;
import view.hexagon.HexagonFactory;
import view.hexagon.TileManager;
import view.images.ImagePatternHelper;
import view.panel.ActionPanel;
import view.ui.GameView;
import view.ui.InfoPanel;
import view.ui.UIManager;

public class Controller2 {
    private GameState gameState;
    private GameLogic gameLogic;
    private UIManager uiManager;
    private MinionButtonFactory buttonFactory;
    private HexagonFactory hexagonFactory;
    private InfoPanel infoPanel;
    private TileManager tileManager;
    private GameView gameView;
    private GameActions actions;
    private ButtonManager buttonManager;
    private EffectProcessor effectProcessor;
    private ActionController actionController;
    private UIController uiController;
    private final List<Button> minionButtons = new ArrayList<>();
    private final List<Polygon> hexList = new ArrayList<>();
    private VBox labelBox;

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
    private HBox coinsHBox;
    @FXML
    private Label minionCountLabel;
    @FXML
    private Button beurtButton;
    @FXML
    private AnchorPane gameBoardContainer;
    @FXML
    private ScrollPane gameScrollPane;
    private ActionPanel actionPanel;
    private Button rustButton;

    @FXML
    public void initialize() {
        if (!hexList.isEmpty()) return;
        hexagonFactory = new HexagonFactory();
        setupKeyListener();
    }

    public void setInfo(String speler1, String speler2, int munten) {
        FieldParser fieldParser = new FieldParser();
        List<Tile> tiles = fieldParser.parseField();

        this.gameState = new GameState(speler1, speler2, munten, tiles);
        this.gameLogic = new GameLogic(gameState);
        this.buttonFactory = new MinionButtonFactory(gameState, minionButtons);
        this.uiManager = new UIManager(gameState, naamLabel, coinsLabel);
        this.tileManager = new TileManager(gameState, hexList);
        this.gameView = new GameView(gameState);
        this.infoPanel = new InfoPanel(gameState);
        this.actions = gameState.getGameActions();
        this.effectProcessor = new EffectProcessor(gameState, tileManager);
        this.actionController = new ActionController(gameState, tileManager);
        this.uiController = new UIController(gameState, uiManager, infoPanel);
        this.actionPanel = new ActionPanel(
                gameState,
                this::handleStayAction,
                this::handleBasicAttack,
                this::handleHeal,
                this::onSpecialAttackAction,
                this::handlePowerSelect,
                this::handleTabChange

        );


        gameView.initializeUI(splitPane, coinImageView);
        updateUI();
        createMinionButtons();
        createHexagons(tiles);
        tileManager.markHomebases();
    }

    @FXML
    private void handleBeurtButton() {
        gameState.switchPlayer();
        gameState.resetBeurtButton();
        gameView.positionViewportForPlayer(gameScrollPane);
        gameState.resetProcessedMinions();
        tileManager.resetAllOverlays();
        tileManager.markHomebases();
        updateUI();

        if (!gameState.isPlacementPhase()) {
            beurtButton.setDisable(true);
            actions.resetActions();
            updateMinionCountLabel();
            effectProcessor.processEffects(hexList);
            actionPanel.updatePowerButtonsStyle();
            checkVoorSpelEinde();
        }

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

    private void createHexagons(List<Tile> tiles) {
        for (Tile tile : tiles) {
            HexagonData hexData = hexagonFactory.createHexagon(tile);
            hexList.add(hexData.getHex());
            gameBoardContainer.getChildren().addAll(hexData.getHex(), hexData.getOverlay());
            hexData.getHex().setUserData(hexData);

            hexData.getOverlay().setOnMouseClicked(e -> handleTileClick(hexData.getHex()));
            hexData.getOverlay().setOnMouseEntered(e -> handleHoverStart(hexData.getTile()));
            hexData.getOverlay().setOnMouseExited(e -> handleHoverEnd());
        }
    }

    private void handleHoverStart(Tile hoveredTile) {
        if (gameState.getSelectedPower() != null && !gameState.getPowerBoolean() && gameState.getPowerUsed() < 2) {
            updatePowerRangeVisuals(hoveredTile);
        }
    }

    private void handleHoverEnd() {
        if (gameState.getSelectedPower() != null) {
            tileManager.resetAllOverlays();
        }
    }


    private void setupKeyListener() {
        gameBoardContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
            }
        });
    }


    private void updateUI() {
        uiManager.updateUI();

        if (!gameState.isPlacementPhase()) {
            afterPlacement();
            beurtButton.setDisable(actions.getMinionProcessed() < gameState.getTotalMinions());
            updateActionButtonsState();

        } else {
            updateButtonStates();
            tileManager.markHomebases();
        }
        tileManager.updateMinionVisibility();

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
                } else {
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
        Minion clickedMinion = gameState.getPlacedMinion(tile);

        if (!gameState.isPlacementPhase()) {
            tileManager.resetAllOverlays();
            if (clickedMinion != null) {
                labelBox.getChildren().clear();
                VBox nieuweVBox = infoPanel.generateMinionInfo(tile, clickedMinion);
                labelBox.getChildren().add(nieuweVBox);
                int aantalEffecten = clickedMinion.getActiveEffects().size();
                labelBox.setMinHeight(100 + (aantalEffecten * 22));
            }
        }
        if (gameState.getSelectedPower() != null && !gameState.getPowerBoolean() && gameState.getPowerUsed() < 2) {
            handleBonus();
            return;
        }

        if (actions.getMinionProcessed() >= gameState.getTotalMinions()) {
            tileManager.resetAllOverlays();
            return;
        }

        if (clickedMinion != null && gameState.getProcessedMinions().contains(clickedMinion)) return;

        if (gameState.getCurrentMinion() == null) {
            gameState.setCurrentMinion(clickedMinion);
        } else if (gameState.getCurrentMinion() != clickedMinion) return;

        if (actions.hasMoved() && actions.hasAttacked()) {
            actions.setHasMoved(false);
            actions.setHasAttacked(false);
            gameState.setCurrentMinion(null);
            gameState.setSelectedTile(null);
        }
        if (!gameState.isPlacementPhase()) {
            //Bewegen
            if (actionController.canMove(tile)) {
                actionController.handleMove(tile);
                updateAfterAction();
                selectMinion(tile, overlayHex);
            }
            // Handle attack

            else if (actionController.canAttack(tile)) {
                actionController.handleAttack(tile);
                updateAfterAction();
                return;
            }
        }
        actions.setBasisAttacked(false);
        actions.setSpecialAttack(false);

        if (!actions.hasAttacked() && !actions.hasMoved()) {
            gameState.setCurrentMinion(null);
            if (!gameState.isOccupied(tile) && !gameState.isPlacementPhase()) {
                if (gameState.getCurrentTile() == null) {
                    gameState.setCurrentTile(tile);
                    selectTile(tile, overlayHex);
                } else if (gameState.getCurrentTile() == tile) {
                    tileManager.resetAllOverlays();
                    labelBox.getChildren().clear();
                    gameState.setCurrentTile(null);
                } else {
                    tileManager.resetAllOverlays();
                    selectTile(tile, overlayHex);
                    gameState.setCurrentTile(tile);
                }
                return;
            }
        } else {
            updateActionButtonsState();
            return;
        }

        // Reset previous selection
        gameState.setCurrentTile(null);
        if (gameState.getCurrentlySelectedHex() != null) {
            HexagonData prevData = (HexagonData) gameState.getCurrentlySelectedHex().getUserData();
            prevData.getOverlay().setFill(Color.TRANSPARENT);
            prevData.getOverlay().setOpacity(0);
        }

        if (gameState.getCurrentlySelectedHex() == hex) {
            gameState.setCurrentlySelectedHex(null);
            gameState.setSelectedTile(null);
            if (!gameState.isPlacementPhase()) {
                tileManager.resetAllOverlays();
                labelBox.getChildren().clear();
            }
            return;
        }

        if (gameState.getSelectedMinion() != null) {
            if (gameState.isValidPlacement(tile)) {
                placeMinion(tile, hex);
            } else if (gameState.isOccupied(tile) && gameState.isMinionOwnedByCurrentPlayer(tile)) {
                selectMinion(tile, overlayHex);
                gameState.setCurrentlySelectedHex(hex);
            }

        } else if (gameState.isOccupied(tile)) {
            if (gameState.isMinionOwnedByCurrentPlayer(tile)) {
                selectMinion(tile, overlayHex);
                gameState.setCurrentlySelectedHex(hex);
            }
        }
    }

    private void updateAfterAction() {
        updateMinionCountLabel();
        checkTurnCompletion();
        updateActionButtonsState();
        checkVoorSpelEinde();
    }

    private void handleBonus() {
        actionController.handlePower(actions.getPowerTiles());
        gameState.setSelectedPower(null);
        gameState.setPowerBoolean(true);
        gameState.powerUse();
        actionPanel.updatePowerButtonsStyle();
        tileManager.resetAllOverlays();
        checkVoorSpelEinde();
    }


    private void selectTile(Tile tile, Polygon overlayHex) {
        gameState.setSelectedTile(null);
        if (!gameState.isPlacementPhase()) {
            tileManager.resetAllOverlays();
        }
        tileManager.markSelected(overlayHex);
        if (!gameState.isPlacementPhase()) {
            labelBox.getChildren().clear();
            HBox nieuweHBox = infoPanel.generateTileInfo(tile);
            labelBox.getChildren().add(0, nieuweHBox);
        }
    }


    private void placeMinion(Tile tile, Polygon hex) {
        Minion original = gameState.getSelectedMinion();
        Minion kopie = original.copy();

        gameState.deductCoins(kopie.getCost());
        try {
            hex.setFill(ImagePatternHelper.createMinionPattern(kopie.getType()));
        } catch (Exception e) {
            hex.setFill(Color.RED);
        }

        gameState.placeMinion(tile, kopie);
        gameState.setCurrentlySelectedHex(null);
        updateUI();

    }

    private void selectMinion(Tile tile, Polygon overlayHex) {
        if (!gameState.isPlacementPhase()) {
            tileManager.resetAllOverlays();
        }

        if (gameState.isMinionOwnedByCurrentPlayer(tile)) {
            gameState.setSelectedTile(tile);
            tileManager.markSelected(overlayHex);
            if (!gameState.isPlacementPhase()) {
                Minion minion = gameState.getPlacedMinion(tile);
                updateTileInteraction(tile, minion);
            }
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE && gameState.isPlacementPhase()) {
            Tile selectedTile = gameState.getSelectedTile();
            if (selectedTile != null && gameState.isOccupied(selectedTile)) {
                Minion removedMinion = gameState.getPlacedMinion(selectedTile);
                gameState.refundCoins(removedMinion.getCost());
                tileManager.resetTileVisual(selectedTile);
                gameState.removeMinion(selectedTile);
                gameState.setSelectedTile(null);
                gameState.setCurrentlySelectedHex(null);
                updateUI();
                event.consume();
            }
        }
    }

    private void afterPlacement() {
        removeMinionButtons();
        addCustomVBox();
        minionCountLabel = gameView.makeMinionCountLabel();
        gameView.replaceCoinsDisplay(minionCountLabel, coinsHBox);
        rustButton = gameView.makeRustButton();
        rustButton.setOnAction(e -> handleRustButton());
        gameView.setupActionButtons(beurtButton, rustButton);
        this.buttonManager = new ButtonManager(actionPanel, rustButton, gameState, gameLogic);
    }

    private void removeMinionButtons() {
        minionsContainer.getChildren();
        minionButtons.clear();
        gameState.setSelectedMinion(null);
    }

    private void addCustomVBox() {
        minionsContainer.getChildren().clear();
        VBox topVBox = actionPanel.initializePanel();
        VBox tabPane = actionPanel.createTabPane();
        labelBox = actionPanel.createLabelBox();
        topVBox.getChildren().addAll(labelBox, tabPane);
        minionsContainer.getChildren().add(0, topVBox);
    }

    private void onSpecialAttackAction() {
        actions.setBasisAttacked(false);
        actions.setSpecialAttack(true);
        highlightAttackRange();
    }

    private void handleStayAction() {
        gameState.setSelectedPower(null);
        if (gameState.getSelectedTile() != null) {
            Minion minion = gameState.getPlacedMinion(gameState.getSelectedTile());
            if (actions.hasAttacked()) {
                gameState.addProcessedMinion(minion);
                actions.oneMoreMinionProcessed();
                gameState.setCurrentMinion(null);
            }
            actions.setHasMoved(true);
            updateMinionCountLabel();
            tileManager.resetAllOverlays();
            gameState.setCurrentlySelectedHex(null);
            checkTurnCompletion();
            updateActionButtonsState();
        }
    }

    private void handleBasicAttack() {
        gameState.setSelectedPower(null);
        tileManager.resetAllOverlays();
        actions.setBasisAttacked(true);
        actions.setSpecialAttack(false);
        highlightAttackRange();
    }

    private void handleHeal() {
        gameState.setSelectedPower(null);
        if (gameState.getSelectedTile() != null) {
            Minion minion = gameState.getPlacedMinion(gameState.getSelectedTile());
            if (minion != null) {
                int newDefence = Math.min(
                        minion.getCurrentDefence() + 2,
                        minion.getDefence()
                );
                minion.setCurrentDefence(newDefence);
                minion.setHealCount(minion.getHealCount() + 1);

                if (actions.hasMoved()) {
                    gameState.addProcessedMinion(minion);
                    actions.oneMoreMinionProcessed();
                    gameState.setCurrentMinion(null);
                }
                actions.setHasAttacked(true);
                gameState.setCurrentlySelectedHex(null);
                updateMinionCountLabel();
                tileManager.resetAllOverlays();
                checkTurnCompletion();
                updateActionButtonsState();
            }
        }
    }

    private void handlePowerSelect(Power power) {
        // Toggle
        if (gameState.getSelectedPower() == power) {
            gameState.setSelectedPower(null);
        } else {
            gameState.setSelectedPower(power);
        }
        // Update UI
        actionPanel.updatePowerButtonsStyle();
        actions.clearPowerTiles();
    }

    private void handleTabChange(String tabTitle) {
        actions.setCurrentTab(tabTitle);
        gameState.setSelectedPower(null);
        if (gameState.getSelectedTile() != null) {
            Tile tile = gameState.getSelectedTile();
            Minion minion = gameState.getPlacedMinion(tile);
            updateTileInteraction(tile, minion);
        }
    }

    private void updateTileInteraction(Tile tile, Minion minion) {
        tileManager.resetAllOverlays();
        if (minion != null) {
            if ("Bewegen".equals(actions.getCurrentTab()) && !actions.hasMoved()) {
                actions.setReachableTiles(gameLogic.calculateMovementRange(tile, minion.getCurrentMovement()));
                highlightReachableTiles();
            } else if ("Aanvallen".equals(actions.getCurrentTab()) && !actions.hasAttacked()) {
                actions.setAttackableTiles(gameLogic.calculateAttackRange(tile, minion.getMinRange(), minion.getCurrentMaxRange()));
                highlightAttackRange();
                updateActionButtonsState();

                if (actions.isHasNoAction()) {
                    gameState.addProcessedMinion(minion);
                    actions.oneMoreMinionProcessed();
                    gameState.setCurrentMinion(null);
                    actions.setHasMoved(true);
                    actions.setHasAttacked(true);
                    gameState.setSelectedTile(null);
                    tileManager.resetAllOverlays();
                }
            }
        }
        updateActionButtonsState();
    }

    private void handleRustButton() {
        Tile tile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(tile);

        if (minion != null && !gameState.getProcessedMinions().contains(minion)) {
            actions.setHasAttacked(true);
            actions.setHasMoved(true);
            actions.oneMoreMinionProcessed();
            gameState.addProcessedMinion(minion);

            if (minion.isSpecialAttackUsed()) {
                // Als 2 keer gerust, reset speciale aanval
                minion.setRestCount(minion.getRestCount() + 1);
                if (minion.getRestCount() >= 2) {
                    minion.setSpecialAttackUsed(false);
                    minion.setRestCount(0);
                }
            }


        }
        tileManager.resetAllOverlays();
        updateMinionCountLabel();
        checkTurnCompletion();
    }

    private void updatePowerRangeVisuals(Tile centerTile) {
        actions.clearPowerTiles();
        String powerType = gameState.getSelectedPower().getType().toLowerCase();
        actions.setPowerTiles(gameLogic.calculateBonusRange(centerTile, gameState.getSelectedPower().getRadius()));
        boolean hasValidTarget = switch (powerType) {
            case "healing" -> gameLogic.hasFriendlyInRange(actions.getPowerTiles());
            case "fireball", "lightning" -> gameLogic.hasEnemyInAttackRange(actions.getPowerTiles());
            default -> false;
        };

        if (hasValidTarget) {
            tileManager.highlightTiles(actions.getPowerTiles(), Color.BLUE);
        } else {
            tileManager.highlightTiles(actions.getPowerTiles(), Color.GREEN);
        }
    }

    private void highlightReachableTiles() {
        tileManager.highlightTiles(actions.getReachableTiles(), Color.GREEN);
    }

    private void highlightAttackRange() {
        tileManager.highlightTiles(actions.getAttackableTiles(), Color.RED);
    }

    private void checkVoorSpelEinde() {
        String winnaar = gameState.getWinnaar();
        if (winnaar != null) {
            toonEindScherm(winnaar);
        }
    }

    private void toonEindScherm(String winnaar) {
        try {
            FXMLLoader loader = new FXMLLoader(MinionWars.class.getResource("einde.fxml"));
            Parent root = loader.load();

            EndController endController = loader.getController();
            endController.setWinnaar(winnaar);

            Stage stage = (Stage) beurtButton.getScene().getWindow();
            Scene scene = new Scene(root, 800, 700);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMinionCountLabel() {
        if (actions.getMinionProcessed() <= gameState.getTotalMinions()) {
            minionCountLabel.setText(actions.getMinionProcessed() + "/" + gameState.getTotalMinions());
        }
        updateActionButtonsState();
    }

    private void checkTurnCompletion() {
        if (actions.getMinionProcessed() >= gameState.getTotalMinions()) {
            beurtButton.setDisable(false);
            tileManager.resetAllOverlays();
            gameState.setSelectedTile(null);
            gameState.setCurrentlySelectedHex(null);
            gameState.setCurrentMinion(null);

        }
    }

    private void updateActionButtonsState() {
        buttonManager.updateButtons();
    }
}