package Controllers;
import be.ugent.objprog.minionwars.MinionWars;
import javafx.application.Platform;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.GameLogic;
import models.GameState;
import models.effects.Effect;
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
import view.images.ImageLoader;
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
    private String currentTab = "Bewegen";
    private Set<Tile> attackableTiles = new HashSet<>();
    private Set<Tile> reachableTiles = new HashSet<>();
    private Set<Tile> powerTiles = new HashSet<>();
    private final List<Button> minionButtons = new ArrayList<>();
    private final List<Polygon> hexList = new ArrayList<>();
    private Tile currentlySelectedTile = null;
    private boolean hasMoved = false;
    private boolean hasAttacked = false;
    private Minion currentMinion = null;
    private int minionsProcessedThisTurn = 0;
    private VBox labelBox;
    private boolean basisAttacked = false;
    private boolean specialAttack = false;
    int totalProcessed = 2;


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
    private ActionPanel actionPanel;
    private Button rustButton;

    @FXML
    public void initialize() {
        if (!hexList.isEmpty()) return;
        hexagonFactory = new HexagonFactory();
    }

    public void setInfo(String speler1, String speler2, int munten) {
        FieldParser fieldParser = new FieldParser();
        List<Tile> tiles = fieldParser.parseField();

        this.gameState = new GameState(speler1, speler2, munten, tiles);
        this.gameLogic = new GameLogic(gameState);
        this.buttonFactory = new MinionButtonFactory(gameState, minionButtons);
        this.uiManager = new UIManager(gameState, naamLabel, coinsLabel);
        this.tileManager = new TileManager(gameState, hexList);
        this.gameView  = new GameView(gameState,tileManager);
        this.infoPanel = new InfoPanel(gameState);

        this.actionPanel = new ActionPanel(
                gameState,
                this::handleStayAction,
                this::handleBasicAttack,
                this::handleHeal,
                this::onSpecialAttackAction,
                this::handlePowerSelect,
                this::handleTabChange

        );



        setupKeyListener();

        gameView.initializeUI(splitPane,coinImageView);
        updateUI();
        createMinionButtons();
        createHexagons(tiles);
        tileManager.markHomebases();
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

    @FXML
    private void handleBeurtButton() {
        gameState.switchPlayer();
        gameState.resetBeurtButton();
        currentlySelectedTile = null;
        gameView.positionViewportForPlayer(gameScrollPane);
        updateUI();
        gameState.resetProcessedMinions();
        tileManager.resetAllOverlays();
        tileManager.markHomebases();
        currentTab = "Bewegen";

        if (!gameState.isPlacementPhase()){
            beurtButton.setDisable(true);
            totalProcessed = gameState.remainOne()? 1 : 2;
            minionsProcessedThisTurn = 0;
            hasMoved = false;
            hasAttacked = false;
            currentMinion = null;
            basisAttacked = false;
            specialAttack = false;
            updateMinionCountLabel();
            processEffects();
            actionPanel.updatePowerButtonsStyle();



        }

    }

    private void updateUI() {
        uiManager.updateUI();

        if (!gameState.isPlacementPhase()) {
            afterPlacement();
            beurtButton.setDisable(minionsProcessedThisTurn < totalProcessed);
            updateActionButtonsState();

        }else{
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

        if (minionsProcessedThisTurn >= totalProcessed) {
            tileManager.resetAllOverlays();
            return;
        }

        if (clickedMinion != null && gameState.getProcessedMinions().contains(clickedMinion)) return;

        if (currentMinion == null) {
            currentMinion = clickedMinion;
        }else if (currentMinion != clickedMinion)return;

        if (hasMoved && hasAttacked){
            hasMoved = false;
            hasAttacked = false;
            currentMinion = null;
            gameState.setSelectedTile(null);
        }
        if (!gameState.isPlacementPhase()) {

            //Bewegen
            if ("Bewegen".equals(currentTab) && gameState.getSelectedTile() != null && reachableTiles.contains(tile) && !hasMoved) {
                moveMinion(tile);
                selectMinion(tile, overlayHex);
                return;
            }
            // Handle attack
            else if ("Aanvallen".equals(currentTab)
                    && gameState.getSelectedTile() != null
                    && attackableTiles.contains(tile)
                    && gameState.isOccupied(tile)
                    && !gameState.isMinionOwnedByCurrentPlayer(tile)
                    && !hasAttacked
                    && (basisAttacked || specialAttack)) {

                Minion attacker = gameState.getPlacedMinion(gameState.getSelectedTile());
                Minion defender = gameState.getPlacedMinion(tile);

                if (attacker != null && defender != null) {
                    defender.verminderCurrentDefence(attacker.getAttack());

                    if (specialAttack && attacker.hasSpecialAbility()) {

                        String effectName = attacker.getEffect();
                        Effect effect = gameState.findEffectByName(effectName);
                        attacker.setSpecialAttackUsed(true);
                        attacker.setRestCount(0);
                        // effect gevonden
                        if (effect != null) {
                            // de kopie
                            Effect appliedEffect = new Effect(
                                    effect.getType(),
                                    effect.getName(),
                                    effect.getDuration(),
                                    attacker.getEffectValue() != 0 ? attacker.getEffectValue() : effect.getValue()
                            );
                            if ("rage".equals(effect.getType()) || "heal".equals(effect.getType())) {
                                attacker.addEffect(appliedEffect);
                            }else {
                                defender.addEffect(appliedEffect);
                            }


                        }
                    }

                    if (defender.getCurrentDefence() <= 0) {
                        gameState.removeMinion(tile);
                        tileManager.resetTileVisual(tile);
                    }

                    if (hasMoved) {
                        gameState.addProcessedMinion(attacker);
                        minionsProcessedThisTurn++;
                    }

                    hasAttacked = true;
                    currentMinion = null;

                    updateMinionCountLabel();
                    checkTurnCompletion();
                    checkVoorSpelEinde();
                    tileManager.resetAllOverlays();
                }
                return;
            }
        }
        basisAttacked = false;
        specialAttack = false;

        if (!hasAttacked && !hasMoved){
            currentMinion = null;
            if (!gameState.isOccupied(tile) && !gameState.isPlacementPhase()) {
                if (currentlySelectedTile == null ) {
                    currentlySelectedTile = tile;
                    selectTile(tile, overlayHex);
                }else if (currentlySelectedTile == tile){
                    tileManager.resetAllOverlays();
                    labelBox.getChildren().clear();
                    currentlySelectedTile = null;
                }else{
                    tileManager.resetAllOverlays();
                    selectTile(tile, overlayHex);
                    currentlySelectedTile = tile;
                }
                return;
            }
        } else {
            updateActionButtonsState();
            return;
        }

        // Reset previous selection
        currentlySelectedTile = null;
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
    private void handleBonus() {
        applyFireball();
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
            labelBox.getChildren().add(0, nieuweHBox);}
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
       if (!gameState.isPlacementPhase()){
           tileManager.resetAllOverlays();
       }

        if (gameState.isMinionOwnedByCurrentPlayer(tile)) {
            gameState.setSelectedTile(tile);
            tileManager.markSelected(overlayHex);
            if (!gameState.isPlacementPhase()) {
                Minion minion = gameState.getPlacedMinion(tile);

                if ("Bewegen".equals(currentTab) && !hasMoved) {
                    reachableTiles = gameLogic.calculateMovementRange(tile, minion.getCurrentMovement());
                    highlightReachableTiles();
                }else if("Aanvallen".equals(currentTab) && !hasAttacked) {
                    attackableTiles = gameLogic.calculateAttackRange(tile, minion.getMinRange(), minion.getCurrentMaxRange());
                    highlightAttackRange();
                }
                updateActionButtonsState();
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
    private void afterPlacement(){
        removeMinionButtons();
        addCustomVBox();
        minionCountLabel = gameView.makeMinionCountLabel();
        gameView.replaceCoinsDisplay(minionCountLabel, coinsHBox);
        rustButton = gameView.makeRustButton();
        rustButton.setOnAction(e -> handleRustButton());
        gameView.setupActionButtons(beurtButton,rustButton);
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
        basisAttacked = false;
        specialAttack = true;
        highlightAttackRange();
    }

    private void handleStayAction() {
        gameState.setSelectedPower(null);
        if (gameState.getSelectedTile() != null) {
            Minion minion = gameState.getPlacedMinion(gameState.getSelectedTile());
            if (hasAttacked) {
                gameState.addProcessedMinion(minion);
                minionsProcessedThisTurn++;
                currentMinion = null;
            }
            hasMoved = true;
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
        basisAttacked = true;
        specialAttack = false;
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

                if (hasMoved) {
                    gameState.addProcessedMinion(minion);
                    minionsProcessedThisTurn++;
                    currentMinion = null;
                }
                hasAttacked = true;
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
        powerTiles.clear();
    }

    private void handleTabChange(String tabTitle) {
        currentTab = tabTitle;
        gameState.setSelectedPower(null);
        if (gameState.getSelectedTile() != null) {
            Tile tile = gameState.getSelectedTile();
            Minion minion = gameState.getPlacedMinion(tile);
            tileManager.resetAllOverlays();
            if (minion != null) {
                if ("Bewegen".equals(currentTab) && !hasMoved) {
                    reachableTiles = gameLogic.calculateMovementRange(tile, minion.getCurrentMovement());
                    highlightReachableTiles();

                }else if("Aanvallen".equals(currentTab) && !hasAttacked) {
                    attackableTiles = gameLogic.calculateAttackRange(tile, minion.getMinRange(), minion.getCurrentMaxRange());
                    highlightAttackRange();
                }
            }
            updateActionButtonsState();
        }
    }

    private void handleRustButton() {
        Tile tile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(tile);

        if (minion != null && !gameState.getProcessedMinions().contains(minion)) {
            hasAttacked = true;
            if (hasMoved) {
                minionsProcessedThisTurn++;
                gameState.addProcessedMinion(minion);
            }
            if (minion.isSpecialAttackUsed()){
                // Als 2 keer gerust, reset speciale aanval
                minion.setRestCount(minion.getRestCount() + 1);
                if (minion.getRestCount() >= 2) {
                    minion.setSpecialAttackUsed(false);
                    minion.setRestCount(0);
                }
            }
        }
        if ("Aanvallen".equals(currentTab)) {
            tileManager.resetAllOverlays();
        }
        updateMinionCountLabel();
        checkTurnCompletion();
    }

    private void updatePowerRangeVisuals(Tile centerTile) {
        powerTiles.clear();
        String powerType = gameState.getSelectedPower().getType().toLowerCase();
        powerTiles = gameLogic.calculateBonusRange(centerTile, gameState.getSelectedPower().getRadius());
            boolean hasValidTarget = switch (powerType) {
                case "healing" -> gameLogic.hasFriendlyInRange(powerTiles);
                case "fireball", "lightning" -> gameLogic.hasEnemyInAttackRange(powerTiles);
                default -> false;
            };

            if (hasValidTarget) {
                tileManager.highlightTiles(powerTiles, Color.BLUE);
            } else {
                tileManager.highlightTiles(powerTiles, Color.GREEN);
            }
    }

    private void moveMinion(Tile targetTile) {
        Tile originalTile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(originalTile);
        gameState.removeMinion(originalTile);
        gameState.placeMinion(targetTile, minion);
        // Update visuals
        tileManager.resetTileVisual(originalTile);
        tileManager.updateMinionVisual(targetTile, minion);

        if(hasAttacked){
            gameState.addProcessedMinion(minion);
            minionsProcessedThisTurn++;
            currentMinion = null;
        }
        hasMoved = true;
        // Reset selection
        reachableTiles.clear();
        tileManager.resetAllOverlays();
        updateActionButtonsState();
        updateMinionCountLabel();
        checkTurnCompletion();

    }
    private void highlightReachableTiles() {
        tileManager.highlightTiles(reachableTiles, Color.GREEN);
    }

    private void highlightAttackRange() {
        tileManager.highlightTiles(attackableTiles, Color.RED);
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
        if (minionsProcessedThisTurn<=2){
            minionCountLabel.setText(minionsProcessedThisTurn + "/" + totalProcessed);
        }
        updateActionButtonsState();
    }

    private void checkTurnCompletion() {
        if (minionsProcessedThisTurn >= totalProcessed) {
            beurtButton.setDisable(false);
            tileManager.resetAllOverlays();
            gameState.setSelectedTile(null);
            gameState.setCurrentlySelectedHex(null);
            currentMinion = null;
        }
    }


    private void applyFireball() {
        for (Tile tile : powerTiles) {
            boolean isOwnMinion = gameState.isMinionOwnedByCurrentPlayer(tile);

            Power power = gameState.getSelectedPower();
            if (gameState.isOccupied(tile)) {
                Minion minion = gameState.getPlacedMinion(tile);
                if (!isOwnMinion && !power.getType().equals("healing")){
                    minion.verminderCurrentDefence(power.getValue());  //de schade doen

                } else if (isOwnMinion && power.getType().equals("healing")){   //heal van eigen minion
                    minion.setCurrentDefence(Math.min(
                            minion.getCurrentDefence() + power.getValue(),
                            minion.getDefence()
                    ));
                }
                if (power.hasEffect()) {
                    Effect effect = gameState.findEffectByName(power.getEffect());
                    Effect appliedEffect = new Effect(
                            effect.getType(),
                            effect.getName(),
                            effect.getDuration(),
                            power.getEffectValue() != 0 ? power.getEffectValue() : effect.getValue()
                    );
                    minion.addEffect(appliedEffect);
                }
                if (minion.getCurrentDefence() <= 0) {
                    gameState.removeMinion(tile);
                    tileManager.resetTileVisual(tile);
                    checkVoorSpelEinde();
                }
            }
        }

    }

    private void updateActionButtonsState() {
        if (gameState.isPlacementPhase()) return;

        Tile tile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(tile);
        boolean isParalized = minion != null && minion.isParalized();

        boolean enemiesInRange = !isParalized && gameLogic.hasEnemyInAttackRange(attackableTiles);
        boolean canHeal = !isParalized && (minion == null || minion.getHealCount() < 2);

        boolean canUseSpecial = minion != null &&
                !minion.isSpecialAttackUsed() &&
                !hasAttacked;

        actionPanel.setBasicAndSpecialAttackDisabled(isParalized || hasAttacked || !enemiesInRange);
        actionPanel.setSpecialAttackVisible(canUseSpecial && minion.hasSpecialAbility() && gameLogic.hasEnemyInAttackRange(attackableTiles));
        actionPanel.setHealDisabled(isParalized || hasAttacked || !canHeal);
        actionPanel.setStayButtonDisabled(isParalized || hasMoved);
        rustButton.setDisable(isParalized || hasAttacked);

    }

    private void processEffects() {
        for (Polygon hex :hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();
            if (gameState.isOccupied(tile)) {
                Minion minion = gameState.getPlacedMinion(tile);
                List<Effect> effectsToProcess = new ArrayList<>(minion.getActiveEffects());
                if (effectsToProcess.isEmpty()) {
                    continue;
                }
                for (Effect effect : effectsToProcess) {
                    effect.verminderDuration();
                    minion.applyEffect(effect);

                    if (effect.getDuration() <= 0) {
                        minion.removeEffect(effect);
                    }
                    if (minion.getCurrentDefence() <= 0) {
                        gameState.removeMinion(tile);
                        tileManager.resetTileVisual(tile);
                        checkVoorSpelEinde();
                    }
                }
            }
        }
    }
}