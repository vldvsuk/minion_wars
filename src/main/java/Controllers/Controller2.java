package Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
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
    private List<Button> minionButtons = new ArrayList<>();
    private List<Polygon> hexList = new ArrayList<>();
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



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInfo(String speler1, String speler2, int munten) {
        this.gameState = new GameState(speler1, speler2, munten);
        updateUI();
    }

    private void updateUI() {
        naamLabel.setText(gameState.getCurrentPlayerName());
        coinsLabel.setText(String.valueOf(gameState.getCurrentCoins()));
        hexList.forEach(btn -> btn.setStyle(""));
    }

    @FXML
    private void handleBeurtButton() {
        gameState.switchPlayer();
        gameState.setSelectedMinion(null);
        gameState.setSelectedTile(null);
        minionButtons.forEach(btn -> btn.setStyle(""));
        updateUI();
    }


    @FXML
    public void initialize() {

        Image coinImage = new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/coin-FFB900.png"));
        coinImageView.setImage(coinImage);

        splitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
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
            Polygon hex = createHexagon(tile);
            gameBoardContainer.getChildren().add(hex);
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
            Image image = new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/" + minion.getType().toLowerCase() + ".png"));
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
                button.setStyle("");
            } else {
                gameState.setSelectedMinion(minion);
                minionsContainer.getChildren().forEach(btn ->
                        btn.setStyle(btn == button ? "-fx-border-color: gold;" : ""));
            }
        });

        return button;
    }


    public Polygon createHexagon(Tile tile) {

        double hexSize = 74.0;
        double n = Math.sqrt(hexSize * hexSize * 0.75);
        double hexWidth = 2 * n;
        double hexHeight = 2 * hexSize;


        double xCoord = tile.getX() * hexWidth + (tile.getY() % 2) * n;
        double yCoord = tile.getY() * hexHeight * 0.75;

        xCoord += 80;
        yCoord += 40;


        Polygon hex = new Polygon();
        hexList.add(hex);

        hex.getPoints().addAll(
                xCoord, yCoord,                         // Punt boven
                xCoord + n, yCoord + hexSize * 0.5,          // Rechts boven
                xCoord + n, yCoord + hexSize * 1.5,          // Rechts onder
                xCoord, yCoord + 2 * hexSize,                // Punt onder
                xCoord - n, yCoord + hexSize * 1.5,          // Links onder
                xCoord - n, yCoord + hexSize * 0.5           // Links boven
        );

        try {
            String imagePath = "/be/ugent/objprog/minionwars/images/tiles/" + tile.getType().toLowerCase() + ".png";
            ImagePattern imagePattern = new ImagePattern(
                    new Image(getClass().getResourceAsStream(imagePath))
            );
            hex.setFill(imagePattern);
        } catch (Exception e) {
            System.err.println("Afbeelding niet gevonden voor: " + tile.getType());
            hex.setFill(Color.LIGHTGRAY);
        }

        hex.setStroke(Color.BLACK);
        hex.setStrokeWidth(1.5);
        hex.setUserData(tile);
        hex.setOnMouseClicked(e -> handleTileClick(tile, hex));

        return hex;
    }

    private void handleTileClick(Tile tile, Polygon hex) {
        if (currentlySelectedHex != null) {
            currentlySelectedHex.getStyleClass().remove("selected-hex");
        }

        if (currentlySelectedHex == hex) {
            currentlySelectedHex = null;
            gameState.setSelectedTile(null);
            return;
        }

        if (gameState.getSelectedMinion() != null) {
            if (gameState.isValidPlacement(tile)) {
                placeMinion(tile, hex);
            }
        } else if (gameState.isOccupied(tile)) {
            selectMinion(tile, hex);
            currentlySelectedHex = hex;
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

    private void selectMinion(Tile tile, Polygon hex) {
        gameState.setSelectedTile(tile);
        hex.getStyleClass().add("selected-hex");
    }
}


