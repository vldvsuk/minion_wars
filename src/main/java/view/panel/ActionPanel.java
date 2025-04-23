package view.panels;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import models.GameState;
import models.minions.Minion;
import models.powers.Power;
import models.parsers.PowerParser;
import view.images.ImageLoader;
import view.button.StatBoxFactory;

import java.util.List;
import java.util.function.Consumer;

public class ActionPanel extends VBox {

    private final GameState gameState;
    private final Runnable onStayAction;
    private final Runnable onBasicAttackAction;
    private final Runnable onHealAction;
    private final Consumer<Power> onPowerSelected;
    private final Consumer<String> onTabChanged;

    public ActionPanel(GameState gameState,
                       Runnable onStayAction,
                       Runnable onBasicAttackAction,
                       Runnable onHealAction,
                       Consumer<Power> onPowerSelected,
                       Consumer<String> onTabChanged) {
        this.gameState = gameState;
        this.onStayAction = onStayAction;
        this.onBasicAttackAction = onBasicAttackAction;
        this.onHealAction = onHealAction;
        this.onPowerSelected = onPowerSelected;
        this.onTabChanged = onTabChanged;

        initializePanel();
    }

    private void initializePanel() {
        setSpacing(10);
        setPrefWidth(180);
        setAlignment(Pos.TOP_CENTER);

        VBox labelBox = createLabelBox();
        TabPane tabPane = createTabPane();

        getChildren().addAll(labelBox, tabPane);
    }

    private VBox createLabelBox() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Kies een minion!");
        label.setFont(Font.font("System", FontWeight.BOLD, 24));
        box.setMinHeight(100);
        box.getChildren().add(label);
        return box;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                createMovementTab(),
                createAttackTab(),
                createBonusTab()
        );

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                onTabChanged.accept(newTab.getText());
            }
        });

        return tabPane;
    }

    private Tab createMovementTab() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(170);

        Label label = new Label("Selecteer een groen veld op het spelbord of kies om te blijven staan");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font("System", FontWeight.BOLD, 17));
        label.setWrapText(true);
        label.setMaxWidth(160);

        Button stayButton = new Button("Blijven staan");
        stayButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        stayButton.setOnAction(e -> onStayAction.run());

        content.getChildren().addAll(label, stayButton);
        return new Tab("Bewegen", content);
    }

    private Tab createAttackTab() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(170);

        Label label = new Label("Selecteer een aanval uit de lijst en klik op een vijandelijke minion");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font("System", FontWeight.BOLD, 17));
        label.setWrapText(true);
        label.setMaxWidth(160);

        Button basicAttackButton = new Button("Basis aanval");
        basicAttackButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        basicAttackButton.setOnAction(e -> onBasicAttackAction.run());

        Button specialAttackButton = new Button("Speciale aanval");
        specialAttackButton.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label orLabel = new Label("of");
        orLabel.setFont(Font.font("System", FontWeight.BOLD, 17));

        Button healButton = new Button("Genees minion (+2hp)");
        healButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        healButton.setOnAction(e -> onHealAction.run());

        content.getChildren().addAll(
                label,
                basicAttackButton,
                specialAttackButton,
                orLabel,
                healButton
        );

        return new Tab("Aanvallen", new StackPane(content));
    }

    private Tab createBonusTab() {
        VBox content = new VBox(10);
        content.setPrefWidth(170);

        PowerParser powerParser = new PowerParser();
        List<Power> powers = powerParser.parsePowers();

        for (Power power : powers) {
            Button powerButton = createPowerButton(power);
            content.getChildren().add(powerButton);
        }

        return new Tab("Bonus", content);
    }

    private Button createPowerButton(Power power) {
        Button button = new Button();
        button.getStyleClass().add("minion-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(100);
        button.setPrefHeight(75);
        button.setUserData(power);

        HBox mainContent = new HBox(15);
        mainContent.setAlignment(Pos.CENTER_LEFT);

        // Power image
        ImageView powerImage = new ImageView();
        try {
            powerImage.setImage(ImageLoader.loadPowerImage(power.getType()));
        } catch (Exception e) {
            powerImage.setImage(ImageLoader.loadFallbackMinionImage());
        }
        powerImage.setFitHeight(100);
        powerImage.setFitWidth(100);
        powerImage.setPreserveRatio(true);
        powerImage.setClip(new Circle(50, 50, 40));

        // Details container
        HBox detailsContainer = new HBox(70);
        detailsContainer.setAlignment(Pos.CENTER_LEFT);

        // Text details
        VBox textDetails = new VBox(5);
        textDetails.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(power.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 23));
        nameLabel.setPrefWidth(150);

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
        VBox statsContainer = createPowerStats(power);

        detailsContainer.getChildren().addAll(textDetails, statsContainer);
        mainContent.getChildren().addAll(powerImage, detailsContainer);
        button.setGraphic(mainContent);

        button.setOnAction(e -> onPowerSelected.accept(power));
        return button;
    }

    private VBox createPowerStats(Power power) {
        VBox statsContainer = new VBox(5);
        statsContainer.setAlignment(Pos.CENTER);

        Image valueIcon = switch (power.getType().toLowerCase()) {
            case "healing" -> ImageLoader.loadHealthIcon();
            default -> ImageLoader.loadAttackIcon();
        };

        HBox valueBox = StatBoxFactory.createStatBox(
                valueIcon,
                String.valueOf(power.getValue()),
                "value-stat"
        );

        HBox radiusBox = StatBoxFactory.createStatBox(
                ImageLoader.loadRangeIcon(),
                String.valueOf(power.getRadius()),
                "radius-stat"
        );

        HBox durationBox = StatBoxFactory.createStatBox(
                ImageLoader.loadDurationIcon(),
                "1",
                "duration-stat"
        );

        statsContainer.getChildren().add(valueBox);
        if (!power.getType().equalsIgnoreCase("lightning")) {
            statsContainer.getChildren().add(radiusBox);
        }
        if (!power.getType().equalsIgnoreCase("healing")) {
            statsContainer.getChildren().add(durationBox);
        }

        return statsContainer;
    }
}