package view.hexagon;

import models.grond.Tile;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import view.images.ImagePatternHelper;


/**Fabriek voor het aanmaken van hexagonale vormen voor tegels.**/

public class HexagonFactory {
    private static final double HEX_SIZE = 74.0;

    public HexagonData createHexagon(Tile tile) {

        double n = Math.sqrt(HEX_SIZE * HEX_SIZE * 0.75);  // Korte zijde
        double hexWidth = 2 * n;                           // Breedte tussen hexagonen
        double hexHeight = 2 * HEX_SIZE;                   // Hoogte van hexagoon

        // Bepaal positie op het bord met offset voor even/oneven rijen

        double xCoord = tile.getX() * hexWidth + (tile.getY() % 2) * n + 80;
        double yCoord = tile.getY() * hexHeight * 0.75 + 40;  // 0.75 voor verticale overlapping


        Polygon hex = createHexagonShape(xCoord, yCoord, n);
        Polygon overlay = createHexagonShape(xCoord, yCoord, n);


        setupHexagonAppearance(hex, tile);
        setupOverlayAppearance(overlay);

        return new HexagonData(hex, overlay, tile);
    }

    private Polygon createHexagonShape(double x, double y, double n) {
        return new Polygon(
                x, y,
                x + n, y + HEX_SIZE * 0.5,
                x + n, y + HEX_SIZE * 1.5,
                x, y + 2 * HEX_SIZE,
                x - n, y + HEX_SIZE * 1.5,
                x - n, y + HEX_SIZE * 0.5
        );
    }

    private void setupHexagonAppearance(Polygon hex, Tile tile) {
        try {
            ImagePattern pattern = ImagePatternHelper.createTilePattern(tile.getType().toLowerCase());
            hex.setFill(pattern);
        } catch (Exception e) {
            hex.setFill(Color.LIGHTGRAY);
        }
        hex.setStroke(Color.BLACK);
        hex.setStrokeWidth(1.5);
    }

    private void setupOverlayAppearance(Polygon overlay) { // visuele eigenschapen voor overlay
        overlay.setFill(Color.TRANSPARENT);
        overlay.setStroke(Color.BLACK);
        overlay.setStrokeWidth(1.5);
        overlay.setOpacity(0);
        overlay.setStroke(Color.TRANSPARENT);
    }
}