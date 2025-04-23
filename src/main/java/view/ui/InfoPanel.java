package view.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.grond.Tile;
import models.minions.Minion;

public class InfoPanel {


    //Moet geen void zjjn
    public void generateMinionInfo(Minion minion, Tile tile) {
        // Minion info opbouw
    }


    //Moet geen void zjjn
    public void generateTileInfo(Tile tile) {
        // Tile info opbouw
    }

    public void generateEffectInfo(Tile tile) {
        // Tile info opbouw
    }

    public void updateLabelBox(VBox labelBox, HBox content) {
        labelBox.getChildren().clear();
        labelBox.getChildren().add(content);
    }
}



