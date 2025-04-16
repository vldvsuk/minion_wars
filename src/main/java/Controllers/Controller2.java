package Controllers;
import be.ugent.objprog.minionwars.MinionWars;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.GameState;
import models.minions.Minion;
import models.parsers.FieldParser;
import models.parsers.MinionParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.grond.Tile;
import models.parsers.PowerParser;
import models.powers.Power;
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
    private String currentTab = "Bewegen";
    private final Set<Tile> attackableTiles = new HashSet<>();
    private final Set<Tile> reachableTiles = new HashSet<>();
    private final List<Button> minionButtons = new ArrayList<>();
    private final List<Polygon> hexList = new ArrayList<>();
    private Polygon currentlySelectedHex = null;
    private boolean hasMoved = false;
    public int acties = 0;

    private int minionsProcessedThisTurn = 0;
    private final Set<Minion> processedMinions = new HashSet<>();
    private VBox labelBox;
    private VBox tabBox;


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
        currentTab = "Bewegen";
        if (!gameState.isPlacementPhase()){
            beurtButton.setDisable(true);
            minionsProcessedThisTurn = 0;
            acties = 0;
            updateMinionCountLabel();
            processedMinions.clear();
            hasMoved = false;

        }

    }

    private void resetAllOverlays() {
        reachableTiles.clear();
        attackableTiles.clear();
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            data.getOverlay().setFill(Color.TRANSPARENT);
            data.getOverlay().setOpacity(0);
            data.getOverlay().setStroke(Color.BLACK);
            data.getOverlay().setStrokeWidth(1.5);
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


        if (!gameState.isPlacementPhase()) {
            naPlacemet();
            beurtButton.setDisable(minionsProcessedThisTurn < 2);
        }else{
            updateButtonStates();
            markHomebases();
        }

        updateMinionVisibility();

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

        if (minionsProcessedThisTurn >= 2) return;

        HexagonData data = (HexagonData) hex.getUserData();
        Tile tile = data.getTile();
        Polygon overlayHex = data.getOverlay();


        Minion minion = gameState.getPlacedMinion(tile);
        if (minion != null && processedMinions.contains(minion)) {
            return;
        }
        if (acties >= 2){
            acties=0;
            hasMoved = false;
        }

        if (!gameState.isPlacementPhase()) {
            // Handle movement
            if ("Bewegen".equals(currentTab)
                    && gameState.getSelectedTile() != null
                    && reachableTiles.contains(tile)) {
                if (!hasMoved){
                    moveMinion(tile);
                }

                return;
            }
            // Handle attack
            else if ("Aanvallen".equals(currentTab)
                    && gameState.getSelectedTile() != null
                    && attackableTiles.contains(tile)
                    && gameState.isOccupied(tile)
                    && !gameState.isMinionOwnedByCurrentPlayer(tile)) {


                Minion attacker = gameState.getPlacedMinion(gameState.getSelectedTile());
                Minion defender = gameState.getPlacedMinion(tile);

                if (attacker != null && defender != null) {
                    defender.verminderCurrentDefence(attacker.getAttack());
                    if (defender.getCurrentDefence() <= 0) {
                        gameState.removeMinion(tile);
                        resetTileVisual(tile);
                    }
                    if(acties == 1){
                        processedMinions.add(attacker);
                        minionsProcessedThisTurn++;
                    }
                    acties+=1;
                    updateMinionCountLabel();
                    checkTurnCompletion();
                    checkVoorSpelEinde();


                    resetAllOverlays();
                    gameState.setSelectedTile(null);
                    currentlySelectedHex = null;
                }
                return;
            }
        }

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
        Minion original = gameState.getSelectedMinion();
        Minion kopie = original.copy();

        gameState.deductCoins(kopie.getCost());
        try {
            hex.setFill(ImagePatternHelper.createMinionPattern(kopie.getType()));
        } catch (Exception e) {
            hex.setFill(Color.RED);
        }

        gameState.placeMinion(tile, kopie);
        currentlySelectedHex = null;
        updateUI();
    }

    private void selectMinion(Tile tile, Polygon overlayHex) {
        if (gameState.isMinionOwnedByCurrentPlayer(tile)) {
            gameState.setSelectedTile(tile);


            overlayHex.setFill(Color.rgb(0, 255, 0, 0.2));
            overlayHex.setOpacity(0.7);
            overlayHex.setStroke(Color.GREEN);

            if (!gameState.isPlacementPhase()) {
                labelBox.getChildren().clear();
                HBox nieuweHBox = generateMinionInfo(tile);
                labelBox.getChildren().add(0, nieuweHBox);

                Minion minion = gameState.getPlacedMinion(tile);


                if ("Aanvallen".equals(currentTab)) {
                    calculateAttackRange(tile, minion.getRange());
                    highlightAttackRange();
                } else {
                    calculateMovementRange(tile, minion.getMovement());
                    highlightReachableTiles();
                }
            }
        }
    }


    private void markHomebases() {

        if (!gameState.isPlacementPhase()) return;

        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();
            Polygon overlayHex = data.getOverlay();

            if (hex == currentlySelectedHex) continue;
            // Reset overlay
            overlayHex.setFill(Color.TRANSPARENT);
            overlayHex.setOpacity(0);
            overlayHex.setStroke(Color.BLACK);
            overlayHex.setStrokeWidth(1.5);


            if (gameState.isPlacementPhase()) {
                if (gameState.isOccupied(tile)) {
                    if (!gameState.isMinionOwnedByCurrentPlayer(tile)) {
                        overlayHex.setFill(Color.RED);
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
                    // Show all minions na placement
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

    private void naPlacemet(){
        removeMinionButtons();
        addCustomVBox();
        replaceCoinsDisplay();
        setupActionButtons();
    }

    private void removeMinionButtons() {
        minionsContainer.getChildren();
        minionButtons.clear();
        gameState.setSelectedMinion(null);
    }

    private void addCustomVBox() {
        minionsContainer.getChildren().clear();

        VBox topVBox = new VBox(10);
        topVBox.setId("topVBox");
        topVBox.setPrefWidth(180);
        topVBox.setAlignment(Pos.TOP_CENTER);


        // LABEL BOX
        labelBox = new VBox();
        labelBox.setAlignment(Pos.CENTER);
        Label label = new Label("Kies een minion!");
        label.setFont(Font.font("System", FontWeight.BOLD, 24));
        labelBox.setMinHeight(100);
        labelBox.getChildren().add(label);

        tabBox = new VBox();
        tabBox.setAlignment(Pos.BOTTOM_CENTER);
        tabBox.setPrefHeight(300);
        tabBox.setPrefWidth(180);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMinHeight(600);
        tabPane.setPrefWidth(180);


        // Tab 1: Bewegen
        Tab bewegenTab = new Tab("Bewegen");
        VBox bewegenContent = new VBox(15);
        bewegenContent.setAlignment(Pos.CENTER);
        bewegenContent.setPrefWidth(170);

        Label bewegenLabel = new Label("Selecteer een groen veld op het spelbord of kies om te blijven staan");
        bewegenLabel.setTextAlignment(TextAlignment.CENTER);
        bewegenLabel.setFont(Font.font("System", FontWeight.BOLD, 17));
        bewegenLabel.setWrapText(true);
        bewegenLabel.setMaxWidth(160);

        Button blijvenStaanButton = new Button("Blijven staan");
        blijvenStaanButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        blijvenStaanButton.setOnAction(e -> {
            if (gameState.getSelectedTile() != null) {
                Minion minion = gameState.getPlacedMinion(gameState.getSelectedTile());
                if(acties == 1){
                    processedMinions.add(minion);
                    minionsProcessedThisTurn++;
                }
                acties+=1;
                hasMoved = true;
                updateMinionCountLabel();
                resetAllOverlays();
                gameState.setSelectedTile(null);
                currentlySelectedHex = null;
                checkTurnCompletion();
            }
        });

        bewegenContent.getChildren().addAll(bewegenLabel, blijvenStaanButton);
        bewegenTab.setContent(bewegenContent);

        // Tab 2: Aanvallen
        Tab aanvallenTab = new Tab("Aanvallen");
        VBox aanvallenContent = new VBox(10);
        aanvallenContent.setAlignment(Pos.CENTER);
        aanvallenContent.setPrefWidth(170);

        Label aanvallenLabel = new Label("Selecteer een aanval uit de lijst en klik op een vijandelijke minion");
        aanvallenLabel.setTextAlignment(TextAlignment.CENTER);
        aanvallenLabel.setFont(Font.font("System", FontWeight.BOLD, 17));
        aanvallenLabel.setWrapText(true);
        aanvallenLabel.setMaxWidth(160);

        Button basisAanvalButton = new Button("Basis aanval");
        basisAanvalButton.setFont(Font.font("System", FontWeight.BOLD, 18));

        Button specialeAanvalButton = new Button("Speciale aanval");
        specialeAanvalButton.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label ofLabel = new Label("of");
        ofLabel.setFont(Font.font("System", FontWeight.BOLD, 17));

        Button geneesButton = new Button("Genees minion (+2hp)");
        geneesButton.setFont(Font.font("System", FontWeight.BOLD, 18));

        geneesButton.setOnAction(e -> {
            if (gameState.getSelectedTile() != null) {
                Minion minion = gameState.getPlacedMinion(gameState.getSelectedTile());
                if (minion != null) {
                    int newDefence = Math.min(
                            minion.getCurrentDefence() + 2,
                            minion.getDefence()
                    );

                    minion.setCurrentDefence(newDefence);
                    if(acties == 1){
                        processedMinions.add(minion);
                        minionsProcessedThisTurn++;
                    }
                    acties+=1;
                    updateMinionCountLabel();

                    resetAllOverlays();
                    gameState.setSelectedTile(null);
                    currentlySelectedHex = null;
                    checkTurnCompletion();
                }
            }
        });

        aanvallenContent.getChildren().addAll(
                aanvallenLabel,
                basisAanvalButton,
                specialeAanvalButton,
                ofLabel,
                geneesButton
        );
        aanvallenTab.setContent(new StackPane(aanvallenContent));

        // Tab 3: Bonus
        Tab bonusTab = new Tab("Bonus");
        VBox bonusContent = new VBox(10);
        bonusContent.setPrefWidth(170);

// Parse powers en maak buttons
        PowerParser powerParser = new PowerParser();
        List<Power> powers = powerParser.parsePowers();

        for (Power power : powers) {
            Button powerButton = new Button();
            powerButton.getStyleClass().add("minion-button");
            powerButton.setMaxWidth(Double.MAX_VALUE);
            powerButton.setMinHeight(100);
            powerButton.setPrefHeight(75);
            powerButton.setUserData(power);


            // Main content HBox
            HBox content = new HBox(15);
            content.setAlignment(Pos.CENTER_LEFT);

            // 1. Power afbeelding (eerste element van hoofd-HBox)
            ImageView powerImage = new ImageView();
            try {
                powerImage.setImage(ImageLoader.loadPowerImage(power.getType()));
            } catch (Exception e) {
                powerImage.setImage(ImageLoader.loadFallbackMinionImage());
            }
            powerImage.setFitHeight(100);
            powerImage.setFitWidth(100);
            powerImage.setPreserveRatio(true);
            Circle clip = new Circle(50, 50, 40);
            powerImage.setClip(clip);

            // 2. HBox (detailsContainer) - tweede element van hoofd-HBox
            HBox detailsContainer = new HBox(70);
            detailsContainer.setAlignment(Pos.CENTER_LEFT);

            // 2a. Eerste element van detailsContainer: VBox met naam en effect
            VBox textDetails = new VBox(5);
            textDetails.setAlignment(Pos.CENTER_LEFT);

            // Naam (bold)
            Label nameLabel = new Label(power.getName());

            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 23));
            nameLabel.setPrefWidth(150);
            nameLabel.setAlignment(Pos.CENTER_LEFT);

            // Effect (niet bold)
            Label effectLabel = new Label();
            switch (power.getType().toLowerCase()) {
                case "fireball" -> effectLabel.setText("Effect: branden");
                case "lightning" -> effectLabel.setText("Effect: verlaming");
                case "healing" -> effectLabel.setText("Effect: genezing");
                default -> effectLabel.setText("Effect: ");
            }
            effectLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

            textDetails.getChildren().addAll(nameLabel, effectLabel);

            // Stats container
            VBox statsContainer = new VBox(5);
            statsContainer.setAlignment(Pos.CENTER);

            // Value icon conditioneel instellen
            Image valueIcon = switch (power.getType().toLowerCase()) {
                case "healing" -> ImageLoader.loadHealthIcon();
                default -> ImageLoader.loadAttackIcon();
            };

            HBox valueBox = createStatBox(
                    valueIcon,
                    String.valueOf(power.getValue()),
                    "value-stat"
            );

            HBox radiusBox = createStatBox(
                    ImageLoader.loadRangeIcon(),
                    String.valueOf(power.getRadius()),
                    "radius-stat"
            );

            HBox durationBox = createStatBox(
                    ImageLoader.loadDurationIcon(),
                    "1",
                    "duration-stat"
            );

            // Conditionele toevoeging van stats
            statsContainer.getChildren().add(valueBox);
            if (!power.getType().equalsIgnoreCase("lightning")) {
                statsContainer.getChildren().add(radiusBox);
            }
            if (!power.getType().equalsIgnoreCase("healing")) {
                statsContainer.getChildren().add(durationBox);
            }

            detailsContainer.getChildren().addAll(textDetails, statsContainer);
            content.getChildren().addAll(powerImage, detailsContainer);
            powerButton.setGraphic(content);
            powerButton.setOnAction(e -> System.out.println("de button is" + power.getType()));
            bonusContent.getChildren().add(powerButton);
        }

        bonusTab.setContent(bonusContent);
        tabPane.getTabs().addAll(bewegenTab, aanvallenTab, bonusTab);


        setupTabListener(tabPane);

        tabBox.getChildren().add(tabPane);
        topVBox.getChildren().addAll(labelBox, tabBox);
        minionsContainer.getChildren().add(0, topVBox);
    }


    private void replaceCoinsDisplay() {

        ImageView minionIcon = new ImageView(ImageLoader.loadUseMinionIcon());
        minionIcon.setFitHeight(26);
        minionIcon.setFitWidth(24);
        minionIcon.setPreserveRatio(true);

        minionCountLabel = new Label("0/2");
        minionCountLabel.setTextFill(Color.BLUE);
        minionCountLabel.setFont(Font.font("System Bold Italic", 24));

        coinsHBox.getChildren().clear();
        coinsHBox.getChildren().addAll(minionIcon, minionCountLabel);


    }
    private HBox createStatBox(Image icon, String value, String styleClass) {
        HBox box = new HBox(5);
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(20);
        iconView.setFitWidth(20);
        iconView.setPreserveRatio(true);

        Label label = new Label(value);
        label.getStyleClass().add(styleClass);

        box.getChildren().addAll(iconView, label);
        return box;
    }

    private HBox generateMinionInfo(Tile selectedTile) {

        Minion selectedMinion = gameState.getPlacedMinion(selectedTile);
        HBox hbox1 = new HBox(10);

        hbox1.setAlignment(Pos.CENTER_LEFT);


        // Minion afbeelding
        ImageView imageView = new ImageView();
        try {
            Image image = ImageLoader.loadMinionImage(selectedMinion.getType());
            imageView.setImage(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            Circle clip = new Circle(50, 50, 40);
            imageView.setClip(clip);
        } catch (Exception e) {
            imageView.setImage(ImageLoader.loadFallbackMinionImage());
        }

        // VBox voor naam, stats en ondergrond
        VBox contentVBox = new VBox(5);
        contentVBox.setAlignment(Pos.CENTER);

        // HBox voor naam en stats
        HBox nameAndStats = new HBox(20);
        nameAndStats.setAlignment(Pos.CENTER_LEFT);

        HBox stats = new HBox(7);
        stats.setAlignment(Pos.CENTER_LEFT);


        HBox nameBox = new HBox();
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.setPrefWidth(150);


        // Naam label
        Label nameLabel = new Label(selectedMinion.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 23));

        nameBox.getChildren().addAll(nameLabel);

        // Attack en defense stats
        HBox attackBox = createStatBox(
                ImageLoader.loadAttackIcon(),
                String.valueOf(selectedMinion.getAttack()),
                "attack-stat"
        );

        String health = String.valueOf(selectedMinion.getCurrentDefence()) + "/" + String.valueOf(selectedMinion.getDefence());

        HBox defenseBox = createStatBox(
                ImageLoader.loadDefenseIcon(),
                health,
                "defence-stat"
        );

        stats.getChildren().addAll(attackBox, defenseBox);
        nameAndStats.getChildren().addAll(nameBox, stats);

        HBox tilesBox = new HBox(5);
        tilesBox.setAlignment(Pos.CENTER_LEFT);

        Label tileLabel = new Label("Ondergrond: " + selectedTile.getType());
        tileLabel.setFont(Font.font("System",FontWeight.BOLD, 23));
        tilesBox.getChildren().add(tileLabel);


        contentVBox.getChildren().addAll(nameAndStats, tilesBox);
        hbox1.getChildren().addAll(imageView, contentVBox);

        return hbox1;
    }

    private void setupActionButtons() {
        // Verwijder bestaande button uit FXML
        HBox buttonContainer = (HBox) beurtButton.getParent();
        buttonContainer.getChildren().clear();

        // Maak beide buttons met gelijke grootte
        beurtButton.setPrefHeight(35);

        Button rustButton = new Button("Rust");
        rustButton.setPrefWidth(80);
        rustButton.setPrefHeight(35);
        rustButton.setFont(Font.font("System", FontWeight.BOLD, 15));
        rustButton.setOnAction(e -> handleRustButton());

        // Voeg toe aan container
        buttonContainer.getChildren().addAll(rustButton, beurtButton);
        buttonContainer.setSpacing(5);
    }

    private void handleRustButton() {

        minionsProcessedThisTurn+=1;
        acties+=2;



        updateMinionCountLabel();
        checkTurnCompletion();
    }

    private void calculateMovementRange(Tile startTile, int movement) {
        reachableTiles.clear();
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();

            if (isValidMoveTarget(startTile, tile, movement)) {
                reachableTiles.add(tile);
            }
        }
    }

    private boolean isValidMoveTarget(Tile start, Tile target, int maxDistance) {

        int distance = calculateHexDistance(start, target);

        return distance <= maxDistance &&
                !gameState.isOccupied(target) &&
                List.of("dirt", "forest", "mountains").contains(target.getType());
    }


    private void moveMinion(Tile targetTile) {
        Tile originalTile = gameState.getSelectedTile();
        Minion minion = gameState.getPlacedMinion(originalTile);


        gameState.removeMinion(originalTile);
        gameState.placeMinion(targetTile, minion);


        // Update visuals
        resetTileVisual(originalTile);
        updateMinionVisual(targetTile, minion);

        // Reset selection
        reachableTiles.clear();
        hasMoved = true;
        resetAllOverlays();
        updateMinionCountLabel();
        checkTurnCompletion();
        if(acties == 1){
            processedMinions.add(minion);
            minionsProcessedThisTurn++;
        }
        acties+=1;
    }

    private void updateMinionVisual(Tile tile, Minion minion) {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            if (data.getTile().equals(tile)) {
                try {
                    hex.setFill(ImagePatternHelper.createMinionPattern(minion.getType()));
                } catch (Exception e) {
                    hex.setFill(Color.RED);
                }
                break;
            }
        }
    }

    private int[] parseAttackRange(String rangeStr) {
        String[] parts = rangeStr.split(" ");
        int min = Integer.parseInt(parts[0]);
        int max = Integer.parseInt(parts[1]);
        return new int[]{min, max};
    }

    private int calculateHexDistance(Tile start, Tile target) {
        int startX = start.getX();
        int startY = start.getY();
        int targetX = target.getX();
        int targetY = target.getY();

        // axial coordinates
        int q1 = startX - (startY - (startY&1)) / 2;
        int r1 = startY;
        int q2 = targetX - (targetY - (targetY&1)) / 2;
        int r2 = targetY;

        return (Math.abs(q1 - q2)
                + Math.abs(q1 + r1 - q2 - r2)
                + Math.abs(r1 - r2)) / 2;
    }


    private void calculateAttackRange(Tile startTile, String attackRangeStr) {
        attackableTiles.clear();
        try {
            int[] range = parseAttackRange(attackRangeStr);
            int minRange = range[0];
            int maxRange = range[1];

            for (Polygon hex : hexList) {
                HexagonData data = (HexagonData) hex.getUserData();
                Tile tile = data.getTile();
                int distance = calculateHexDistance(startTile, tile);

                if (distance >= minRange
                        && distance <= maxRange
                        && !gameState.isMinionOwnedByCurrentPlayer(tile)) {
                    attackableTiles.add(tile);
                }
            }
        } catch (Exception e) {
            System.err.println("Fout bij berekenen aanvalsbereik: " + e.getMessage());
        }
    }


    private void setupTabListener(TabPane tabPane) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            currentTab = newTab.getText();
            if (!gameState.isPlacementPhase() && gameState.getSelectedTile() != null) {
                Tile tile = gameState.getSelectedTile();
                Minion minion = gameState.getPlacedMinion(tile);
                resetAllOverlays();

                if (minion != null) {
                    if (!"Bewegen".equals(currentTab)) {
                        calculateAttackRange(tile, minion.getRange());
                        highlightAttackRange();
                    } else {
                        calculateMovementRange(tile, minion.getMovement());
                        highlightReachableTiles();
                    }
                }
            }
        });
    }

    private void highlightTiles(Set<Tile> tiles, Color color) {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Polygon overlay = data.getOverlay();

            if (tiles.contains(data.getTile())) {
                overlay.setFill(color);
                overlay.setOpacity(0.4);
                overlay.setStroke(color.darker());
                overlay.setStrokeWidth(2);
            } else {
                overlay.setFill(Color.TRANSPARENT);
                overlay.setOpacity(0);
                overlay.setStroke(Color.TRANSPARENT);
            }
        }
    }

    private void highlightReachableTiles() {
        highlightTiles(reachableTiles, Color.GREEN);
    }

    private void highlightAttackRange() {
        highlightTiles(attackableTiles, Color.RED);
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
        minionCountLabel.setText(minionsProcessedThisTurn + "/2");
    }

    private void checkTurnCompletion() {
        if (minionsProcessedThisTurn >= 2) {
            beurtButton.setDisable(false);
            resetAllOverlays();
            gameState.setSelectedTile(null);
            currentlySelectedHex = null;
        }
    }



}