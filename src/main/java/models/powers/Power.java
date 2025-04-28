package models.powers;

public class Power {
    private final String type;
    private final int radius;
    private final int value;
    private final String name;
    private final String effect;
    private final int effectValue;

    public Power(String type, String name, int radius, int value, String effect, int effectValue) {
        this.type = type;
        this.radius = radius;
        this.value = value;
        this.name = name;
        this.effect = effect;
        this.effectValue = effectValue;
    }

    // Getters en setters
    public String getType() {
        return type;
    }
    public int getRadius() {
        return radius;
    }
    public int getValue() {
        return value;
    }
    public String getName() {
        return name;
    }
    public String getEffect() {
        return effect;
    }
    public int getEffectValue() {
        return effectValue;
    }
    public boolean hasEffect(){
        return !effect.equals("none");
    }
}
