package powers;

public class PowerFactory {
    public static Power createPower(String type, int radius, int value, String effect, int effectValue) {
        switch (type.toLowerCase()) {
            case "fireball":
                return new Fireball(radius, value, effect, effectValue);
            case "lightning":
                return new Lightning(radius, value, effect, effectValue);
            case "heal":
                return new Heal(radius, value);
            default:
                throw new IllegalArgumentException("Onbekend power-type: " + type);
        }
    }
}