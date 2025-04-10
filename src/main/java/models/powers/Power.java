package models.powers;

public class Power {
    private final String type;
    private final int radius;
    private final int value;
    private final String name;

    public Power(String type, String name, int radius, int value) {
        this.type = type;
        this.radius = radius;
        this.value = value;
        this.name = name;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "type='" + type + '\'' +
                ", radius=" + radius +
                ", value=" + value + "}";
    }
}
