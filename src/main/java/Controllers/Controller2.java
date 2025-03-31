package Controllers;

import grond.Tile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
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
import models.Minion;
import parsers.FieldParser;
import parsers.MinionParser;

import java.util.List;

public class Controller2 {
    private Stage stage;
    private String speler1Naam;
    private String speler2Naam;
    private int munten;
    private boolean isSpeler1AanZet = true;

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
    private ScrollPane gameBoardScroll;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSpelerNamen(String speler1, String speler2) {
        this.speler1Naam = speler1;
        this.speler2Naam = speler2;
        updateUI();
    }

    public void setMunten(int munten) {
        this.munten = munten;
        updateUI();
    }

    @FXML
    public void initialize() {
        Image coinImage = new Image(getClass().getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/coin-FFB900.png"));
        coinImageView.setImage(coinImage);

        splitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> splitPane.setDividerPositions(0.26));
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
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(125);
        button.setPrefHeight(125);
        button.setStyle("-fx-padding: 0 10 0 0;");

        HBox content = new HBox(24);
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

        HBox mainContent = new HBox(30);
        mainContent.setAlignment(Pos.CENTER_LEFT);
        mainContent.setStyle("-fx-padding: 0 0 0 10;"); // Padding aanpassen

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

        return button;
    }
    public Polygon createHexagon(Tile tile) {
        // Hexagon coördinaten
        double hexSize = 40.0;
        double hexWidth = hexSize * 2;
        double hexHeight = Math.sqrt(3) * hexSize;

        double x = tile.getX() * hexWidth * 0.75;
        double y = tile.getY() * hexHeight + (tile.getX() % 2) * hexHeight / 2;


        Polygon hex = new Polygon();
        hex.getPoints().addAll(
                x + hexWidth * 0.5, y,
                x + hexWidth, y + hexHeight * 0.25,
                x + hexWidth, y + hexHeight * 0.75,
                x + hexWidth * 0.5, y + hexHeight,
                x, y + hexHeight * 0.75,
                x, y + hexHeight * 0.25
        );
        try {
            String imagePath = "/be/ugent/objprog/minionwars/images/tiles/" + tile.getType().toLowerCase() + ".png";

            ImagePattern imagePattern = new ImagePattern(
                    new Image(getClass().getResourceAsStream(imagePath))
            );
            hex.setFill(imagePattern);
        } catch (Exception e) {
            System.err.println("Afbeelding niet gevonden voor: " + tile.getType());
            hex.setFill(Color.LIGHTGRAY); // Fallback kleur
        }

        hex.setStroke(Color.BLACK);

        // Voeg event handler toe
        hex.setOnMouseClicked(e -> {
            System.out.printf("Tile clicked: %s at (%d,%d)%n",
                    tile.getType(), tile.getX(), tile.getY());
        });

        return hex;
    }



    private void updateUI() {
        if (isSpeler1AanZet) {
            naamLabel.setText(speler1Naam);
        } else {
            naamLabel.setText(speler2Naam);
        }
        coinsLabel.setText(String.valueOf(munten));
    }

    @FXML
    private void handleBeurtButton() {
        isSpeler1AanZet = !isSpeler1AanZet;
        updateUI();
    }
}