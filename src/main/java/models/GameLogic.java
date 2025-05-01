package models;

import java.util.*;
import models.grond.Tile;
import models.minions.Minion;


/**
 * Bevat de kernlogica van het spel voor beweging, aanvallen en berekeningen.
 * Implementeert spelregels en wiskundige berekeningen voor het hexagonale grid.
 */

public class GameLogic {
    private final GameState gameState;

    public GameLogic(GameState gameState) {
        this.gameState = gameState;
    }

    public Set<Tile> calculateMovementRange(Tile startTile, int movement) { // bereken tegels waar de minion naartoe kan bewegen
        Set<Tile> reachable = new HashSet<>();

        for (Tile tile : gameState.getTiles()) {
            if (tile.getType().equals("forest")){
                if (isValidMoveTarget(startTile, tile, movement - 1)){
                    reachable.add(tile);
                }
            }else if (isValidMoveTarget(startTile, tile, movement)) {
                reachable.add(tile);
            }
        }
        return reachable;
    }

    private boolean isValidMoveTarget(Tile start, Tile target, int maxDistance) { // controleert of een tegel een geldige bestemming is
        int distance = calculateHexDistance(start, target);
        Minion minion = gameState.getPlacedMinion(start);

        return distance <= maxDistance &&
                !gameState.isOccupied(target) &&
                List.of("dirt", "forest", "mountains").contains(target.getType()) && !minion.isParalized();
    }

    public Set<Tile> calculateAttackRange(Tile startTile, int minRange, int maxRange) { // Bepaalt aanvalsbereik voor een minion
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

    public int calculateHexDistance(Tile start, Tile target) { //  Bereken afstand tussen twee tegels in een hexgrid
        int startX = start.getX();
        int startY = start.getY();
        int targetX = target.getX();
        int targetY = target.getY();

        // Converteer naar axial coördinaten

        int q1 = startX - (startY - (startY & 1)) / 2;
        int q2 = targetX - (targetY - (targetY & 1)) / 2;

        return (Math.abs(q1 - q2) + Math.abs(q1 + startY - q2 - targetY) + Math.abs(startY - targetY)) / 2;
    }

    public Set<Tile> calculateBonusRange(Tile centerTile, int radius) { // Bepaalt bereik voor speciale krachten
        Set<Tile> powerTiles = new HashSet<>();
        for (Tile tile : gameState.getTiles()) {
            int distance = calculateHexDistance(centerTile, tile);
            if (distance <= radius && List.of("dirt", "forest", "mountains").contains(tile.getType())) {
                powerTiles.add(tile);
            }
        }
        return powerTiles;
    }
    public boolean hasEnemyInAttackRange(Set<Tile> tiles){ // Controleert op vijanden in een aanvalsbereik

        for (Tile tile : tiles) {
            if (gameState.isOccupied(tile) && !gameState.isMinionOwnedByCurrentPlayer(tile)) {
                return true;
            }
        }
        return false;
    }
    public boolean hasFriendlyInRange(Set<Tile> tiles){ // Controleert op eigen minions in een gebied

        for (Tile tile : tiles) {
            if (gameState.isOccupied(tile) && gameState.isMinionOwnedByCurrentPlayer(tile)) {
                return true;
            }
        }
        return false;
    }


}
