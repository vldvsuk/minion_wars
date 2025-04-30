package models.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XmlLoader {

    private static final String CONFIG_DIR = System.getProperty("minionwars.config.dir", "config");
    private static final String DEFAULT_RESOURCE_PATH = "/be/ugent/objprog/minionwars/configs/";

    public static InputStream loadXml(String fileName) throws IOException {
        // Probeer eerst externe locatie
        Path externalPath = Paths.get(CONFIG_DIR, fileName);
        if (Files.exists(externalPath)) {
            return Files.newInputStream(externalPath);
        }

        // Fallback naar embedded resource
        InputStream resourceStream = XmlLoader.class.getResourceAsStream(DEFAULT_RESOURCE_PATH + fileName);
        if (resourceStream == null) {
            throw new IOException("XML-bestand niet gevonden in resources: " + fileName);
        }
        return resourceStream;
    }
}

