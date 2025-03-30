package models;


public class Minion {
    private String type;
    private String name;
    private int cost;
    private int movement;
    private String range;
    private int attack;
    private int defence;

    // Constructor voor alle minions
    public Minion(String type, String name, int cost, int movement, String range, int attack, int defence) {
        this.type = type;
        this.name = name;
        this.cost = cost;
        this.movement = movement;
        this.range = range;
        this.attack = attack;
        this.defence = defence;
    }

    // Getters

    public String getType() {
        return type;
    }
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", movement=" + movement +
                ", range='" + range + '\'' +
                ", attack=" + attack +
                ", defence=" + defence +
                '}';
    }
}