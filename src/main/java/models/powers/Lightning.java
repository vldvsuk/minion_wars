package models.powers;

public class Lightning extends Power {
    private String effect;
    private int effectValue;

    public Lightning(int radius, int value, String effect, int effectValue) {
        super("lightning", radius, value);
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
