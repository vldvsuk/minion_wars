package view.button;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.GameState;
import models.powers.Power;
import view.images.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PowerButtonHelper {
    private final List<Button> powerButtons = new ArrayList<>();
    private final GameState gameState;
    private final Consumer<Power> onPowerSelected;

    public PowerButtonHelper(GameState gameState, Consumer<Power> onPowerSelected) {
        this.gameState = gameState;
        this.onPowerSelected = onPowerSelected;
    }

    public Button createPowerButton(Power power) {
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

        button.setOnAction(e -> {
            onPowerSelected.accept(power);
        });

        powerButtons.add(button);

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

    public void updatePowerButtonsStyle() {
        powerButtons.forEach(btn -> {
            btn.getStyleClass().remove("selected");

            Power power = (Power) btn.getUserData();
            if (gameState.getPowerUsed() >= 2 || gameState.getPowerBoolean()) {
                btn.getStyleClass().add("unaffordable");
            }else if (power == gameState.getSelectedPower()) {
                btn.getStyleClass().add("selected");
            }
        });
    }
}
