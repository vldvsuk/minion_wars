package models.powers;

public class Fireball extends Power {

    private String effect;
    private int effectValue;

    public Fireball(int radius, int value, String effect, int effectValue) {
        super("fireball", radius, value);
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
