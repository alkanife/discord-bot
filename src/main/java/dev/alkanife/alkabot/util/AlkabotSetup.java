package dev.alkanife.alkabot.util;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.configuration.ConfigManager;
import dev.alkanife.alkabot.data.DataManager;
import dev.alkanife.alkabot.lang.LangFilesManager;
import dev.alkanife.alkabot.secrets.SecretsManager;
import dev.alkanife.alkabot.util.timetracker.TimeTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AlkabotSetup {

    private final Alkabot alkabot;

    public AlkabotSetup(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public boolean setup() {
        TimeTracker.end("alkabot-setup-time");

        alkabot.getLogger().info("Alkabot version {}", alkabot.getFullVersion());

        alkabot.getLogger().info("--------------------------");
        alkabot.getLogger().info("If you already have done a setup or edited some files, take into consideration that this setup");
        alkabot.getLogger().info("will reset all configurations, secrets, data, and languages pack you already have.");
        alkabot.getLogger().info("Press Enter to continue or CTRL+C to abort");
        alkabot.getLogger().info("--------------------------");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean confirmed = false;

        try {
            String input = reader.readLine();
            if (input.isEmpty())
                confirmed = true;
            else
                alkabot.getLogger().info("Setup aborted");
        } catch (IOException exception) {
            alkabot.getLogger().error("Internal error while ready your input", exception);
            return false;
        }

        if (!confirmed)
            return true;

        alkabot.getLogger().info("Starting directory setup...");

        alkabot.getLogger().info("[1/4] Writing secrets template");
        if (!new SecretsManager(alkabot).setup())
            return false;

        ConfigManager configManager = new ConfigManager(alkabot);

        alkabot.getLogger().info("[2/4] Writing default configuration");
        if (!configManager.setup())
            return false;

        alkabot.setConfigManager(configManager);

        alkabot.getLogger().info("[3/4] Exporting language files");
        if (!new LangFilesManager(alkabot).exportDefaultLanguages())
            return false;

        alkabot.getLogger().info("[4/4] Setting up persistent storage");
        if (!new DataManager(alkabot).setupData())
            return false;

        TimeTracker.end("alkabot-setup-time");
        alkabot.getLogger().info("Setup done! Don't forget to write your secrets before starting the bot!");
        return true;
    }

}
