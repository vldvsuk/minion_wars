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
import view.ui.UIManager;


/**Hoofdcontroller voor het spel, verantwoordelijk voor coördinatie tussen model, view en gebruikersinteracties**/

public class MainController {
    private GameState gameState;                    // Model: Bevat alle speldata
    private GameLogic gameLogic;                    // Model: Spelregels en berekeningen
    private GameActions actions;                    // Model: Bevat speciefike date over actions en tegels
    private TileManager tileManager;                // View: Beheert tegels visualisatie
    private UIManager uiManager;                    // View: Setups en veranderen van naam en coins
    private ActionPanel actionPanel;                // View: Setups al de tabs en nieuwe buttons
    private GameView gameView;                      // View: Setups van de fase 2 interface
    private MinionButtonFactory buttonFactory;      // View: aanmaken van de minions buttons
    private HexagonFactory hexagonFactory;          // View: aanmaken van de tiles ( hex en overlay)
    private ButtonManager buttonManager;            // Controller: zet de buttons available and enabled
    private ActionController actionController;      // Controller: Actie-afhandeling: attack, move, power use
    private ButtonHandler buttonHandler;            // Controller: Button management en de interacties met de model
    private UIController uiController;              // Controller: View-helper, verandering van de info-panel en aantal processed minions


    private final List<Button> minionButtons = new ArrayList<>();
    private final List<Polygon> hexList = new ArrayList<>();
    private VBox labelBox;


    @FXML private SplitPane splitPane;
    @FXML private Label naamLabel;
    @FXML private Label coinsLabel;
    @FXML private ImageView coinImageView;
    @FXML private VBox minionsContainer;
    @FXML private HBox coinsHBox;
    @FXML private Label minionCountLabel;
    @FXML private Button beurtButton;
    @FXML private AnchorPane gameBoardContainer;
    @FXML private ScrollPane gameScrollPane;


    // ==================== Regio: Initialisatie ====================

    public void setInfo(String speler1, String speler2, int munten) {
        FieldParser fieldParser = new FieldParser();
        List<Tile> tiles = fieldParser.parseField();
        this.gameState = new GameState(speler1, speler2, munten, tiles);
        initializeGameComponents();
        updateUI();
        createMinionButtons();
        createHexagons(tiles);
        tileManager.markHomebases();
    }

    private void initializeGameComponents() {
        this.gameLogic = new GameLogic(gameState);
        this.buttonFactory = new MinionButtonFactory(gameState, minionButtons);
        this.uiManager = new UIManager(gameState, naamLabel, coinsLabel);
        this.tileManager = new TileManager(gameState, gameLogic, hexList);
        this.gameView = new GameView(gameState);
        this.uiController = new UIController(gameState, gameView, tileManager, splitPane, coinImageView);
        this.actions = gameState.getGameActions();
        this.actionController = new ActionController(gameState, tileManager);
        this.buttonHandler = new ButtonHandler(gameState, tileManager, actionController);
        this.actionPanel = new ActionPanel(
                gameState,
                this::handleStayAction,
                this::handleBasicAttack,
                this::handleHeal,
                this::onSpecialAttackAction,
                this::handlePowerSelect,
                this::handleTabChange

        );
    }

    @FXML
    public void initialize() {
        if (!hexList.isEmpty()) return;
        hexagonFactory = new HexagonFactory();
        setupKeyListener();
    }

    private void setupKeyListener() {
        gameBoardContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
            }
        });
    }

    // ==================== Einde Regio: Initialisatie ====================

    //====================  Regio: Game board aanmaking  ==================

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
        tiles.forEach(this::createHexagonForTile);
    }

    private void createHexagonForTile(Tile tile) {
        HexagonData hexData = hexagonFactory.createHexagon(tile);
        hexList.add(hexData.getHex());
        gameBoardContainer.getChildren().addAll(hexData.getHex(), hexData.getOverlay());
        hexData.getHex().setUserData(hexData);
        setupHexagonInteractions(hexData);
    }

    private void setupHexagonInteractions(HexagonData hexData) {
        Polygon overlay = hexData.getOverlay();
        overlay.setOnMouseClicked(e -> handleTileClick(hexData.getHex()));
        overlay.setOnMouseEntered(e -> handleHoverStart(hexData.getTile()));
        overlay.setOnMouseExited(e -> handleHoverEnd());
    }
    // ==================== Einde Regio: Game board aanmaking  ====================

    // ==================== Regio: Tile interaction logic  ====================

    private void handleTileClick(Polygon hex) {
        HexagonData data = (HexagonData) hex.getUserData();
        Tile tile = data.getTile();
        Polygon overlayHex = data.getOverlay();
        Minion clickedMinion = gameState.getPlacedMinion(tile);

        if (!gameState.isPlacementPhase()) {
            tileManager.resetAllOverlays();
            if (clickedMinion != null) {
                uiController.handleMinionInfo(tile, clickedMinion, labelBox); // info over minion
            }
        }
        // ==================== Bonus  ====================
        if (actionController.canUseBonus()) {
            handleBonus();
            return;
        }

        if (actions.getMinionProcessed() >= gameState.getTotalMinions()) {
            tileManager.resetAllOverlays(); // extra restrictie om de game te stoppen totdat de speler niet de beurtbutton opklikt
            return;
        }

        if (clickedMinion != null && gameState.getProcessedMinions().contains(clickedMinion))
            return; // minions die al behandeld zijn mogen niet meer actions doen

        if (gameState.getCurrentMinion() == null) {
            gameState.setCurrentMinion(clickedMinion);
        } else if (gameState.getCurrentMinion() != clickedMinion) return; // restrictie om de current monion te hebben

        if (actions.hasMoved() && actions.hasAttacked()) {
            actionController.resetNewAction(); // new minion kiezen om te behandelen
        }
        if (!gameState.isPlacementPhase()) {

            // ==================== Bewegen  ====================

            if (actionController.canMove(tile)) {
                actionController.handleMove(tile);
                updateAfterAction();
                selectMinion(tile, overlayHex);
            }
            // ==================== Attack  ====================

            else if (actionController.canAttack(tile)) {
                actionController.handleAttack(tile);
                updateAfterAction();
                return; // als attacked gaat de controle niet verder
            }
        }

        actions.setBasisAttacked(false); // als wel gaat, de buttons die geklikt waren tijdens aanval fase gaan resetten
        actions.setSpecialAttack(false);

        if (!actions.hasAttacked() && !actions.hasMoved()) { // kijken of de tile of de minion is geselecteerd
            gameState.setCurrentMinion(null);
            if (!gameState.isOccupied(tile) && !gameState.isPlacementPhase()) {
                if (gameState.getCurrentTile() == null) { // toggle van de tile
                    gameState.setCurrentTile(tile);
                    uiController.handleTileInfo(tile, overlayHex, labelBox);
                } else if (gameState.getCurrentTile() == tile) {
                    tileManager.resetAllOverlays();
                    labelBox.getChildren().clear();
                    gameState.setCurrentTile(null);
                } else {
                    tileManager.resetAllOverlays();
                    uiController.handleTileInfo(tile, overlayHex, labelBox);
                    gameState.setCurrentTile(tile);
                }
                return; // als de tile was geselecteerd de controle eindigd hier
            }
        } else {
            updateActionButtonsState();
            return;  // als heeft al bewogen of aanval gedaan moet de speler eerst volledig vorige minion afhandelen
        }

        // Reset previous selection
        gameState.setCurrentTile(null);
        if (gameState.getCurrentlySelectedHex() != null) {
            HexagonData prevData = (HexagonData) gameState.getCurrentlySelectedHex().getUserData();
            prevData.getOverlay().setFill(Color.TRANSPARENT);
            prevData.getOverlay().setOpacity(0);
        }

        if (gameState.getCurrentlySelectedHex() == hex) { // toggle van de hex
            gameState.setCurrentlySelectedHex(null);
            gameState.setSelectedTile(null);
            if (!gameState.isPlacementPhase()) {
                tileManager.resetAllOverlays();
                labelBox.getChildren().clear();
            }
            return;
        }
        if (gameState.getSelectedMinion() != null) {
            if (gameState.isValidPlacement(tile)) { // mogelijk om de minion te plaatsen
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
    // ==================== Einde Regio: Tile interaction logic  ================

    // ==================== Regio: Event handlers  ====================
    @FXML
    private void handleBeurtButton() {
        buttonHandler.handleBeurtButton(hexList);
        gameView.positionViewportForPlayer(gameScrollPane);
        updateUI();
        if (!gameState.isPlacementPhase()) {
            beurtButton.setDisable(true);
            updateMinionCountLabel();
            checkTurnCompletion();
            actionPanel.updatePowerButtonsStyle();
            checkVoorSpelEinde();
        }

    }

    private void handleHoverStart(Tile hoveredTile) {
        if (gameState.getSelectedPower() != null && !gameState.getPowerBoolean() && gameState.getPowerUsed() < 2) {
            tileManager.updatePowerRangeVisuals(hoveredTile);
        }
    }

    private void handleHoverEnd() {
        if (gameState.getSelectedPower() != null) {
            tileManager.resetAllOverlays();
        }
    }


    private void updateUI() {
        uiManager.updateUI();

        if (!gameState.isPlacementPhase()) {
            afterPlacement();
            beurtButton.setDisable(actions.getMinionProcessed() < gameState.getTotalMinions());
            updateActionButtonsState();

        } else {
            buttonHandler.updateButtonStates(minionButtons);
            tileManager.markHomebases();
        }
        tileManager.updateMinionVisibility();

    }


    private void handleBonus() {
        buttonHandler.handleBonus();
        actionPanel.updatePowerButtonsStyle();
        checkVoorSpelEinde();
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
        if (event.getCode() == KeyCode.DELETE && gameState.isPlacementPhase()) { // gaat enkel in de placement fase
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

    private void afterPlacement() { // na de placement fase gaat de minions button weg voor de main tabs en de rest van de UI weergave
        removeMinionButtons();
        addCustomVBox();
        minionCountLabel = gameView.makeMinionCountLabel();
        gameView.replaceCoinsDisplay(minionCountLabel, coinsHBox);
        Button rustButton = gameView.makeRustButton();
        rustButton.setOnAction(e -> handleRustButton());
        uiController.setupActionButtons(beurtButton, rustButton);
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
        minionsContainer.getChildren().addFirst(topVBox);
    }

    private void onSpecialAttackAction() {
        buttonHandler.handleSpecialAttack();
        highlightAttackRange();
    }

    private void handleStayAction() {
        buttonHandler.handleOnStayButton();
        updateMinionCountLabel();
        checkTurnCompletion();
        updateActionButtonsState();

    }

    private void handleBasicAttack() {
        buttonHandler.handleBasicAttack();
        highlightAttackRange();
    }

    private void handleHeal() {
        buttonHandler.handleHeal();
        updateMinionCountLabel();
        checkTurnCompletion();
        updateActionButtonsState();

    }

    private void handlePowerSelect(Power power) {
        buttonHandler.handlePowerSelect(power);
        actionPanel.updatePowerButtonsStyle();
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
                    actionController.handleAddMinion(minion);
                    checkTurnCompletion();
                    updateMinionCountLabel();
                }
            }
        }
        updateActionButtonsState();
    }
    private void handleRustButton() {
        buttonHandler.handleRustButton();
        updateMinionCountLabel();
        checkTurnCompletion();
    }

    private void highlightReachableTiles() {
        tileManager.highlightTiles(actions.getReachableTiles(), Color.GREEN);
    }

    private void highlightAttackRange() {
        tileManager.highlightTiles(actions.getAttackableTiles(), Color.RED);
    }


    private void updateMinionCountLabel() {
        if (actions.getMinionProcessed() <= gameState.getTotalMinions()) {
            uiController.updateMinionCountLabel(minionCountLabel, actions.getMinionProcessed(), gameState.getTotalMinions());
        }
        updateActionButtonsState();
    }
    private void checkTurnCompletion() {
       actionController.checkTurn(beurtButton);
    }
    private void updateActionButtonsState() {
        buttonManager.updateButtons();
    }
    // ==================== Einde Regio: Event handlers  ====================

    //  State management
    private void updateAfterAction() {
        updateMinionCountLabel();
        checkTurnCompletion();
        updateActionButtonsState();
        checkVoorSpelEinde();
    }

    // ==================== Regio: Eind scherm   ====================

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
    // ==================== Eind Regio: Eind scherm   ====================
}