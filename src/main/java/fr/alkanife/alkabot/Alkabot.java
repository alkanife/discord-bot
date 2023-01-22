package fr.alkanife.alkabot;

import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.configuration.ConfigurationParser;
import fr.alkanife.alkabot.configuration.json.JSONConfiguration;
import fr.alkanife.alkabot.configuration.ConfigurationLoader;
import fr.alkanife.alkabot.configuration.tokens.Tokens;
import fr.alkanife.alkabot.configuration.tokens.TokensLoader;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.lang.TranslationsManager;
import fr.alkanife.alkabot.listener.ListenerManager;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.music.shortcut.ShortcutManager;
import fr.alkanife.alkabot.notification.NotificationManager;
import fr.alkanife.alkabot.utils.AlkabotUtils;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alkabot {

    public static final String VERSION = "2.0.0-dev2";
    public static final String WEBSITE = "https://github.com/alkanife/alkabot";

    private static boolean debug = false;
    private static String tokensFilePath = "tokens.json";
    private static String configurationFilePath = "configuration.json";
    private static Logger logger;
    private static Tokens tokens;
    private static boolean spotifySupport = true;
    private static JSONConfiguration configuration;
    private static TextChannel welcomeMessageChannel;
    private static Role autoRole;
    private static JDA jda;
    private static Guild guild;

    private static CommandManager commandManager;
    private static MusicManager musicManager;
    private static ShortcutManager shortcutManager;
    private static NotificationManager notificationManager;
    private static ListenerManager listenerManager;
    private static TranslationsManager translationsManager;

    public static void main(String[] args) {
        try {
            // Reading arguments
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("help")) {
                    System.out.println("This is Alkabot version " + VERSION);
                    System.out.println("""
                            Usage:
                              java -jar alkabot.jar [help]
                              java -jar alkabot.jar [debug/prod] [tokens file path] [configuration file path]""");
                    System.out.println("Default args: prod tokens.json configuration.json");
                    System.out.println("For more details go to " + WEBSITE);
                    return;
                }

                if (args[0].equalsIgnoreCase("debug"))
                    debug = true;

                if (args.length >= 2) {
                    tokensFilePath = args[1];

                    if (args.length >= 3)
                        configurationFilePath = args[2];
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
                System.out.println("""
                        ***                                                                                ***
                        *** THIS VERSION IS A DEV BUILD AND SHOULD NOT BE USED IN A PRODUCTION ENVIRONMENT ***
                        ***                                                                                ***""");

            Thread.sleep(2000);

            debug("Debug override");

            // Moving old latest.log file
            AlkabotUtils.cleanLogs();

            debug("Creating logger");
            logger = LoggerFactory.getLogger(Alkabot.class);

            // Initializing tokens
            TokensLoader tokensLoader = new TokensLoader();
            tokens = tokensLoader.getTokens();

            if (tokensLoader.getTokens() == null)
                return;

            if (tokensLoader.getTokens().getDiscord_token() == null)
                return;

            if (tokensLoader.getTokens().getSpotify() == null) {
                logger.info("Disabling spotify support");
                spotifySupport = false;
            }

            if (StringUtils.isNull(tokensLoader.getTokens().getSpotify().getClient_id())
                    || StringUtils.isNull(tokensLoader.getTokens().getSpotify().getClient_secret())) {
                logger.info("Disabling spotify support because there is no client_id or client_secret");
                spotifySupport = false;
            }

            // Initializing configuration
            ConfigurationLoader configurationLoader = new ConfigurationLoader(false);

            if (configurationLoader.getConfiguration() == null)
                return;

            configuration = configurationLoader.getConfiguration();

            ConfigurationParser configurationParser = new ConfigurationParser(false);

            if (configurationParser.getStatus() == ConfigurationParser.Status.FAIL)
                return;

            // Initializing translations
            TranslationsLoader translationsLoader = new TranslationsLoader(false);

            if (translationsLoader.getTranslations() == null)
                return;

            translationsManager = new TranslationsManager(translationsLoader);

            // Initializing commands
            // Always initialize command AFTER parsing the configuration
            commandManager = new CommandManager();
            commandManager.initialize();

            logger.info(commandManager.getCommands().size() + " commands ready");

            // Initializing music manager
            musicManager = new MusicManager();

            // Initializing Notification manager
            notificationManager = new NotificationManager();

            // Initializing shortcuts
            shortcutManager = new ShortcutManager();
            shortcutManager.read();

            // Initializing Listener Manager
            listenerManager = new ListenerManager();

            // Building JDA
            logger.info("Building JDA...");

            JDABuilder jdaBuilder = JDABuilder.createDefault(tokens.getDiscord_token());
            jdaBuilder.setRawEventsEnabled(true);
            jdaBuilder.setStatus(OnlineStatus.valueOf(configuration.getGuild().getPresence().getStatus()));
            if (configuration.getGuild().getPresence().getActivity().isShow())
                jdaBuilder.setActivity(AlkabotUtils.buildActivity());

            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT);
            jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);

            listenerManager.initialize(jdaBuilder);

            logger.info("Starting JDA");
            jda = jdaBuilder.build();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static boolean isDebugging() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Alkabot.debug = debug;
    }

    public static String getTokensFilePath() {
        return tokensFilePath;
    }

    public static void setTokensFilePath(String tokensFilePath) {
        Alkabot.tokensFilePath = tokensFilePath;
    }

    public static String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public static void setConfigurationFilePath(String configurationFilePath) {
        Alkabot.configurationFilePath = configurationFilePath;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        Alkabot.logger = logger;
    }

    public static Tokens getTokens() {
        return tokens;
    }

    public static void setTokens(Tokens tokens) {
        Alkabot.tokens = tokens;
    }

    public static boolean supportSpotify() {
        return spotifySupport;
    }

    public static void setSpotifySupport(boolean spotifySupport) {
        Alkabot.spotifySupport = spotifySupport;
    }

    public static JSONConfiguration getConfig() {
        return configuration;
    }

    public static void setConfiguration(JSONConfiguration configuration) {
        Alkabot.configuration = configuration;
    }

    public static TextChannel getWelcomeMessageChannel() {
        return welcomeMessageChannel;
    }

    public static void setWelcomeMessageChannel(TextChannel welcomeMessageChannel) {
        Alkabot.welcomeMessageChannel = welcomeMessageChannel;
    }

    public static Role getAutoRole() {
        return autoRole;
    }

    public static void setAutoRole(Role autoRole) {
        Alkabot.autoRole = autoRole;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static void setCommandManager(CommandManager commandManager) {
        Alkabot.commandManager = commandManager;
    }

    public static TranslationsManager getTranslationsManager() {
        return translationsManager;
    }

    public static void setTranslationsManager(TranslationsManager translationsManager) {
        Alkabot.translationsManager = translationsManager;
    }

    public static JDA getJda() {
        return jda;
    }

    public static void setJda(JDA jda) {
        Alkabot.jda = jda;
    }

    public static Guild getGuild() {
        return guild;
    }

    public static void setGuild(Guild guild) {
        Alkabot.guild = guild;
    }

    public static MusicManager getMusicManager() {
        return musicManager;
    }

    public static void setMusicManager(MusicManager musicManager) {
        Alkabot.musicManager = musicManager;
    }

    public static ShortcutManager getShortcutManager() {
        return shortcutManager;
    }

    public static void setShortcutManager(ShortcutManager shortcutManager) {
        Alkabot.shortcutManager = shortcutManager;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static void setNotificationManager(NotificationManager notificationManager) {
        Alkabot.notificationManager = notificationManager;
    }

    public static ListenerManager getListenerManager() {
        return listenerManager;
    }

    public static void setListenerManager(ListenerManager listenerManager) {
        Alkabot.listenerManager = listenerManager;
    }

    public static void debug(String s) {
        if (debug)
            if (logger == null)
                System.out.println("[Debug] " + s);
            else
                logger.info("* " + s);
    }

    public static void shutdown() {
        Alkabot.getCommandManager().getTerminalCommandHandler().setRunning(false);
        Alkabot.getCommandManager().getTerminalCommandHandlerThread().interrupt();
        jda.shutdown();
        System.exit(0);
    }

    public static String t(String key, String... values) {
        return getTranslationsManager().t(key, values);
    }

    public static String tr(String key, String... values) {
        return getTranslationsManager().tr(key, values);
    }

    public static String tri(String key, String... values) {
        return getTranslationsManager().tri(key, values);
    }
}
