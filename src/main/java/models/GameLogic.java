package models;

import java.util.*;
import models.grond.Tile;
import models.minions.Minion;

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
        Minion minion = gameState.getPlacedMinion(start);

        return distance <= maxDistance &&
                !gameState.isOccupied(target) &&
                List.of("dirt", "forest", "mountains").contains(target.getType()) && !minion.isParalized();
    }

    public Set<Tile> calculateAttackRange(Tile startTile, int minRange, int maxRange) {
        Set<Tile> attackable = new HashSet<>();
        Minion minion = gameState.getPlacedMinion(startTile);

        try {

            for (Tile tile : gameState.getTiles()) {
                int distance = calculateHexDistance(startTile, tile);
                if (distance >= minRange && distance <= maxRange && !gameState.isMinionOwnedByCurrentPlayer(tile) &&
                        !"mountains".equals(startTile.getType()) && !minion.isParalized()) {
                    attackable.add(tile);
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating attack range: " + e.getMessage());
        }
        return attackable;
    }

    public int calculateHexDistance(Tile start, Tile target) {
        int startX = start.getX();
        int startY = start.getY();
        int targetX = target.getX();
        int targetY = target.getY();

        int q1 = startX - (startY - (startY & 1)) / 2;
        int q2 = targetX - (targetY - (targetY & 1)) / 2;

        return (Math.abs(q1 - q2) + Math.abs(q1 + startY - q2 - targetY) + Math.abs(startY - targetY)) / 2;
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
    public boolean hasEnemyInAttackRange(Set<Tile> tiles){

        for (Tile tile : tiles) {
            if (gameState.isOccupied(tile) && !gameState.isMinionOwnedByCurrentPlayer(tile)) {
                return true;
            }
        }
        return false;
    }
    public boolean hasFriendlyInRange(Set<Tile> tiles){

        for (Tile tile : tiles) {
            if (gameState.isOccupied(tile) && gameState.isMinionOwnedByCurrentPlayer(tile)) {
                return true;
            }
        }
        return false;
    }


}
