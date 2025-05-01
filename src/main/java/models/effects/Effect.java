package models.effects;


/** Hoofd effect klasse **/
public class Effect {
    private final String type; // type van de effect
    private final String name; // name van de effect
    private int duration;      // duration van de effect
    private final int value;   // value van de effect

    public Effect(String type, String name, int duration, int value) {
        this.type = type;
        this.duration = duration;
        this.value = value;
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void verminderDuration(){ // vermindering duration na elke spelers beurt
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