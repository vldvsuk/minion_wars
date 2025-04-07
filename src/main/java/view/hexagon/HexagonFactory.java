package view.hexagon;

import models.grond.Tile;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import view.images.ImagePatternHelper;

public class HexagonFactory {
    private static final double HEX_SIZE = 74.0;

    public HexagonData createHexagon(Tile tile) {
        double n = Math.sqrt(HEX_SIZE * HEX_SIZE * 0.75);
        double hexWidth = 2 * n;
        double hexHeight = 2 * HEX_SIZE;

        double xCoord = tile.getX() * hexWidth + (tile.getY() % 2) * n + 80;
        double yCoord = tile.getY() * hexHeight * 0.75 + 40;

        Polygon hex = createHexagonShape(xCoord, yCoord, n, HEX_SIZE);
        Polygon overlay = createHexagonShape(xCoord, yCoord, n, HEX_SIZE);

        setupHexagonAppearance(hex, tile);
        setupOverlayAppearance(overlay);

        return new HexagonData(hex, overlay, tile);
    }

    private Polygon createHexagonShape(double x, double y, double n, double size) {
        return new Polygon(
                x, y,
                x + n, y + size * 0.5,
                x + n, y + size * 1.5,
                x, y + 2 * size,
                x - n, y + size * 1.5,
                x - n, y + size * 0.5
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

    private void setupOverlayAppearance(Polygon overlay) {
        overlay.setFill(Color.TRANSPARENT);
        overlay.setStroke(Color.BLACK);
        overlay.setStrokeWidth(1.5);
        overlay.setOpacity(0);
        overlay.setStroke(Color.TRANSPARENT);
    }
}