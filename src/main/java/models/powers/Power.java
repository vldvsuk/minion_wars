package models.powers;

public class Power {
    private String type;
    private int radius;
    private int value;

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
