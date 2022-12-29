package fr.alkanife.alkabot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import fr.alkanife.alkabot.commands.AdminCommands;
import fr.alkanife.alkabot.commands.InfoCommands;
import fr.alkanife.alkabot.commands.UtilitiesCommands;
import fr.alkanife.alkabot.commands.music.PlayerCommands;
import fr.alkanife.alkabot.commands.music.PlaylistCommand;
import fr.alkanife.alkabot.commands.music.QueueCommand;
import fr.alkanife.alkabot.commands.utils.CommandHandler;
import fr.alkanife.alkabot.configuration.Configuration;
import fr.alkanife.alkabot.configuration.ConfigurationLoader;
import fr.alkanife.alkabot.configuration.tokens.Tokens;
import fr.alkanife.alkabot.configuration.tokens.TokensLoader;
import fr.alkanife.alkabot.events.Events;
import fr.alkanife.alkabot.events.LogEvents;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.music.TrackScheduler;
import fr.alkanife.alkabot.music.playlists.Playlist;
import fr.alkanife.alkabot.music.playlists.PlaylistsManager;
import fr.alkanife.alkabot.utils.AlkabotUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Alkabot {

    private static String VERSION = "1.3.beta2";
    private static String WEBSITE = "https://github.com/alkanife/alkabot";

    private static boolean DEBUG = false;
    private static String TOKENS_FILE_PATH;
    private static String CONFIGURATION_FILE_PATH;
    private static String ABSOLUTE_PATH;
    private static Logger LOGGER;
    private static Tokens TOKENS;
    private static Configuration CONFIGURATION;
    private static CommandHandler COMMAND_HANDLER;
    private static HashMap<String, Object> TRANSLATIONS = new HashMap<>();
    private static JDA JDA;
    private static Guild GUILD;
    private static MessageChannelUnion LAST_SLASH_PLAY_CHANNEL;
    private static AudioPlayerManager AUDIO_PLAYER_MANAGER;
    private static AudioPlayer AUDIO_PLAYER;
    private static TrackScheduler TRACK_SCHEDULER;
    private static List<Playlist> PLAYLISTS = new ArrayList<>();
    private static PlaylistsManager PLAYLIST_MANAGER;

    public static void main(String[] args) {
        try {
            // Reading arguments
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("help")) {
                    System.out.println("This is Alkabot version " + VERSION);
                    System.out.println("Usage:\n" +
                            "  java -jar alkabot.jar [help]\n" +
                            "  java -jar alkabot.jar [debug/prod] [tokens file path] [configuration file path]");
                    System.out.println("Default args: prod tokens.json configuration.json");
                    System.out.println("For more details go to " + WEBSITE);
                    return;
                }

                if (args[0].equalsIgnoreCase("debug"))
                    DEBUG = true;

                if (args.length >= 2) {
                    TOKENS_FILE_PATH = args[1];

                    if (args.length >= 3)
                        CONFIGURATION_FILE_PATH = args[2];
                }
            }

            // Splash text
            System.out.println("           _ _         _           _");
            System.out.println("     /\\   | | |       | |         | |");
            System.out.println("    /  \\  | | | ____ _| |__   ___ | |_");
            System.out.println("   / /\\ \\ | | |/ / _` | '_ \\ / _ \\| __|");
            System.out.println("  / ____ \\| |   < (_| | |_) | (_) | |_ ");
            System.out.println(" /_/    \\_\\_|_|\\_\\__,_|_.__/ \\___/ \\__|");
            System.out.println();
            System.out.println(" " + WEBSITE);
            System.out.println(" Version " + VERSION);
            System.out.println();

            if (AlkabotUtils.isDevBuild())
                System.out.println("***                                                                                ***\n" +
                        "*** THIS VERSION IS A DEV BUILD AND SHOULD NOT BE USED IN A PRODUCTION ENVIRONMENT ***\n" +
                        "***                                                                                ***");

            // Setting default path (this is the path where the .jar is located)
            ABSOLUTE_PATH = Paths.get("").toAbsolutePath().toString();
            TOKENS_FILE_PATH = ABSOLUTE_PATH + "/tokens.json";
            CONFIGURATION_FILE_PATH = ABSOLUTE_PATH = "configuration.json";

            // Output for debug
            debug("Debug override");
            debug("Absolute path: " + ABSOLUTE_PATH);
            debug("Tokens path: " + TOKENS_FILE_PATH);
            debug("Configuration path: " + CONFIGURATION_FILE_PATH);

            // Moving old latest.log file
            debug("Moving old 'latest.log' file to the logs/ folder");
            File latestLogs = new File(ABSOLUTE_PATH + "/latest.log");

            if (latestLogs.exists()) {
                debug("latest.log file existing");
                System.out.println("Cleaning logs...");

                File logsFolder = new File(ABSOLUTE_PATH + "/logs");

                if (logsFolder.exists()) {
                    debug("logs/ folder already existing");
                    if (!logsFolder.isDirectory()) {
                        System.out.println(ABSOLUTE_PATH + "/logs is not a directory");
                        return;
                    }
                } else {
                    System.out.println("No logs/ directory found, creating one");
                    logsFolder.mkdir();
                }

                String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                String newPath = ABSOLUTE_PATH + "/logs/before-" + date + ".log";

                debug("Moving latest.log file to " + newPath);
                Files.move(latestLogs.toPath(), Paths.get(newPath));
            } else {
                debug("No latest.log file");
            }

            debug("Creating logger");
            LOGGER = LoggerFactory.getLogger(Alkabot.class);

            // Initializing tokens
            TokensLoader tokensLoader = new TokensLoader();
            TOKENS = tokensLoader.getTokens();

            if (tokensLoader.getTokens() == null)
                return;

            if (tokensLoader.getTokens().getDiscord_token() == null)
                return;

            // Initializing configuration
            ConfigurationLoader configurationLoader = new ConfigurationLoader(false);

            if (configurationLoader.getConfiguration() == null)
                return;

            CONFIGURATION = configurationLoader.getConfiguration();

            // Initializing commands
            debug("Setting up commands");

            COMMAND_HANDLER = new CommandHandler();
            getCommandHandler().registerCommands(new AdminCommands(),
                    new PlayerCommands(),
                    new PlaylistCommand(),
                    new QueueCommand(),
                    new InfoCommands(),
                    new UtilitiesCommands());

            getLogger().info(COMMAND_HANDLER.getCommands().size() + " commands ready");

            // Initializing translations
            debug("Reading translations");

            TranslationsLoader translationsLoader = new TranslationsLoader(false);

            if (translationsLoader.getTranslations() == null)
                return;

            TRANSLATIONS = translationsLoader.getTranslations();

            // Initializing playlists
            debug("Reading playlists");
            PLAYLIST_MANAGER = new PlaylistsManager();
            getPlaylistManager().read();

            // Building JDA
            getLogger().info("Building JDA...");

            JDABuilder jdaBuilder = JDABuilder.createDefault(getTokens().getDiscord_token());
            jdaBuilder.setRawEventsEnabled(true);
            jdaBuilder.setStatus(OnlineStatus.valueOf(getConfig().getPresence().getStatus()));
            if (getConfig().getPresence().getActivity().isShow())
                jdaBuilder.setActivity(AlkabotUtils.buildActivity());

            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT);
            jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
            jdaBuilder.addEventListeners(new Events());
            jdaBuilder.addEventListeners(new LogEvents());

            getLogger().info("Starting JDA");
            JDA = jdaBuilder.build();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static boolean isDebugging() {
        return DEBUG;
    }

    public static void setDebugging(boolean debug) {
        DEBUG = debug;
    }

    public static String absolutePath() {
        return ABSOLUTE_PATH;
    }

    public static String getTokensFilePath() {
        return TOKENS_FILE_PATH;
    }

    public static String getConfigurationFilePath() {
        return CONFIGURATION_FILE_PATH;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Tokens getTokens() {
        return TOKENS;
    }

    public static void setTokens(Tokens tokens) {
        Alkabot.TOKENS = tokens;
    }

    public static Configuration getConfig() {
        return CONFIGURATION;
    }

    public static void setConfig(Configuration CONFIGURATION) {
        Alkabot.CONFIGURATION = CONFIGURATION;
    }

    public static CommandHandler getCommandHandler() {
        return COMMAND_HANDLER;
    }

    public static HashMap<String, Object> getTranslations() {
        return TRANSLATIONS;
    }

    public static void setTranslations(HashMap<String, Object> TRANSLATIONS) {
        Alkabot.TRANSLATIONS = TRANSLATIONS;
    }

    public static JDA getJDA() {
        return JDA;
    }

    public static void setGuild(Guild guild) {
        GUILD = guild;
    }

    public static Guild getGuild() {
        return GUILD;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static MessageChannelUnion getLastSlashPlayChannel() {
        return LAST_SLASH_PLAY_CHANNEL;
    }

    public static void setLastSlashPlayChannel(MessageChannelUnion lastSlashPlayChannel) {
        LAST_SLASH_PLAY_CHANNEL = lastSlashPlayChannel;
    }

    public static AudioPlayerManager getAudioPlayerManager() {
        return AUDIO_PLAYER_MANAGER;
    }

    public static void setAudioPlayerManager(AudioPlayerManager audioPlayerManager) {
        AUDIO_PLAYER_MANAGER = audioPlayerManager;
    }

    public static AudioPlayer getAudioPlayer() {
        return AUDIO_PLAYER;
    }

    public static void setAudioPlayer(AudioPlayer audioPlayer) {
        AUDIO_PLAYER = audioPlayer;
    }

    public static TrackScheduler getTrackScheduler() {
        return TRACK_SCHEDULER;
    }

    public static void setTrackScheduler(TrackScheduler trackScheduler) {
        TRACK_SCHEDULER = trackScheduler;
    }

    public static List<Playlist> getPlaylists() {
        return PLAYLISTS;
    }

    public static Playlist getPlaylist(String name) {
        Playlist pl = null;

        for (Playlist p : PLAYLISTS)
            if (p.getName().equalsIgnoreCase(name))
                pl = p;

        return pl;
    }

    public static void setPlaylists(List<Playlist> PLAYLISTS) {
        Alkabot.PLAYLISTS = PLAYLISTS;
    }

    public static PlaylistsManager getPlaylistManager() {
        return PLAYLIST_MANAGER;
    }

    public static void debug(String s) {
        if (DEBUG)
            if (getLogger() == null)
                System.out.println("[Debug] " + s);
            else
                getLogger().info("* " + s);
    }

    public static String t(String key, String... values) {
        if (TRANSLATIONS.containsKey(key)) {
            MessageFormat messageFormat = new MessageFormat(String.valueOf(TRANSLATIONS.get(key)));
            return messageFormat.format(values);
        } else return "{MISSING TRANSLATION @ " + key + "}";
    }

    public static void discordLog(MessageEmbed embed) {
        try {
            TextChannel textChannel = getGuild().getTextChannelById(getConfig().getLogs().getChannel_id());

            if (textChannel == null) {
                getLogger().warn("No log channel provided / failed to log embed title = " + embed.getTitle());
                getLogger().warn("Description: " + embed.getDescription());
                getLogger().warn("Disable logging or provide a valid channel id");
                return;
            }

            textChannel.sendMessageEmbeds(embed).queue();
        } catch (Exception exception) {
            getLogger().error("Failed to log embed");
            getLogger().error("Title " + embed.getTitle());
            getLogger().error("Description: " + embed.getDescription());
            exception.printStackTrace();
        }
    }
}
