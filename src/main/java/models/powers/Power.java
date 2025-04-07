package models.powers;

public class Power {
    private final String type;
    private final int radius;
    private final int value;

    public Power(String type, int radius, int value) {
        this.type = type;
        this.radius = radius;
        this.value = value;
    }

    // Getters en setters
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "type='" + type + '\'' +
                ", radius=" + radius +
                ", value=" + value + "}";
    }
}
