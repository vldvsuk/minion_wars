package models.minions;


public class Minion {
    private final String type;
    private final String name;
    private final int cost;
    private final int movement;
    private final String range;
    private final int attack;
    private final int defence;
    private int currentDefence;

    // Constructor voor alle minions
    public Minion(String type, String name, int cost, int movement, String range, int attack, int defence) {
        this.type = type;
        this.name = name;
        this.cost = cost;
        this.movement = movement;
        this.range = range;
        this.attack = attack;
        this.defence = defence;
        this.currentDefence = defence;
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

    public int getCurrentDefence() {
        return currentDefence;
    }

    public void verminderCurrentDefence(int getal) {
        currentDefence -= getal;

    }
    public void setCurrentDefence (int getal){
        currentDefence = getal;
    }

    public Minion copy() {
        return new Minion(
                this.type,
                this.name,
                this.cost,
                this.movement,
                this.range,
                this.attack,
                this.defence
        );
    }
}