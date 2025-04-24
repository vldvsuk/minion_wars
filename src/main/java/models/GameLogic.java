package models;

import java.util.*;
import models.grond.Tile;

public class GameLogic {
    private final GameState gameState;

    public GameLogic(GameState gameState) {
        this.gameState = gameState;
    }

    public Set<Tile> calculateMovementRange(Tile startTile, int movement) {
        Set<Tile> reachable = new HashSet<>();
        if ("forest".equals(startTile.getType())){
            movement-=1;
        }

        for (Tile tile : gameState.getTiles()) {
            if (isValidMoveTarget(startTile, tile, movement)) {
                reachable.add(tile);
            }
        }
        return reachable;
    }

    private boolean isValidMoveTarget(Tile start, Tile target, int maxDistance) {
        int distance = calculateHexDistance(start, target);

        return distance <= maxDistance &&
                !gameState.isOccupied(target) &&
                List.of("dirt", "forest", "mountains").contains(target.getType());
    }

    public Set<Tile> calculateAttackRange(Tile startTile, String attackRangeStr) {
        Set<Tile> attackable = new HashSet<>();
        try {
            int[] range = parseAttackRange(attackRangeStr);
            int minRange = range[0];
            int maxRange = range[1];

            for (Tile tile : gameState.getTiles()) {
                int distance = calculateHexDistance(startTile, tile);
                if (distance >= minRange && distance <= maxRange && !gameState.isMinionOwnedByCurrentPlayer(tile) &&  !"mountains".equals(startTile.getType())) {
                    attackable.add(tile);
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating attack range: " + e.getMessage());
        }
        return attackable;
    }

    private int[] parseAttackRange(String rangeStr) {
        String[] parts = rangeStr.split(" ");
        int min = Integer.parseInt(parts[0]);
        int max = Integer.parseInt(parts[1]);
        return new int[]{min, max};
    }

    public int calculateHexDistance(Tile start, Tile target) {
        int startX = start.getX();
        int startY = start.getY();
        int targetX = target.getX();
        int targetY = target.getY();

        int q1 = startX - (startY - (startY & 1)) / 2;
        int r1 = startY;
        int q2 = targetX - (targetY - (targetY & 1)) / 2;
        int r2 = targetY;

        return (Math.abs(q1 - q2) + Math.abs(q1 + r1 - q2 - r2) + Math.abs(r1 - r2)) / 2;
    }

    public Set<Tile> calculateBonusRange(Tile centerTile, int radius) {
        Set<Tile> powerTiles = new HashSet<>();
        for (Tile tile : gameState.getTiles()) {
            int distance = calculateHexDistance(centerTile, tile);
            if (distance <= radius && List.of("dirt", "forest", "mountains").contains(tile.getType())) {
                powerTiles.add(tile);
            }
        }
        return powerTiles;
    }
    public boolean  hasEnemyInAttackRange(Set<Tile> attackable){

        for (Tile tile : attackable) {
            if (gameState.isOccupied(tile)) {
                return true;
            }
        }
        return false;
    }
}
