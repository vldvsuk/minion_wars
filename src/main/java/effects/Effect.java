package effects;

public class Effect {
    private String type;
    private int duration;
    private int value;

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