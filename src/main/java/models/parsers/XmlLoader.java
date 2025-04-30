package models.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XmlLoader {
    private static String externalConfigPath;
    private static final String DEFAULT_RESOURCE_PATH = "/be/ugent/objprog/minionwars/configs/game-big.xml";

    public static void setConfigPath(String path) {
        externalConfigPath = path;
    }

    public static InputStream loadXml() throws IOException {

        // 1. Probeer extern pad eerst
        if (externalConfigPath != null) {
            Path path = Paths.get(externalConfigPath);
            if (Files.exists(path)) {
                return Files.newInputStream(path);
            }
        }

        // 2. Fallback naar embedded resource
        InputStream stream = XmlLoader.class.getResourceAsStream(DEFAULT_RESOURCE_PATH);
        if (stream == null) {
            throw new IOException("XML niet gevonden in resources!");
        }
        return stream;
    }
}
