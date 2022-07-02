package fr.alkanife.alkabot.configuration;

import com.google.gson.Gson;
import fr.alkanife.alkabot.Alkabot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigurationLoader {

    private Configuration configuration = null;

    public ConfigurationLoader(boolean reload) throws IOException {
        Alkabot.getLogger().info((reload ? "(RELOAD) " : "") + "Reading " + Alkabot.absolutePath() + "/configuration.json");

        File configurationFile = new File(Alkabot.absolutePath() + "/configuration.json");

        if (!configurationFile.exists()) {
            Alkabot.getLogger().error("Configuration file not found");
            return;
        }

        // Reading configuration file
        String configurationRaw = Files.readString(configurationFile.toPath());
        //Alkabot.debug("RAW CONFIGURATION: " + configurationRaw);

        Gson gson = new Gson();
        configuration = gson.fromJson(configurationRaw, Configuration.class);

        // If advanced debugging is on, ignore the configuration. Otherwise, follow the configuration
        if (!Alkabot.isDebugging())
            Alkabot.setDebugging(configuration.isDebug());

        Alkabot.debug("Debugging set to true");
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
