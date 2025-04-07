package models.minions;

public class Archer extends Minion {
    public Archer(String type ,String name, int cost, int movement, String range, int attack, int defence) {
        super(type, name, cost, movement, range, attack, defence);
    }

    public static class Sword extends Minion {
        public Sword(String type, String name, int cost, int movement, String range, int attack, int defence) {
            super(type, name, cost, movement, range, attack, defence);
        }
    }
}
