package models.powers;


/** Hoofd Power klasse**/
public class Power {
    private final String type;      // Type power (bijv. attack, heal, buff)
    private final int radius;       // Bereik waarin de power actief is
    private final int value;        // Basiskracht/waarde van de power
    private final String name;      // Weergavenaam van de power
    private final String effect;    // Type effect (bijv. burn, poison, rage)
    private final int effectValue;  // Intensiteit van het effect

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
