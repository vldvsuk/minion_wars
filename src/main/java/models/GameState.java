package models;
import models.effects.Effect;
import models.grond.Tile;
import models.minions.Minion;
import models.parsers.EffectParser;
import models.powers.Power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameState {
    private final Map<Tile, Minion> speler1Minions = new HashMap<>();
    private final Map<Tile, Minion> speler2Minions = new HashMap<>();
    private Minion selectedMinion;
    private Tile selectedTile;
    private boolean isSpeler1AanZet = true;
    private int speler1Coins;
    private int speler2Coins;
    private final String speler1Naam;
    private final String speler2Naam;
    private boolean placementPhase = true;
    private int placementTurns = 0;
    private List<Tile> tiles;
    private List<Effect> allEffects;
    private Power selectedPower = null;


    public GameState(String speler1Naam, String speler2Naam, int startCoins, List<Tile> tiles) {
        this.speler1Naam = speler1Naam;
        this.speler2Naam = speler2Naam;
        this.speler1Coins = startCoins;
        this.speler2Coins = startCoins;
        this.tiles = tiles;
        this.allEffects = new EffectParser().parseEffects();
    }


    public boolean isSpeler1AanZet() {
        return isSpeler1AanZet;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void switchPlayer() {
        isSpeler1AanZet = !isSpeler1AanZet;
        placementTurns+=1;

        if (placementPhase && placementTurns >= 2) {
            endPlacementPhase();
        }
    }

    public int getCurrentCoins() {
        return isSpeler1AanZet ? speler1Coins : speler2Coins;
    }

    public void deductCoins(int amount) {
        if(isSpeler1AanZet) speler1Coins -= amount;
        else speler2Coins -= amount;
    }

    public Minion getSelectedMinion() {
        return selectedMinion;
    }

    public void setSelectedMinion(Minion minion) {
        this.selectedMinion = minion;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }
    public void setSelectedTile(Tile tile) {
        this.selectedTile = tile;
    }

    public String getCurrentPlayerName() {
        return isSpeler1AanZet ? speler1Naam : speler2Naam;
    }

    public boolean isOccupied(Tile tile) {
        return speler1Minions.containsKey(tile) || speler2Minions.containsKey(tile);
    }

    public void placeMinion(Tile tile, Minion minion) {
        if (isSpeler1AanZet) {
            speler1Minions.put(tile, minion);
        } else {
            speler2Minions.put(tile, minion);
        }
    }

    public boolean canAffordMinion(Minion minion) {
        return minion.getCost() <= getCurrentCoins();
    }

    public void removeMinion(Tile tile) {
        speler1Minions.remove(tile);
        speler2Minions.remove(tile);
    }

    public Minion getPlacedMinion(Tile tile) {
        if (speler1Minions.containsKey(tile)) {
            return speler1Minions.get(tile);
        } else {
            return speler2Minions.get(tile);
        }
    }

    public boolean isPlacementPhase() {
        return placementPhase;
    }

    public void endPlacementPhase() {
        placementPhase = false;
    }

    public boolean isValidPlacement(Tile tile) {
        if (isOccupied(tile)) return false;
        if (!List.of("dirt", "forest", "mountains").contains(tile.getType())) return false;
        if (getSelectedMinion().getCost() > getCurrentCoins()) return false;

        if (isPlacementPhase()) {
            return (isSpeler1AanZet ? tile.getHomebase() == 1 : tile.getHomebase() == 2);
        }

        return true;
    }
    public void refundCoins(int amount) {
        if (isSpeler1AanZet) {
            speler1Coins += amount;
        } else {
            speler2Coins += amount;
        }
    }

    public boolean isMinionOwnedByCurrentPlayer(Tile tile) {
        if (isSpeler1AanZet) {
            return speler1Minions.containsKey(tile);
        } else {
            return speler2Minions.containsKey(tile);
        }
    }

    public String getWinnaar() {
        if (speler1Minions.isEmpty()) return speler2Naam;
        if (speler2Minions.isEmpty()) return speler1Naam;
        return null;
    }

    public Effect findEffectByName(String effectName) {
        for (Effect effect : allEffects) {
            if (effect.getType().equalsIgnoreCase(effectName)) {
                return effect;
            }
        }
        return null;
    }

    public Power getSelectedPower() {
        return selectedPower;
    }
    public void setSelectedPower(Power selectedPower) {
        this.selectedPower = selectedPower;
    }
}