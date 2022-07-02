package fr.alkanife.alkabot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import fr.alkanife.alkabot.commands.AdminCommands;
import fr.alkanife.alkabot.commands.InfoCommands;
import fr.alkanife.alkabot.commands.MusicCommands;
import fr.alkanife.alkabot.commands.UtilitiesCommands;
import fr.alkanife.alkabot.commands.utils.CommandHandler;
import fr.alkanife.alkabot.configuration.Configuration;
import fr.alkanife.alkabot.configuration.ConfigurationLoader;
import fr.alkanife.alkabot.events.Events;
import fr.alkanife.alkabot.events.LogEvents;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.music.TrackScheduler;
import fr.alkanife.alkabot.playlists.Playlist;
import fr.alkanife.alkabot.playlists.PlaylistsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Alkabot {

    private static boolean DEBUG = false;
    private static String ABSOLUTE_PATH = ""; // Path where the .jar is located
    private static Logger LOGGER;
    private static Configuration CONFIGURATION;
    private static CommandHandler COMMAND_HANDLER;
    private static HashMap<String, Object> TRANSLATIONS = new HashMap<>();
    private static JDA JDA;
    private static Guild GUILD;
    private static String VERSION = "1.0-SNAPSHOT";
    private static TextChannel LAST_COMMAND_CHANNEL;
    private static AudioPlayerManager AUDIO_PLAYER_MANAGER;
    private static AudioPlayer AUDIO_PLAYER;
    private static TrackScheduler TRACK_SCHEDULER;
    private static List<Playlist> PLAYLISTS = new ArrayList<>();
    private static PlaylistsManager PLAYLIST_MANAGER;

    public static void main(String[] args) {
        try {
            //
            // Checking advancedDebug argument
            //
            if (args.length > 0)
                if (args[0].equalsIgnoreCase("debug"))
                    DEBUG = true;

            //
            // Moving old 'latest.log' file to the logs/ folder
            //
            ABSOLUTE_PATH = Paths.get("").toAbsolutePath().toString();
            debug("ABSOLUTE_PATH: " + ABSOLUTE_PATH);

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

            //
            // Initializing configuration
            //
            ConfigurationLoader configurationLoader = new ConfigurationLoader(false);

            if (configurationLoader.getConfiguration() == null)
                return;

            CONFIGURATION = configurationLoader.getConfiguration();

            //
            // Initializing commands
            //
            debug("Setting up commands");

            COMMAND_HANDLER = new CommandHandler();
            getCommandHandler().registerCommands(new AdminCommands(), new MusicCommands(), new InfoCommands(), new UtilitiesCommands());

            getLogger().info(COMMAND_HANDLER.getCommands().size() + " commands ready");

            //
            // Initializing translations
            //
            debug("Reading translations");

            TranslationsLoader translationsLoader = new TranslationsLoader(false);

            if (translationsLoader.getTranslations() == null)
                return;

            TRANSLATIONS = translationsLoader.getTranslations();

            //
            // Initializing playlists
            //
            debug("Reading playlists");
            PLAYLIST_MANAGER = new PlaylistsManager();
            getPlaylistManager().read();

            //
            // Building JDA
            //
            getLogger().info("Building JDA...");

            JDABuilder jdaBuilder = JDABuilder.createDefault(getConfig().getToken());
            jdaBuilder.setRawEventsEnabled(true);
            jdaBuilder.setStatus(OnlineStatus.valueOf(getConfig().getPresence().getStatus()));
            if (getConfig().getPresence().getActivity().isShow()) {
                debug("Building activity");
                Activity.ActivityType activityType = Activity.ActivityType.valueOf(getConfig().getPresence().getActivity().getType());
                jdaBuilder.setActivity(Activity.of(activityType, getConfig().getPresence().getActivity().getText()));
            }

            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES);
            jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
            jdaBuilder.addEventListeners(new Events());
            jdaBuilder.addEventListeners(new LogEvents());

            getLogger().info("Starting JDA");
            JDA = jdaBuilder.build();
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
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

    public static Logger getLogger() {
        return LOGGER;
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

    public static TextChannel getLastCommandChannel() {
        return LAST_COMMAND_CHANNEL;
    }

    public static void setLastCommandChannel(TextChannel lastCommandChannel) {
        LAST_COMMAND_CHANNEL = lastCommandChannel;
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
                System.out.println("(debug) " + s);
            else
                getLogger().info("(debug) " + s);
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

    public static String limitString(String value, int length) {
        StringBuilder buf = new StringBuilder(value);
        if (buf.length() > length) {
            buf.setLength(length - 5);
            buf.append("`...`");
        }

        return buf.toString();
    }

    public static String musicDuration(long duration) {
        if (duration >= 3600000) {
            return String.format("%02d:%02d:%02d",  TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
        } else {
            return String.format("%02d:%02d",  TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
        }
    }

    public static boolean isURL(String s) {
        return s.toLowerCase(Locale.ROOT).startsWith("http");
    }
}
