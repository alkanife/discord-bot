package dev.alkanife.alkabot.util.tool;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alkanife.alkabot.Parameters;
import dev.alkanife.alkabot.configuration.json.AutoRoleConfig;
import dev.alkanife.alkabot.configuration.json.Configuration;
import dev.alkanife.alkabot.configuration.json.MusicConfig;
import dev.alkanife.alkabot.configuration.json.WelcomeMessageConfig;
import dev.alkanife.alkabot.configuration.json.commands.*;
import dev.alkanife.alkabot.configuration.json.guild.GuildConfig;
import dev.alkanife.alkabot.configuration.json.guild.GuildPresenceActivityConfig;
import dev.alkanife.alkabot.configuration.json.guild.GuildPresenceConfig;
import dev.alkanife.alkabot.configuration.json.notifications.*;
import dev.alkanife.alkabot.log.Logs;
import dev.alkanife.alkabot.music.data.MusicData;
import dev.alkanife.alkabot.token.Tokens;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class DefaultFilesGenerator {

    private final Parameters parameters;
    private final Logger logger;
    private final Gson gson;

    public DefaultFilesGenerator(Parameters parameters) {
        this.parameters = parameters;
        this.logger = Logs.createLogger(DefaultFilesGenerator.class);
        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

        logger.info("This operation will not replace already existing files.");

        try {
            generateTokens();
        } catch (Exception exception) {
            logger.error("Failed to generate tokens at path '" + parameters.getTokensPath() + "'", exception);
            return;
        }

        try {
            generateConfig();
        } catch (Exception exception) {
            logger.error("Failed to generate configuration at path '" + parameters.getConfigurationPath() + "'", exception);
            return;
        }

        if (!generateData())
            return;

        if (!generateLangs())
            return;

        logger.info("Generation complete");
    }

    private void generateTokens() throws Exception {
        File file = new File(parameters.getTokensPath());

        if (file.exists()) {
            logger.info("[1/4] Skipping tokens");
            return;
        }

        logger.info("[1/4] Generating tokens");

        Tokens tokens = new Tokens(null, new Tokens.Spotify(null, null));

        logger.debug(tokens.toString());

        Files.writeString(file.toPath(), gson.toJson(tokens, Tokens.class));
    }

    private void generateConfig() throws Exception {
        File file = new File(parameters.getConfigurationPath());

        if (file.exists()) {
            logger.info("[2/4] Skipping configuration");
            return;
        }

        logger.info("[2/4] Generating configuration");

        Configuration configuration = new Configuration();
        configuration.setLangFile("en_US");
        configuration.setAdminIds(new ArrayList<>());

        GuildConfig guildConfig = new GuildConfig(null, new GuildPresenceConfig(OnlineStatus.ONLINE.name(), new GuildPresenceActivityConfig(false, null, null)));
        configuration.setGuildConfig(guildConfig);

        configuration.setWelcomeMessageConfig(new WelcomeMessageConfig(false, null));
        configuration.setAutoRoleConfig(new AutoRoleConfig(false, null));
        configuration.setMusicConfig(new MusicConfig(true));

        // commands
        CommandConfig commandConfig = new CommandConfig();
        commandConfig.setAbout(true);

        MusicCommandConfig musicCommandConfig = new MusicCommandConfig(true, true, true, true, true, true, true, true, true, true, true, new ShortcutCommandConfig(true, true, true, true));
        commandConfig.setMusicCommandConfig(musicCommandConfig);

        commandConfig.setUtilsCommandConfig(new UtilsCommandConfig(new InfoUtilsCommandConfig(true, true, true), true));

        configuration.setCommandConfig(commandConfig);

        // notifications
        SelfNotifConfig selfNotifConfig = new SelfNotifConfig(null, true, true);
        MessageNotifConfig messageNotifConfig = new MessageNotifConfig(null, 30, true, true);
        MemberNotifConfig memberNotifConfig = new MemberNotifConfig(null, true, true);
        ModNotifConfig modNotifConfig = new ModNotifConfig(null, true, true, true, true, true, true, true, true, true);
        VoiceNotifConfig voiceNotifConfig = new VoiceNotifConfig(null, true, true, true);

        configuration.setNotifConfig(new NotifConfig(selfNotifConfig, messageNotifConfig, memberNotifConfig, modNotifConfig, voiceNotifConfig));

        logger.debug(configuration.toString());

        Files.writeString(file.toPath(), gson.toJson(configuration, Configuration.class));
    }

    private boolean generateData() {
        File file = new File(parameters.getDataPath());

        try {
            if (!file.exists()) {
                logger.info("[3/4] Generating data folder");
                file.mkdirs();
            }
        } catch (Exception exception) {
            logger.error("Failed to generate data folder at path '" + parameters.getDataPath() + "'", exception);
            return false;
        }

        try {
            generateMusicData();
        } catch (Exception exception) {
            logger.error("Failed to generate music data at path '" + parameters.getDataPath() + "'", exception);
            return false;
        }

        return true;
    }

    private void generateMusicData() throws Exception {
        File music = new File(parameters.getDataPath() + "/music.json");

        if (music.exists()) {
            logger.info("[3/4] Skipping music data");
            return;
        }

        logger.info("[3/4] Generating music data");

        MusicData musicData = new MusicData(100, new ArrayList<>());

        logger.debug(musicData.toString());

        Files.writeString(music.toPath(), gson.toJson(musicData, MusicData.class));
    }

    private boolean generateLangs() {
        File file = new File(parameters.getLangPath());

        try {
            if (!file.exists()) {
                logger.info("[4/4] Generating lang folder");
                file.mkdirs();
            }
        } catch (Exception exception) {
            logger.error("Failed to generate the lang folder at path '" + parameters.getLangPath() + "'", exception);
            return false;
        }

        String[] langs = {"en_US", "fr_FR"};

        for (String lang : langs) {
            try {
                generateLang(lang);
            } catch (Exception exception) {
                logger.error("Failed to export '" + lang + "' to the lang folder", exception);
                return false;
            }
        }

        return true;
    }

    private void generateLang(String lang) throws Exception {
        File file = new File(parameters.getLangPath() + "/" + lang + ".json");

        if (file.exists()) {
            logger.info("[4/4] Skipping " + lang + ".json");
            return;
        }

        logger.info("[4/4] Exporting " + lang + ".json");

        InputStream inputStream = getClass().getResourceAsStream("/lang/" + lang + ".json");

        if (inputStream == null)
            throw new NullPointerException("Invalid resource call");

        Files.copy(inputStream, file.toPath());
    }
}
