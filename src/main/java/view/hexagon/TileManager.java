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

/** Beheert de visuele weergave van hexagonen en hun overlay-effecten.**/

public class TileManager {
    private final GameState gameState;
    private final GameLogic gameLogic;
    private final List<Polygon> hexList;

    public TileManager(GameState gameState,GameLogic gameLogic, List<Polygon> hexList) {
        this.gameState = gameState;
        this.hexList = hexList;
        this.gameLogic = gameLogic;
    }


    public void resetAllOverlays() { // Reset alle overlay-effecten naar standaardwaarden en de borders van de minions
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


    public void highlightTiles(Set<Tile> tiles, Color color) { // Markeert specifieke tegels met een kleur voor visuele highlight
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


    public void updateMinionVisual(Tile tile, Minion minion) { // Update de visuele weergave van een minion op een tegel
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

    public void markHomebases() { // Markeert homebases tijdens plaatsingsfase
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

    public void resetTileVisual(Tile tile) { // Reset een tegel naar originele weergave
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

    public void updateMinionVisibility() { // Update zichtbaarheid van minions op basis van spelstatus
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

    private void resetTileToOriginal(Polygon hex, Tile tile) { // tile resetten als er minion verbergen is
        try {
            hex.setFill(ImagePatternHelper.createTilePattern(tile.getType().toLowerCase()));
        } catch (Exception e) {
            hex.setFill(Color.LIGHTGRAY);
        }
    }

    public void markSelected(Polygon overlayHex) { // selected overlay tile/minion
        overlayHex.setFill(Color.rgb(255, 255, 11, 0.3));
        overlayHex.setOpacity(0.7);
        overlayHex.setStroke(Color.GREEN);
    }

    public void updatePowerRangeVisuals(Tile centerTile) { // Update de visuele weergave voor krachtbereiken
        gameState.getGameActions().clearPowerTiles();
        String powerType = gameState.getSelectedPower().getType().toLowerCase();
        gameState.getGameActions().setPowerTiles(gameLogic.calculateBonusRange(centerTile, gameState.getSelectedPower().getRadius()));
        boolean hasValidTarget = switch (powerType) {
            case "healing" -> gameLogic.hasFriendlyInRange(gameState.getGameActions().getPowerTiles());
            case "fireball", "lightning" -> gameLogic.hasEnemyInAttackRange(gameState.getGameActions().getPowerTiles());
            default -> false;
        };
        if (hasValidTarget) {
            highlightTiles(gameState.getGameActions().getPowerTiles(), Color.BLUE);
        } else {
            highlightTiles(gameState.getGameActions().getPowerTiles(), Color.GREEN);
        }
    }
}