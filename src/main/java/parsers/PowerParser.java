package parsers;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import models.powers.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PowerParser {

    public List<Power> parsePowers() {
        List<Power> powers = new ArrayList<>();

        try {
            InputStream inputStream = getClass().getResourceAsStream("/be/ugent/objprog/minionwars/configs/game.xml");

            if (inputStream == null) {
                throw new FileNotFoundException("Bestand niet gevonden: game.xml");
            }

            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputStream);
            Element rootElement = document.getRootElement();
            Element powersElement = rootElement.getChild("powers");
            List<Element> powerElements = powersElement.getChildren();

            for (Element powerElement : powerElements) {
                String type = powerElement.getName();
                int radius = Integer.parseInt(powerElement.getAttributeValue("radius"));
                int value = Integer.parseInt(powerElement.getAttributeValue("value"));
                String effect = powerElement.getAttributeValue("effect", "none");
                int effectValue = Integer.parseInt(powerElement.getAttributeValue("effect-value", "0"));

                Power power = createPower(type, radius, value, effect, effectValue);
                powers.add(power);
            }

        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }

        return powers;

    }

    public static Power createPower(String type, int radius, int value, String effect, int effectValue) {
        return switch (type.toLowerCase()) {
            case "fireball" -> new Fireball(radius, value, effect, effectValue);
            case "lightning" -> new Lightning(radius, value, effect, effectValue);
            case "heal" -> new Heal(radius, value);
            default -> throw new IllegalArgumentException("Onbekend power-type: " + type);
        };
    }
}