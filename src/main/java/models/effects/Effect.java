package models.effects;

public class Effect {
    private final String type;
    private final String name;
    private int duration;
    private final int value;

    public Effect(String type, String name, int duration, int value) {
        this.type = type;
        this.duration = duration;
        this.value = value;
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void verminderDuration(){
        duration -= 1;
    }

    public int getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
    public String getName() {
        return name;
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