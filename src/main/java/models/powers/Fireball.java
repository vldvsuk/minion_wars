package models.powers;

public class Fireball extends Power {

    private final String effect;
    private final int effectValue;

    public Fireball(String type, String name, int radius, int value, String effect, int effectValue) {
        super(type, name, radius, value);
        this.effect = effect;
        this.effectValue = effectValue;
    }

    // Getters voor effect en effectValue
    public String getEffect() {
        return effect;
    }

    public int getEffectValue() {
        return effectValue;
    }

}
