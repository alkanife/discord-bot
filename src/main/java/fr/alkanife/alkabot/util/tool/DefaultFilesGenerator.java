package fr.alkanife.alkabot.util.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.AutoRoleConfig;
import fr.alkanife.alkabot.configuration.json.Configuration;
import fr.alkanife.alkabot.configuration.json.MusicConfig;
import fr.alkanife.alkabot.configuration.json.WelcomeMessageConfig;
import fr.alkanife.alkabot.configuration.json.commands.*;
import fr.alkanife.alkabot.configuration.json.guild.GuildConfig;
import fr.alkanife.alkabot.configuration.json.guild.GuildPresenceActivityConfig;
import fr.alkanife.alkabot.configuration.json.guild.GuildPresenceConfig;
import fr.alkanife.alkabot.configuration.json.notifications.*;
import fr.alkanife.alkabot.music.data.MusicData;
import fr.alkanife.alkabot.token.Tokens;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class DefaultFilesGenerator {

    private final Alkabot alkabot;
    private final Gson gson;

    public DefaultFilesGenerator(Alkabot alkabot) {
        this.alkabot = alkabot;
        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

        System.out.println("Generating default files...");
        System.out.println("This operation will not replace already existing files.");

        try {
            generateTokens();
        } catch (Exception exception) {
            alkabot.printJavaError("Failed to generate tokens", exception);
            return;
        }

        try {
            generateConfig();
        } catch (Exception exception) {
            alkabot.printJavaError("Failed to generate configuration", exception);
            return;
        }

        if (!generateData())
            return;

        if (!generateLangs())
            return;

        System.out.println("Generation complete");
    }

    private void generateTokens() throws Exception {
        File file = new File(alkabot.getParameters().getTokensPath());

        if (file.exists()) {
            System.out.println("[1/4] Skipping tokens");
            return;
        }

        System.out.println("[1/4] Generating tokens");

        Tokens tokens = new Tokens(null, new Tokens.Spotify(null, null));

        alkabot.verbose(tokens.toString());

        Files.writeString(file.toPath(), gson.toJson(tokens, Tokens.class));
    }

    private void generateConfig() throws Exception {
        File file = new File(alkabot.getParameters().getConfigurationPath());

        if (file.exists()) {
            System.out.println("[2/4] Skipping configuration");
            return;
        }

        System.out.println("[2/4] Generating configuration");

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

        alkabot.verbose(configuration.toString());

        Files.writeString(file.toPath(), gson.toJson(configuration, Configuration.class));
    }

    private boolean generateData() {
        File file = new File(alkabot.getParameters().getDataPath());

        try {
            if (!file.exists()) {
                System.out.println("[3/4] Generating data folder");
                file.mkdirs();
            }
        } catch (Exception exception) {
            alkabot.printJavaError("Failed to generate data folder", exception);
            return false;
        }

        try {
            generateMusicData();
        } catch (Exception exception) {
            alkabot.printJavaError("Failed to generate music data", exception);
            return false;
        }

        return true;
    }

    private void generateMusicData() throws Exception {
        File music = new File(alkabot.getParameters().getDataPath() + "/music.json");

        if (music.exists()) {
            System.out.println("[3/4] Skipping music data");
            return;
        }

        System.out.println("[3/4] Generating music data");

        MusicData musicData = new MusicData(100, new ArrayList<>());

        alkabot.verbose(musicData.toString());

        Files.writeString(music.toPath(), gson.toJson(musicData, MusicData.class));
    }

    private boolean generateLangs() {
        File file = new File(alkabot.getParameters().getLangPath());

        try {
            if (!file.exists()) {
                System.out.println("[4/4] Generating lang folder");
                file.mkdirs();
            }
        } catch (Exception exception) {
            alkabot.printJavaError("Failed to generate lang folder", exception);
            return false;
        }

        String[] langs = {"en_US", "fr_FR"};

        for (String lang : langs) {
            try {
                generateLang(lang);
            } catch (Exception exception) {
                alkabot.printJavaError("Failed to export lang " + lang, exception);
                return false;
            }
        }

        return true;
    }

    private void generateLang(String lang) throws Exception {
        File file = new File(alkabot.getParameters().getLangPath() + "/" + lang + ".json");

        if (file.exists()) {
            System.out.println("[4/4] Skipping " + lang + ".json");
            return;
        }

        System.out.println("[4/4] Exporting " + lang + ".json");

        InputStream inputStream = getClass().getResourceAsStream("/lang/" + lang + ".json");

        if (inputStream == null)
            throw new NullPointerException("Invalid resource call");

        Files.copy(inputStream, file.toPath());
    }
}
