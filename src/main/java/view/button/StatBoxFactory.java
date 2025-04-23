package view.button;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class StatBoxFactory {
    public static HBox createStatBox(Image icon, String value, String styleClass) {
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
}
