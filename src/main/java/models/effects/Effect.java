package models.effects;

public class Effect {
    private final String type;
    private final int duration;
    private final int value;

    public Effect(String type, int duration, int value) {
        this.type = type;
        this.duration = duration;
        this.value = value;
    }

    public int getDuration() {
        return duration;
    }
    public int getValue() {
        return value;
    }
    public String getType() {
        return type;
    }

    // Getters en setters
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "type='" + type + '\'' +
                ", duration=" + duration +
                ", value=" + value +
                '}';
    }
}