package models;

public class MinionFactory {
    public static Minion createMinion(String type, String name, int cost, int movement, String range, int attack, int defence, String effect, int effectValue) {
        switch (type.toLowerCase()) {
            case "militia":
                return new Militia(name, cost, movement, range, attack, defence,effect, effectValue);
            case "spear":
                return new Spear(name, cost, movement, range, attack, defence, effect, effectValue);
            case "sword":
                return new Sword(name, cost, movement, range, attack, defence, effect, effectValue);
            case "axe":
                return new Axe(name, cost, movement, range, attack, defence, effect, effectValue);
            case "archer":
                return new Archer(name, cost, movement, range, attack, defence, effect, effectValue);
            case "scout":
                return new Scout(name, cost, movement, range, attack, defence, effect, effectValue);
            case "cavalry":
                return new Cavalry(name, cost, movement, range, attack, defence, effect, effectValue);
            case "mounted-archer":
                return new MountedArcher(name, cost, movement, range, attack, defence, effect, effectValue);
            case "heavy-cavalry":
                return new HeavyCavalry(name, cost, movement, range, attack, defence, effect, effectValue);
            case "catapult":
                return new Catapult(name, cost, movement, range, attack, defence, effect, effectValue);
            case "trebuchet":
                return new Trebuchet(name, cost, movement, range, attack, defence, effect, effectValue);
            default:
                throw new IllegalArgumentException("Onbekend minion-type: " + type);
        }
    }
}

