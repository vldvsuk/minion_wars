package models;

import java.util.*;

import javafx.scene.shape.Polygon;
import models.grond.Tile;
import view.hexagon.HexagonData;

public class GameLogic {
    private final GameState gameState;

    private final Set<Tile> attackableTiles = new HashSet<>();
    private final Set<Tile> reachableTiles = new HashSet<>();
    private final Set<Tile> powerTiles = new HashSet<>();
    private final List<Polygon> hexList = new ArrayList<>();

    public GameLogic(GameState gameState) {
        this.gameState = gameState;
    }

    public void addHexagon(HexagonData hexData) {
        hexList.add(hexData.getHex());
    }

    public Set<Tile> getAttackableTiles() {
        return Collections.unmodifiableSet(attackableTiles);
    }

    public Set<Tile> getReachableTiles() {
        return Collections.unmodifiableSet(reachableTiles);
    }

    public Set<Tile> getPowerTiles() {
        return Collections.unmodifiableSet(powerTiles);
    }



    private int calculateHexDistance(Tile start, Tile target) {
        int startX = start.getX();
        int startY = start.getY();
        int targetX = target.getX();
        int targetY = target.getY();

        // axial coordinates
        int q1 = startX - (startY - (startY&1)) / 2;
        int r1 = startY;
        int q2 = targetX - (targetY - (targetY&1)) / 2;
        int r2 = targetY;

        return (Math.abs(q1 - q2)
                + Math.abs(q1 + r1 - q2 - r2)
                + Math.abs(r1 - r2)) / 2;
    }

    public void calculateAttackRange(Tile startTile, String attackRangeStr) {
        attackableTiles.clear();
        try {
            int[] range = parseAttackRange(attackRangeStr);
            int minRange = range[0];
            int maxRange = range[1];

            for (Polygon hex : hexList) {
                HexagonData data = (HexagonData) hex.getUserData();
                Tile tile = data.getTile();
                int distance = calculateHexDistance(startTile, tile);

                if (distance >= minRange
                        && distance <= maxRange
                        && !gameState.isMinionOwnedByCurrentPlayer(tile)) {
                    attackableTiles.add(tile);
                }
            }
        } catch (Exception e) {
            System.err.println("Fout bij berekenen aanvalsbereik: " + e.getMessage());
        }

    }

    private int[] parseAttackRange(String rangeStr) {
        String[] parts = rangeStr.split(" ");
        int min = Integer.parseInt(parts[0]);
        int max = Integer.parseInt(parts[1]);
        return new int[]{min, max};
    }

    private void calculateMovementRange(Tile startTile, int movement) {
        reachableTiles.clear();
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();

            if (isValidMoveTarget(startTile, tile, movement)) {
                reachableTiles.add(tile);
            }
        }
    }

    private void calculateBonus(Tile startTile, int movement) {
        powerTiles.clear();
        for (Polygon hex : hexList) {
            HexagonData data = (HexagonData) hex.getUserData();
            Tile tile = data.getTile();

            if (isValidBonusTarget(startTile, tile, movement)) {
                powerTiles.add(tile);
            }
        }
    }

    private boolean isValidBonusTarget(Tile start, Tile target, int maxDistance) {

        int distance = calculateHexDistance(start, target);

        return distance <= maxDistance &&
                List.of("dirt", "forest", "mountains").contains(target.getType());
    }

    public boolean isValidMoveTarget(Tile start, Tile target, int maxDistance) {

        int distance = calculateHexDistance(start, target);

        return distance <= maxDistance &&
                !gameState.isOccupied(target) &&
                List.of("dirt", "forest", "mountains").contains(target.getType());

    }


}
