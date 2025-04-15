package models.minions;

public class Spear extends Minion {
    private final String effect;
    private final int effectValue;

    public Spear(String type, String name, int cost, int movement, String range, int attack, int defence, String effect, int effectValue) {
        super(type, name, cost, movement, range, attack, defence);
        this.effect = effect;
        this.effectValue = effectValue;
    }

    public String getEffect() {
        return effect;
    }

    public int getEffectValue() {
        return effectValue;
    }

}