package view.button;


import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.shape.*;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import models.GameState;
import models.minions.Minion;
import view.images.ImageLoader;

import java.util.List;

import static view.button.StatBoxFactory.createStatBox;

public class MinionButtonFactory {
    private final GameState gameState;
    private final List<Button> minionButtons;

    public MinionButtonFactory(GameState gameState, List<Button> minionButtons) {
        this.gameState = gameState;
        this.minionButtons = minionButtons;
    }

    public Button createMinionButton(Minion minion) {
        Button button = new Button();
        button.getStyleClass().add("minion-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(125);
        button.setPrefHeight(125);
        button.setUserData(minion);

        HBox content = createButtonContent(minion);
        button.setGraphic(content);

        button.setOnAction(e -> handleButtonClick(button, minion));
        return button;
    }

    private HBox createButtonContent(Minion minion) {
        HBox content = new HBox(17);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = createMinionImageView(minion);
        HBox mainContent = createMainContent(minion);

        content.getChildren().addAll(imageView, mainContent);
        return content;
    }

    private ImageView createMinionImageView(Minion minion) {
        ImageView imageView = new ImageView();
        try {
            Image image = ImageLoader.loadMinionImage(minion.getType());
            imageView.setImage(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            Circle clip = new Circle(50, 50, 50);
            imageView.setClip(clip);
        } catch (Exception e) {
            imageView.setImage(ImageLoader.loadFallbackMinionImage());
        }
        return imageView;
    }

    private HBox createMainContent(Minion minion) {
        HBox mainContent = new HBox(22);
        mainContent.setAlignment(Pos.CENTER_LEFT);
        mainContent.setStyle("-fx-padding: 0 0 0 10;");

        Label nameLabel = new Label(minion.getName());
        nameLabel.getStyleClass().add("minion-name");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPrefWidth(200);

        VBox statsBox = createStatsBox(minion);
        mainContent.getChildren().addAll(nameLabel, statsBox);
        return mainContent;
    }

    private VBox createStatsBox(Minion minion) {
        VBox statsBox = new VBox(5);
        statsBox.setAlignment(Pos.CENTER_RIGHT);

        HBox coinBox = createStatBox(
                ImageLoader.loadCoinIcon(),
                String.valueOf(minion.getCost()),
                "cost-stat"
        );
        HBox attackBox = createStatBox(
                ImageLoader.loadAttackIcon(),
                String.valueOf(minion.getAttack()),
                "attack-stat"
        );
        HBox defenseBox = createStatBox(
                ImageLoader.loadDefenseIcon(),
                String.valueOf(minion.getDefence()),
                "defence-stat"
        );

        statsBox.getChildren().addAll(coinBox, attackBox, defenseBox);
        return statsBox;
    }


    private void handleButtonClick(Button button, Minion minion) {
        minionButtons.forEach(btn -> btn.getStyleClass().remove("selected"));

        if (gameState.getSelectedMinion() == minion) {
            gameState.setSelectedMinion(null);
            button.getStyleClass().remove("selected");
        } else if (gameState.canAffordMinion(minion)) {
            gameState.setSelectedMinion(minion);
            button.getStyleClass().add("selected");

        }
    }
}
