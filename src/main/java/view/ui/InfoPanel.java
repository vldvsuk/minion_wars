package view.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.GameState;
import models.grond.Tile;
import models.minions.Minion;
import view.images.ImageLoader;

import static view.button.StatBoxFactory.createStatBox;

public class InfoPanel {

    private final GameState gameState;

    public InfoPanel(GameState gameState) {
        this.gameState = gameState;
    }

    public HBox generateMinionInfo(Tile selectedTile, Minion selectedMinion) {

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


    public HBox generateTileInfo(Tile tile) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        // Tegel afbeelding
        ImageView imageView = new ImageView();
        try {
            Image image = ImageLoader.loadTileImage(tile.getType().toLowerCase());
            imageView.setImage(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            Circle clip = new Circle(50, 50, 40);
            imageView.setClip(clip);
        } catch (Exception e) {
            imageView.setImage(ImageLoader.loadFallbackMinionImage());
        }

        // VBox voor tekstinfo
        VBox textBox = new VBox(5);
        textBox.setAlignment(Pos.CENTER_LEFT);

        // Naam label
        Label nameLabel = new Label("Ondergrond: " + tile.getType());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 23));

        textBox.getChildren().add(nameLabel);
        hbox.getChildren().addAll(imageView, textBox);

        return hbox;
    }

    public void generateEffectInfo(Tile tile) {
        // Tile info opbouw
    }

    public void updateLabelBox(VBox labelBox, HBox content) {
        labelBox.getChildren().clear();
        labelBox.getChildren().add(content);
    }
}



