package models.powers;

public class Heal extends Power {
    public Heal(String type, String name, int radius, int value, String effect, int effectValue) {
        super("healing", name, radius, value, effect, effectValue);
    }
}
