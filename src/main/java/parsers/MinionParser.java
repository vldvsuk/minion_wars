package parsers;

import models.Minion;
import models.MinionFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MinionParser {

    public List<Minion> parseMinions() {
        List<Minion> minions = new ArrayList<>();

        try {
            InputStream inputStream = getClass().getResourceAsStream("/be/ugent/objprog/minionwars/configs/game.xml");

            if (inputStream == null) {
                throw new FileNotFoundException("Bestand niet gevonden: game.xml");
            }

            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputStream);
            Element rootElement = document.getRootElement();
            Element minionsElement = rootElement.getChild("minions");
            List<Element> minionElements = minionsElement.getChildren();

            for (Element minionElement : minionElements) {
                String type = minionElement.getName();
                String name = minionElement.getAttributeValue("name");
                int cost = Integer.parseInt(minionElement.getAttributeValue("cost"));
                int movement = Integer.parseInt(minionElement.getAttributeValue("movement"));
                String range = minionElement.getAttributeValue("range");
                int attack = Integer.parseInt(minionElement.getAttributeValue("attack"));
                int defence = Integer.parseInt(minionElement.getAttributeValue("defence"));
                String effect = minionElement.getAttributeValue("effect", "none");
                int effectValue = Integer.parseInt(minionElement.getAttributeValue("effect-value", "0"));

                Minion minion = MinionFactory.createMinion(type, name, cost, movement, range, attack, defence, effect, effectValue);
                minions.add(minion);
            }

        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }

        return minions;
    }
}