package models;

import grond.Tile;
import models.Minion;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameState {
    private Map<Tile, Minion> placedMinions = new HashMap<>();
    private Minion selectedMinion;
    private Tile selectedTile;
    private boolean isSpeler1AanZet = true;
    private int speler1Coins;
    private int speler2Coins;
    private String speler1Naam;
    private String speler2Naam;

    public GameState(String speler1Naam, String speler2Naam, int startCoins) {
        this.speler1Naam = speler1Naam;
        this.speler2Naam = speler2Naam;
        this.speler1Coins = startCoins;
        this.speler2Coins = startCoins;
    }


    public boolean isSpeler1AanZet() {
        return isSpeler1AanZet;
    }

    public void setStartCoins(int coins) {
        this.speler1Coins = coins;
        this.speler2Coins = coins;
    }

    public void switchPlayer() {
        isSpeler1AanZet = !isSpeler1AanZet;
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
        return placedMinions.containsKey(tile);
    }

    public void placeMinion(Tile tile, Minion minion) {
        placedMinions.put(tile, minion);
    }

    public boolean canAffordMinion(Minion minion) {
        return minion.getCost() <= getCurrentCoins();
    }

    public void removeMinion(Tile tile) {
        placedMinions.remove(tile);
    }

    public boolean shouldShowMinion(Tile tile) {
        return placedMinions.containsKey(tile) &&
                (isSpeler1AanZet ? tile.getHomebase() == 1 : tile.getHomebase() == 2);
    }

    public boolean isValidPlacement(Tile tile) {
        return!isOccupied(tile) &&
                List.of("dirt", "forest", "mountains").contains(tile.getType()) &&
                (isSpeler1AanZet ? tile.getHomebase() == 1 : tile.getHomebase() == 2) &&
                getSelectedMinion().getCost() <= getCurrentCoins();

    }
}
