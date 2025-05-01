package view.images;

import javafx.scene.image.Image;

import java.util.Objects;

/**  Utility-klasse voor het laden van afbeeldingen uit de applicatieresources **/

public class ImageLoader {
    public static Image loadMinionImage(String type) {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/" + type + ".png")));}

    public static Image loadFallbackMinionImage() {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/minions/trebuchet.png")));}

    public static Image loadTileImage(String type) {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/tiles/" + type + ".png")));
    }

    public static Image loadCoinIcon() {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/coin-FFB900.png")));
    }

    public static Image loadAttackIcon() {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/attack-D60000.png")));
    }

    public static Image loadDefenseIcon() {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/health-D60000.png")));
    }

    public static Image loadUseMinionIcon() {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/minions-0073FF.png")));
    }

    public static Image loadPowerImage(String type){
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/powers/" + type + ".png")));
    }

    public static Image loadRangeIcon() {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/range-119533.png")));
    }
    public static Image loadDurationIcon(){
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/duration-0073FF.png")));
    }
    public static Image loadHealthIcon(){
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/be/ugent/objprog/minionwars/images/icons/heal-D60000.png")));
    }
}
