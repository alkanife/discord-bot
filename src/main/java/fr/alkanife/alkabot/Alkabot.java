package fr.alkanife.alkabot;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.configuration.ConfigLoader;
import fr.alkanife.alkabot.configuration.json.Configuration;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.lang.TranslationsManager;
import fr.alkanife.alkabot.listener.ListenerManager;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.music.shortcut.ShortcutManager;
import fr.alkanife.alkabot.notification.NotificationManager;
import fr.alkanife.alkabot.tokens.TokenLoader;
import fr.alkanife.alkabot.tokens.Tokens;
import fr.alkanife.alkabot.utils.AlkabotUtils;
import fr.alkanife.alkabot.utils.tools.BuildReader;
import fr.alkanife.alkabot.utils.tools.LogsCleaner;
import lombok.Getter;
import lombok.Setter;
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

    @Getter
    private final String github = "https://github.com/alkanife/alkabot";
    @Getter @Setter
    private String version = "unknown";
    @Getter @Setter
    private boolean snapshotBuild = true;
    @Getter @Setter
    private String build = "unknown";

    @Getter
    private Parameters parameters;
    @Getter
    private Logger logger;
    @Getter @Setter
    private Tokens tokens;
    @Getter @Setter
    private boolean spotifySupport = true;
    @Getter @Setter
    private Configuration config;
    @Getter @Setter
    private TranslationsManager translationsManager;


    private static TextChannel welcomeMessageChannel;
    private static Role autoRole;
    private static JDA jda;
    private static Guild guild;

    private static CommandManager commandManager;
    private static MusicManager musicManager;
    private static ShortcutManager shortcutManager;
    private static NotificationManager notificationManager;
    private static ListenerManager listenerManager;

    public Alkabot(String[] args) {
        try {
            // Parse program arguments
            parameters = new Parameters();
            JCommander jCommander = JCommander.newBuilder().programName("alkabot").addObject(parameters).build();

            try {
                jCommander.parse(args);
            } catch (ParameterException exception) {
                System.out.println("Invalid arguments, see correct usage with '--help'");
                return;
            }

            verbose("Provided parameters: " + parameters.toString());

            if (parameters.isHelp()) {
                jCommander.usage();
                return;
            }

            // Read build information
            new BuildReader(this);

            // Splash text
            System.out.println("           _ _         _           _");
            System.out.println("     /\\   | | |       | |         | |");
            System.out.println("    /  \\  | | | ____ _| |__   ___ | |_");
            System.out.println("   / /\\ \\ | | |/ / _` | '_ \\ / _ \\| __|");
            System.out.println("  / ____ \\| |   < (_| | |_) | (_) | |_ ");
            System.out.println(" /_/    \\_\\_|_|\\_\\__,_|_.__/ \\___/ \\__|");
            System.out.println();
            System.out.println(" " + github);
            System.out.println(" Version " + getFullVersion());
            System.out.println();

            if (snapshotBuild)
                System.out.println("""
                        ***                                                                                ***
                        *** THIS VERSION IS A DEV BUILD AND SHOULD NOT BE USED IN A PRODUCTION ENVIRONMENT ***
                        ***                                                                                ***
                        """);

            Thread.sleep(2000);

            // Moving old latest.log file
            new LogsCleaner(this);

            // Create logger
            verbose("Creating logger");
            logger = LoggerFactory.getLogger(Alkabot.class);

            // Initializing tokens
            try {
                verbose("Loading tokens");
                new TokenLoader(this).load();
            } catch (Exception exception) {
                logger.error("Unable to load tokens, see error below");
                return;
            }

            if (tokens.getDiscordToken() == null) {
                logger.error("No Discord token provided!");
                return;
            }

            // Initializing configuration
            try {
                verbose("Loading config");
                new ConfigLoader(this).load();
            } catch (Exception exception) {
                logger.error("Unable to load configuration, see error below");
                return;
            }

            // Initializing translations
            try {
                verbose("Loading translations");
                translationsManager = new TranslationsManager(this);
                new TranslationsLoader(this).load();
            } catch (Exception exception) {
                logger.error("Unable to load translations, see error below");
                return;
            }

            



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

    public String getFullVersion() {
        return version + " (" + build + ")";
    }

    public void verbose(String s) {
        if (parameters.isVerbose())
            if (logger == null)
                System.out.println("[verbose] " + s);
            else
                logger.info("[verbose] " + s);
    }

    public static void shutdown() {
        Alkabot.getCommandManager().getTerminalCommandHandler().setRunning(false);
        Alkabot.getCommandManager().getTerminalCommandHandlerThread().interrupt();
        jda.shutdown();
        System.exit(0);
    }

    // Shortcuts for translations
    public String t(String key, String... values) {
        return getTranslationsManager().translate(key, values);
    }

    public String tr(String key, String... values) {
        return getTranslationsManager().translateRandom(key, values);
    }

    public String tri(String key, String... values) {
        return getTranslationsManager().translateRandomImage(key, values);
    }
}
