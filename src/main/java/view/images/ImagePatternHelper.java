package view.images;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

/**  Helperklasse voor het aanmaken van herhalende beeldpatronen voor tegels en minions **/

public class ImagePatternHelper {
    public static ImagePattern createTilePattern(String tileType) {
        Image image = ImageLoader.loadTileImage(tileType);
        return new ImagePattern(image);
    }

    public static ImagePattern createMinionPattern(String minionType) {
        Image image = ImageLoader.loadMinionImage(minionType);
        return new ImagePattern(image);
    }
}
