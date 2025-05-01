package models.powers;
/**Verschillende klassen die worden niet gebruikt, staan nog omdat ik wil deze project nog later uitbreiden**/
public class Heal extends Power {
    public Heal(String name, int radius, int value, String effect, int effectValue) {
        super("healing", name, radius, value, effect, effectValue); // healing omdat de image staat met de naam healing
    }
}
