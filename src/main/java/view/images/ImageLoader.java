package view.images;

import javafx.scene.image.Image;

public class ImageLoader {
    public static Image loadMinionImage(String type) {
        return new Image(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/" + type + ".png"));}

    public static Image loadFallbackMinionImage() {
        return new Image(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/trebuchet.png"));}

    public static Image loadTileImage(String type) {
        return new Image(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/tiles/" + type + ".png"));
    }

    public static Image loadCoinIcon() {
        return new Image(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/coin-FFB900.png"));
    }

    public static Image loadAttackIcon() {
        return new Image(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/attack-D60000.png"));
    }

    public static Image loadDefenseIcon() {
        return new Image(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/health-D60000.png"));
    }
}
