package models;

import models.grond.Tile;

import java.util.HashSet;
import java.util.Set;

public class GameActions {
    private boolean hasMoved = false;
    private boolean hasAttacked = false;
    private boolean basisAttacked = false;
    private boolean specialAttack = false;

    private Set<Tile> attackableTiles = new HashSet<>();
    private Set<Tile> reachableTiles = new HashSet<>();
    private Set<Tile> powerTiles = new HashSet<>();

    // Getters en setters
    public boolean hasMoved() { return hasMoved; }
    public void setHasMoved(boolean hasMoved) { this.hasMoved = hasMoved; }

    public boolean hasAttacked() { return hasAttacked; }
    public void setHasAttacked(boolean hasAttacked) { this.hasAttacked = hasAttacked; }

    public boolean isBasisAttacked() { return basisAttacked; }
    public void setBasisAttacked(boolean basisAttacked) { this.basisAttacked = basisAttacked; }

    public boolean isSpecialAttack() { return specialAttack; }
    public void setSpecialAttack(boolean specialAttack) { this.specialAttack = specialAttack; }

    public Set<Tile> getAttackableTiles() { return attackableTiles; }
    public void setAttackableTiles(Set<Tile> attackableTiles) {
        this.attackableTiles = attackableTiles;
    }
    public Set<Tile> getReachableTiles() { return reachableTiles; }
    public void setReachableTiles(Set<Tile> reachableTiles) {
        this.reachableTiles = reachableTiles;

    }
    public Set<Tile> getPowerTiles() { return powerTiles; }
    public void setPowerTiles(Set<Tile> powerTiles) {
        this.powerTiles = powerTiles;
    }
    public void clearAttackableTiles() {
        attackableTiles.clear();
    }
    public void clearReachableTiles() {
        reachableTiles.clear();
    }
    public void clearPowerTiles() {
        powerTiles.clear();
    }
    public void resetMovementTiles() {
        reachableTiles.clear();
        attackableTiles.clear();
    }

    public void resetActions() {
        hasMoved = false;
        hasAttacked = false;
        basisAttacked = false;
        specialAttack = false;
        powerTiles.clear();
        attackableTiles.clear();
        reachableTiles.clear();
    }
}

