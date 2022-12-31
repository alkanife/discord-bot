package fr.alkanife.alkabot.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.JSONConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigurationLoader {

    private JSONConfiguration configuration = null;

    public ConfigurationLoader(boolean reload) throws IOException {
        Alkabot.getLogger().info("Reading configuration..." + (reload ? " (reload)" : ""));

        File configurationFile;
        try {
            configurationFile = new File(Alkabot.getConfigurationFilePath());

            Alkabot.debug("Full configuration file path: " + configurationFile.getPath());

            if (!configurationFile.exists()) {
                Alkabot.getLogger().error("The configuration file was not found");
                return;
            }
        } catch (Exception exception) {
            Alkabot.getLogger().error("Invalid configuration file path");
            exception.printStackTrace();
            return;
        }

        String configurationFileContent;
        try {
            configurationFileContent = Files.readString(configurationFile.toPath());
        } catch (IOException exception) {
            Alkabot.getLogger().error("Failed to read the configuration file content!");
            exception.printStackTrace();
            return;
        }

        try {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .create();

            configuration = gson.fromJson(configurationFileContent, JSONConfiguration.class);
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to read the JSON of the configuration file");
            Alkabot.getLogger().error("File content: " + configurationFileContent);
            exception.printStackTrace();
        }
    }

    public JSONConfiguration getConfiguration() {
        return configuration;
    }
}
