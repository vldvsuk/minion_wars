package parsers;

import effects.Effect;
import effects.EffectFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EffectParser {

    public List<Effect> parseEffects() {
        List<Effect> effects = new ArrayList<>();

        try {
            InputStream inputStream = getClass().getResourceAsStream("/be/ugent/objprog/minionwars/configs/game.xml");

            if (inputStream == null) {
                throw new FileNotFoundException("Bestand niet gevonden: /configs/game.xml");
            }

            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputStream);
            Element rootElement = document.getRootElement();
            Element effectsElement = rootElement.getChild("effects");
            List<Element> effectElements = effectsElement.getChildren();

            for (Element effectElement : effectElements) {
                String type = effectElement.getName();
                int duration = Integer.parseInt(effectElement.getAttributeValue("duration"));
                int value = Integer.parseInt(effectElement.getAttributeValue("value", "0")); // Standaardwaarde is 0

                Effect effect = EffectFactory.createEffect(type, duration, value);
                effects.add(effect);
            }

        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }

        return effects;
    }
}