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