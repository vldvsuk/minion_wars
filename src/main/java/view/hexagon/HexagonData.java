package view.hexagon;

import models.grond.Tile;
import javafx.scene.shape.Polygon;
/** Een hulp-klasse om de info van de twee haxagoonen te behouden (hex en overlay) **/
public class HexagonData {
    private final Polygon hexagon;
    private final Polygon overlay;
    private final Tile tile;

    public HexagonData(Polygon hexagon, Polygon overlay, Tile tile) {
        this.hexagon = hexagon;
        this.overlay = overlay;
        this.tile = tile;
    }

    public Polygon getHex() {
        return hexagon;
    }

    public Polygon getOverlay() {
        return overlay;
    }

    public Tile getTile() {
        return tile;
    }
}