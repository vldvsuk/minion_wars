package powers;

public class Power {
    private String type;
    private int radius;
    private int value;
    private String effect;
    private int effectValue;

    public Power(String type, int radius, int value, String effect, int effectValue) {
        this.type = type;
        this.radius = radius;
        this.value = value;
        this.effect = effect;
        this.effectValue = effectValue;
    }

    // Getters en setters
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "type='" + type + '\'' +
                ", radius=" + radius +
                ", value=" + value +
                ", effect='" + effect + '\'' +
                ", effectValue=" + effectValue +
                '}';
    }
}
