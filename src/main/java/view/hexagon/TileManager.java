package view.hexagon;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import models.GameLogic;
import models.GameState;
import models.grond.Tile;
import models.minions.Minion;
import view.images.ImagePatternHelper;

import java.util.List;
import java.util.Set;

public class TileManager {
    private final GameState gameState;
    private final List<Polygon> hexList;

    public TileManager(GameState gameState, List<Polygon> hexList) {
        this.gameState = gameState;
        this.hexList = hexList;
    }


    public void resetAllOverlays() {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();
            Polygon overlay = data.getOverlay();

            if (tile != gameState.getSelectedTile()) {
                overlay.setFill(Color.TRANSPARENT);
                overlay.setOpacity(0.7);
                overlay.setStrokeWidth(1.5);
                overlay.setStroke(Color.BLACK);
            }

            if (!gameState.isPlacementPhase()) {
                if (gameState.isOccupied(tile)) {
                    overlay.setStrokeWidth(4);
                    if(gameState.getProcessedMinions().contains(gameState.getPlacedMinion(tile))) {
                        overlay.setStroke(Color.BLUE);
                    }else{

                        overlay.setStroke(gameState.isMinionOwnedByCurrentPlayer(tile)
                                ? Color.GREEN
                                : Color.RED);
                    }
                }
            } else  {
                overlay.setStrokeWidth(1.5);
                overlay.setStroke(Color.BLACK);
            }
        }
    }


    public void highlightTiles(Set<Tile> tiles, Color color) {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Polygon overlay = data.getOverlay();
            Tile tile = data.getTile();

            if (gameState.getSelectedTile() == tile){
                overlay.setFill(Color.rgb(255, 255, 11, 0.3));
                overlay.setOpacity(0.7);
                overlay.setStroke(Color.GREEN);

            } else if (tiles.contains(data.getTile())) {
                overlay.setFill(color);
                overlay.setOpacity(0.4);
            } else  {
                overlay.setFill(Color.TRANSPARENT);
            }

        }
    }


    public void updateMinionVisual(Tile tile, Minion minion) {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            if (data.getTile().equals(tile)) {
                try {
                    hex.setFill(ImagePatternHelper.createMinionPattern(minion.getType()));
                } catch (Exception e) {
                    hex.setFill(Color.RED);
                }
                break;
            }
        }
    }

    public void markHomebases() {
        if (!gameState.isPlacementPhase()) return;
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();
            Polygon overlayHex = data.getOverlay();

            if (hex == gameState.getCurrentlySelectedHex()) continue;
            // Reset overlay
            overlayHex.setFill(Color.TRANSPARENT);
            overlayHex.setOpacity(0);
            overlayHex.setStroke(Color.BLACK);
            overlayHex.setStrokeWidth(1.5);

            if (gameState.isPlacementPhase()) {
                if (gameState.isOccupied(tile)) {
                    if (!gameState.isMinionOwnedByCurrentPlayer(tile)) {
                        overlayHex.setFill(Color.RED);
                        overlayHex.setOpacity(0.4);
                    }
                } else {
                    overlayHex.setFill(gameState.isSpeler1AanZet() ?
                            (tile.getHomebase() == 1 ? Color.YELLOW : Color.RED) :
                            (tile.getHomebase() == 2 ? Color.YELLOW : Color.RED));
                    overlayHex.setOpacity(0.4);
                }
            } else {
                if (hex == gameState.getCurrentlySelectedHex() && gameState.isOccupied(tile) &&
                        gameState.isMinionOwnedByCurrentPlayer(tile)) {
                    overlayHex.setFill(Color.rgb(255, 255, 11, 0.3));
                    overlayHex.setOpacity(0.5);
                }
            }
        }

    }

    public void resetTileVisual(Tile tile) {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            if (data.getTile().equals(tile)) {
                try {
                    hex.setFill(ImagePatternHelper.createTilePattern(tile.getType().toLowerCase()));
                } catch (Exception e) {
                    hex.setFill(Color.LIGHTGRAY);
                }
                // Reset overlay
                data.getOverlay().setFill(Color.TRANSPARENT);
                data.getOverlay().setOpacity(0);
                data.getOverlay().setStroke(Color.BLACK);
                data.getOverlay().setStrokeWidth(1.5);
                data.getOverlay().setStyle("");
                markHomebases();
                break;
            }
        }
    }

    public void updateMinionVisibility() {
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();

            if (gameState.isOccupied(tile)) {
                if (gameState.isPlacementPhase()) {
                    boolean showMinion = gameState.isMinionOwnedByCurrentPlayer(tile);
                    if (showMinion) {
                        Minion minion = gameState.getPlacedMinion(tile);
                        try {
                            hex.setFill(ImagePatternHelper.createMinionPattern(minion.getType()));
                        } catch (Exception e) {
                            hex.setFill(Color.RED);
                        }
                    } else {
                        resetTileToOriginal(hex, tile);
                    }
                } else {
                    // Show all minions na placement
                    Minion minion = gameState.getPlacedMinion(tile);
                    try {
                        hex.setFill(ImagePatternHelper.createMinionPattern(minion.getType()));
                    } catch (Exception e) {
                        hex.setFill(Color.RED);
                    }
                }
            }
        }
    }

    private void resetTileToOriginal(Polygon hex, Tile tile) {
        try {
            hex.setFill(ImagePatternHelper.createTilePattern(tile.getType().toLowerCase()));
        } catch (Exception e) {
            hex.setFill(Color.LIGHTGRAY);
        }
    }

    public void markSelected(Polygon overlayHex) {
        overlayHex.setFill(Color.rgb(255, 255, 11, 0.3));
        overlayHex.setOpacity(0.7);
        overlayHex.setStroke(Color.GREEN);
    }
}