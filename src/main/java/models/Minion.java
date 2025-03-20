package models;

public class Minion {
    private String name;
    private int cost;
    private int movement;
    private String range;
    private int attack;
    private int defence;
    private String effect;
    private int effectValue;

    // Constructor
    public Minion(String name, int cost, int movement, String range, int attack, int defence, String effect, int effectValue) {
        this.name = name;
        this.cost = cost;
        this.movement = movement;
        this.range = range;
        this.attack = attack;
        this.defence = defence;
        this.effect = effect;
        this.effectValue = effectValue;
    }

    // Getters en setters
    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getMovement() {
        return movement;
    }

    public String getRange() {
        return range;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }

    public String getEffect() {
        return effect;
    }

    public int getEffectValue() {
        return effectValue;
    }
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", movement=" + movement +
                ", range='" + range + '\'' +
                ", attack=" + attack +
                ", defence=" + defence +
                ", effect='" + effect + '\'' +
                ", effectValue=" + effectValue +
                '}';
    }
}