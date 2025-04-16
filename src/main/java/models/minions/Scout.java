package models.minions;


public class Scout extends Minion {

    private final String effect;
    private final int effectValue;

    public Scout(String type, String name, int cost, int movement, String range, int attack, int defence, String effect, int effectValue) {
        super(type, name, cost, movement, range, attack, defence);

        this.effect = effect;
        this.effectValue = effectValue;
    }

    public String getEffect() {
        return effect;
    }

    public int getEffectValue() {
        return effectValue;
    }
    @Override
    public Minion copy() {
        return new Scout(
                this.getType(),
                this.getName(),
                this.getCost(),
                this.getMovement(),
                this.getRange(),
                this.getAttack(),
                this.getDefence(),
                this.effect,
                this.effectValue
        );
    }
}
