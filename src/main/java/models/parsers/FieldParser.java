package models.parsers;
import models.grond.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FieldParser {
    public List<Tile> parseField() {
        List<Tile> tiles = new ArrayList<>();

        try (InputStream inputStream = XmlLoader.loadXml()) {


            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputStream);

            Element rootElement = document.getRootElement();
            Element fieldElement = rootElement.getChild("field");
            List<Element> tileElements = fieldElement.getChildren();

            for (Element tileElement : tileElements) {
                String type = tileElement.getName();
                int x = Integer.parseInt(tileElement.getAttributeValue("x"));
                int y = Integer.parseInt(tileElement.getAttributeValue("y"));
                int homebase = Integer.parseInt(tileElement.getAttributeValue("homebase", "0"));

                Tile tile = createTile(type, x, y, homebase);
                tiles.add(tile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tiles;
    }

    private Tile createTile(String type, int x, int y, int homebase) {
        return switch (type.toLowerCase()) {
            case "dirt" -> new DirtTile(x, y, homebase);
            case "forest" -> new ForestTile(x, y, homebase);
            case "water" -> new WaterTile(x, y, homebase);
            case "mountains" -> new MountainTile(x, y, homebase);
            default -> throw new IllegalArgumentException("Onbekend tile-type: " + type);
        };
    }
}
