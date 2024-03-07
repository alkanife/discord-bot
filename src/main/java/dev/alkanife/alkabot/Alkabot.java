package dev.alkanife.alkabot;

import ch.qos.logback.classic.Logger;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import dev.alkanife.alkabot.cli.CLIArguments;
import dev.alkanife.alkabot.cli.UsageFormatter;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.configuration.ConfigManager;
import dev.alkanife.alkabot.configuration.json.AlkabotConfig;
import dev.alkanife.alkabot.discord.AutoroleManager;
import dev.alkanife.alkabot.discord.GuildManager;
import dev.alkanife.alkabot.discord.WelcomeMessageManager;
import dev.alkanife.alkabot.lang.LangFilesManager;
import dev.alkanife.alkabot.discord.event.EventListenerManager;
import dev.alkanife.alkabot.music.MusicManager;
import dev.alkanife.alkabot.notification.NotificationManager;
import dev.alkanife.alkabot.secrets.SecretsManager;
import dev.alkanife.alkabot.secrets.json.Secrets;
import dev.alkanife.alkabot.secrets.json.SpotifySecrets;
import dev.alkanife.alkabot.util.Logs;
import dev.alkanife.alkabot.util.StringUtils;
import dev.alkanife.alkabot.util.BuildReader;
import dev.alkanife.alkabot.util.timetracker.TimeTracker;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.File;

public class Alkabot {

    @Getter
    private static Alkabot instance;

    @Getter
    private final String github = "https://github.com/alkanife/alkabot";
    @Getter @Setter
    private String version = "unknown";
    @Getter @Setter
    private boolean snapshotBuild = false;
    @Getter @Setter
    private String build = "unknown";

    @Getter
    private CLIArguments args;
    @Getter
    private Logger logger;
    @Getter @Setter
    private boolean spotifySupport = false;

    @Getter @Setter
    private SecretsManager secretsManager;
    @Getter @Setter
    private ConfigManager configManager;
    @Getter @Setter
    private LangFilesManager langFilesManager;
    @Getter
    private EventListenerManager eventListenerManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private MusicManager musicManager;
    @Getter
    private NotificationManager notificationManager;

    @Getter
    private JDA jda;
    @Getter
    private GuildManager guildManager;
    @Getter
    private WelcomeMessageManager welcomeMessageManager;
    @Getter
    private AutoroleManager autoroleManager;

    public Alkabot(String[] args) {
        instance = this;

        // Parse program arguments
        this.args = new CLIArguments();
        JCommander jCommander = JCommander
                .newBuilder()
                .programName("alkabot")
                .addObject(this.args)
                .build();

        try {
            jCommander.parse(args);
        } catch (ParameterException exception) {
            System.out.println("Invalid arguments, see correct usage with '-help'");
            return;
        }

        if (getArgs().isHelp()) {
            new UsageFormatter(jCommander).printUsage();
            return;
        }

        new BuildReader(this);

        if (getArgs().isVersion()) {
            System.out.println("Alkabot version " + getVersion() + " (" + getBuild() + ")");
            System.out.println(getGithub());
            return;
        }

        // Setup logger
        try {
            Logs.setupRootLogger(getArgs());
            logger = Logs.createLogger(getArgs(), Alkabot.class);
        } catch (Exception exception) {
            System.out.println("An error occurred while creating the logger.\nIf you have modified the logger configuration, please check the options used and the error below.\n");
            exception.printStackTrace();
            return;
        }

        try {
            TimeTracker.isTracking = getArgs().isTrackTime();
            TimeTracker.start("total-load-time");

            splashText();

            if (!load())
                return;

            createAndBuildJDA();
        } catch (Exception exception) {
            logger.error("Fatal: an unexpected error prevented the bot to start", exception);
        }
    }

    private void splashText() {
        logger.debug("DEBUG MODE ENABLED");
        logger.debug("Please do not use the debug mode in production.");
        logger.debug("---------");
        logger.debug("Arguments: " + getArgs().toString());

        logger.info("           _ _         _           _");
        logger.info("     /\\   | | |       | |         | |");
        logger.info("    /  \\  | | | ____ _| |__   ___ | |_");
        logger.info("   / /\\ \\ | | |/ / _` | '_ \\ / _ \\| __|");
        logger.info("  / ____ \\| |   < (_| | |_) | (_) | |_ ");
        logger.info(" /_/    \\_\\_|_|\\_\\__,_|_.__/ \\___/ \\__|");
        logger.info(" ");
        logger.info(" " + github);
        logger.info(" Version " + getFullVersion());
        logger.info(" ");

        if (snapshotBuild)
            logger.warn("This version of Alkabot is an experiment, bugs or breaking errors may occur!");

        if (version.equals("unknown"))
            logger.warn("Unable to find the current Alkabot version, be careful...");
    }

    private boolean load() {
        TimeTracker.start("alkabot-load-time");

        logger.info("Loading modules...");

        secretsManager = new SecretsManager(this);
        configManager = new ConfigManager(this);

        if (!secretsManager.load() || !configManager.load())
            return false;

        langFilesManager = new LangFilesManager(this);

        if (!langFilesManager.load())
            return false;

        eventListenerManager = new EventListenerManager(this);
        commandManager = new CommandManager(this);
        musicManager = new MusicManager(this);
        notificationManager = new NotificationManager(this);
        guildManager = new GuildManager(this);
        welcomeMessageManager = new WelcomeMessageManager(this);
        autoroleManager = new AutoroleManager(this);

        commandManager.load();
        notificationManager.load();

        TimeTracker.end("alkabot-load-time");
        return true;
    }

    private void createAndBuildJDA() {
        TimeTracker.start("jda-load-time");

        logger.debug("Creating JDA");

        JDABuilder jdaBuilder = JDABuilder.createDefault(getSecretsManager().getSecrets().getDiscordToken());
        jdaBuilder.setRawEventsEnabled(true);
        jdaBuilder.setStatus(OnlineStatus.valueOf(getConfig().getGuildConfig().getGuildPresenceConfig().getStatus()));
        if (getConfig().getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().isShowing())
            jdaBuilder.setActivity(buildActivity());

        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MODERATION,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);

        eventListenerManager.addEventListeners(jdaBuilder);

        logger.info("Connecting to Discord");
        jda = jdaBuilder.build();
    }

    public void shutdown() {
        commandManager.getTerminalCommandHandler().setRunning(false);
        commandManager.getTerminalCommandHandlerThread().interrupt();
        jda.shutdown();
        System.exit(0);
    }

    public String getFullVersion() {
        return version + " (" + build + ")";
    }

    public AlkabotConfig getConfig() {
        return configManager.getConfig();
    }

    public Guild getGuild() {
        return guildManager.getGuild();
    }

    public Activity buildActivity() {
        logger.debug("Building activity");

        Activity.ActivityType activityType = Activity.ActivityType.valueOf(getConfig().getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getType());
        return Activity.of(activityType, getConfig().getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getText());
    }
}
