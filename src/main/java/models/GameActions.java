package models;

import models.grond.Tile;

import java.util.HashSet;
import java.util.Set;

/**
 * Houdt de actiestaten van het huidige spel bij, inclusief beweging, aanvallen en selecteerbare tegels.
 * Beheert tijdelijke spelstatussen gerelateerd aan speleracties en UI-interacties.
 */

public class GameActions {

    // Actievlaggen
    private boolean hasMoved = false;          // Geeft aan of de huidige minion bewogen is
    private boolean hasAttacked = false;       // Geeft aan of de huidige minion aangevallen heeft
    private boolean basisAttacked = false;     // Geeft aan of een basisaanval gebruikt is
    private boolean specialAttack = false;     // Geeft aan of een speciale aanval gebruikt is
    private boolean hasNoAction = false;       // Geeft aan of een minion geen acties kan uitvoeren

    // Tegelselectie-sets
    private Set<Tile> attackableTiles = new HashSet<>();  // Tegels beschikbaar voor aanvallen
    private Set<Tile> reachableTiles = new HashSet<>();   // Tegels beschikbaar voor beweging
    private Set<Tile> powerTiles = new HashSet<>();       // Tegels beïnvloed door krachten

    // UI-staat
    private String currentTab = "Bewegen";     // Huidig geselecteerd actietabblad
    private int minionProcessed = 0;           // Aantal minions dat acties heeft voltooid

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

    public void clearReachableTiles() {
        reachableTiles.clear();
    }
    public void clearPowerTiles() {
        powerTiles.clear();
    }
    public String getCurrentTab() { return currentTab; }
    public void setCurrentTab(String currentTab) { this.currentTab = currentTab; }
    public int getMinionProcessed() { return minionProcessed; }
    public void oneMoreMinionProcessed() { minionProcessed++; }
    public void setHasNoAction(boolean hasNoAction) { this.hasNoAction = hasNoAction; }
    public boolean isHasNoAction() { return hasNoAction; }

    public void resetActions() {  // Reset alle actiestaten voor een nieuwe beurt
        hasMoved = false;
        hasAttacked = false;
        basisAttacked = false;
        specialAttack = false;
        powerTiles.clear();
        attackableTiles.clear();
        reachableTiles.clear();
        currentTab = "Bewegen";
        minionProcessed = 0;
    }
}

