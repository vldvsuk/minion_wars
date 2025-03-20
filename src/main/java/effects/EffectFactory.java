package effects;

public class EffectFactory {
    public static Effect createEffect(String type, int duration, int value) {
        switch (type.toLowerCase()) {
            case "burn":
                return new Burn(duration, value);
            case "paralysis":
                return new Paralysis(duration);
            case "heal":
                return new HealEffect(duration, value);
            case "poison":
                return new Poison(duration, value);
            case "slow":
                return new Slow(duration, value);
            case "blindness":
                return new Blindness(duration, value);
            case "rage":
                return new Rage(duration, value);
            default:
                throw new IllegalArgumentException("Onbekend effect-type: " + type);
        }
    }
}
