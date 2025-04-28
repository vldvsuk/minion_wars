package models.minions;

public class Militia extends Minion {
    public Militia(String type, String name, int cost, int movement,int minRange, int maxRange, int attack, int defence, String effect, int effectvalue) {
        super(type, name, cost, movement, minRange, maxRange, attack, defence, effect, effectvalue);
    }
}