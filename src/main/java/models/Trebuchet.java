package models;

public class Trebuchet extends Minion {

    private String effect;
    private int effectValue;

    public Trebuchet(String type, String name, int cost, int movement, String range, int attack, int defence, String effect, int effectValue) {
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

    @Override
    public String toString() {
        return super.toString() + ", effect='" + effect + '\'' + ", effectValue=" + effectValue + '}';
    }
}
