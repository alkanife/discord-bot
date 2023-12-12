package dev.alkanife.alkabot;

import ch.qos.logback.classic.Logger;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.configuration.ConfigLoader;
import dev.alkanife.alkabot.configuration.json.Configuration;
import dev.alkanife.alkabot.lang.TranslationsLoader;
import dev.alkanife.alkabot.listener.ListenerManager;
import dev.alkanife.alkabot.log.Logs;
import dev.alkanife.alkabot.music.data.MusicData;
import dev.alkanife.alkabot.music.data.MusicDataLoader;
import dev.alkanife.alkabot.music.MusicManager;
import dev.alkanife.alkabot.music.data.Shortcut;
import dev.alkanife.alkabot.notification.NotificationManager;
import dev.alkanife.alkabot.token.TokenLoader;
import dev.alkanife.alkabot.token.Tokens;
import dev.alkanife.alkabot.util.tool.BuildReader;
import dev.alkanife.alkabot.util.tool.DefaultFilesGenerator;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
    private Parameters parameters;
    @Getter
    private Logger logger;
    @Getter @Setter
    private Tokens tokens;
    @Getter @Setter
    private boolean spotifySupport = true;
    @Getter @Setter
    private Configuration config;
    @Getter
    private CommandManager commandManager;
    @Getter @Setter
    private MusicManager musicManager;
    @Getter
    private NotificationManager notificationManager;
    @Getter @Setter
    private MusicData musicData;
    @Getter
    private ListenerManager listenerManager;
    @Getter
    private JDA jda;
    @Getter @Setter
    private TextChannel welcomeMessageChannel;
    @Getter @Setter
    private Role autoRole;
    @Getter @Setter
    private Guild guild;

    public Alkabot(String[] args) {
        instance = this;

        // Parse program arguments
        parameters = new Parameters();
        JCommander jCommander = JCommander.newBuilder().programName("alkabot").addObject(parameters).build();

        try {
            jCommander.parse(args);
        } catch (ParameterException exception) {
            System.out.println("Invalid arguments, see correct usage with '--help'");
            return;
        }

        if (parameters.isHelp()) {
            jCommander.usage();
            return;
        }

        if (parameters.isDebugAll()) {
            parameters.setDebug(true);
            parameters.setDebugJDA(true);
        }

        new BuildReader(this);

        if (parameters.isVersion()) {
            System.out.println("Alkabot version " + getVersion());
            System.out.println("Build " + getBuild());
            System.out.println(getGithub());
            return;
        }

        try {
            // Validate folder paths
            parameters.setLogsPath(validateFolderPath(parameters.getLogsPath()));
            parameters.setDataPath(validateFolderPath(parameters.getDataPath()));
            parameters.setLangPath(validateFolderPath(parameters.getLangPath()));

            Logs.setupRootLogger(parameters);

            logger = Logs.createLogger(Alkabot.class);
            logger.debug("DEBUG MODE ENABLED");
            logger.debug("Please do not use the debug mode in production.");
            logger.debug("---------");
            logger.debug("Provided parameters: " + parameters.toString());

            // generate files
            if (parameters.isGenerateFiles()) {
                logger.info("Generating default files...");
                new DefaultFilesGenerator(parameters);
                return;
            }

            // Splash text
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

            if (snapshotBuild) {
                logger.warn("***                                                                                ***");
                logger.warn("*** THIS VERSION IS A DEV BUILD AND SHOULD NOT BE USED IN A PRODUCTION ENVIRONMENT ***");
                logger.warn("***                                                                                ***");
            }

            if (version.equals("unknown"))
                logger.warn("Unable to find the current Alkabot version, be careful...");

            // Initializing tokens
            logger.info("Loading tokens...");
            TokenLoader tokenLoader = new TokenLoader(this);
            tokenLoader.load();

            if (!tokenLoader.success)
                return;

            if (tokens.getDiscordToken() == null) {
                logger.error("No Discord token provided!");
                return;
            }

            // Initializing configuration
            logger.info("Loading configuration...");
            ConfigLoader configLoader = new ConfigLoader(this);
            configLoader.load();

            if (!configLoader.success)
                return;

            // Initializing translations
            logger.info("Loading language...");
            TranslationsLoader translationsLoader = new TranslationsLoader(this);
            translationsLoader.load();

            if (!translationsLoader.success)
                return;

            // Initializing commands
            // Always initialize command AFTER parsing the configuration
            commandManager = new CommandManager(this);
            commandManager.initialize();
            logger.info(commandManager.getCommands().size() + " commands enabled and ready");

            // Initializing music manager
            musicManager = new MusicManager(this);

            // Initializing Notification manager
            notificationManager = new NotificationManager(this);

            // Initializing music data
            logger.info("Loading jukebox data...");
            MusicDataLoader musicDataLoader = new MusicDataLoader(this);
            musicDataLoader.load();

            if (!musicDataLoader.success)
                return;

            // Initializing Listener Manager
            listenerManager = new ListenerManager(this);

            // Building JDA
            logger.debug("Creating JDA...");

            JDABuilder jdaBuilder = JDABuilder.createDefault(tokens.getDiscordToken());
            jdaBuilder.setRawEventsEnabled(true);
            jdaBuilder.setStatus(OnlineStatus.valueOf(config.getGuildConfig().getGuildPresenceConfig().getStatus()));
            if (config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().isShowing())
                jdaBuilder.setActivity(buildActivity());

            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_MODERATION,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT);
            jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);

            listenerManager.initialize(jdaBuilder);

            logger.info("Starting JDA...");
            jda = jdaBuilder.build();
        } catch (Exception exception) {
            logger.error("An unexpected error prevented the bot to start", exception);
        }
    }

    public String getFullVersion() {
        return version + " (" + build + ")";
    }

    public void shutdown() {
        commandManager.getTerminalCommandHandler().setRunning(false);
        commandManager.getTerminalCommandHandlerThread().interrupt();
        jda.shutdown();
        System.exit(0);
    }

    public Shortcut getShortcut(String name) {
        Shortcut shortcut = null;

        for (Shortcut s : musicData.getShortcutList())
            if (s.getName().equalsIgnoreCase(name))
                shortcut = s;

        return shortcut;
    }

    public boolean setupGuild() {
        Guild guild = jda.getGuildById(config.getGuildConfig().getGuildId());

        if (guild == null) {
            logger.error("The Discord guild '" + config.getGuildConfig().getGuildId() + "' was not found");
            return false;
        }

        logger.debug("Guild: " + guild.getName());
        this.guild = guild;

        return true;
    }

    public boolean setupWelComeChannel() {
        if (config.getWelcomeMessageConfig().isEnable()) {
            TextChannel textChannel = jda.getTextChannelById(config.getWelcomeMessageConfig().getChannelId());
            if (textChannel == null) {
                logger.warn("Disabling welcome messages because the channel '" + config.getWelcomeMessageConfig().getChannelId() + "' was not found");
                config.getWelcomeMessageConfig().setEnable(false);
                return false;
            } else {
                logger.debug("Welcome message channel: " + textChannel.getName());
                welcomeMessageChannel = textChannel;
                return true;
            }
        }
        return true;
    }

    public boolean setupAutoRole() {
        if (config.getAutoRoleConfig().isEnable()) {
            Role role = guild.getRoleById(config.getAutoRoleConfig().getRoleId());
            if (role == null) {
                logger.warn("Disabling auto-role because the role '" + config.getAutoRoleConfig().getRoleId() + "' was not found");
                config.getAutoRoleConfig().setEnable(false);
                return false;
            } else {
                logger.debug("Auto-role: " + role.getName());
                autoRole = role;
                return true;
            }
        }
        return true;
    }

    public void updateCommands() {
        logger.info("Updating commands...");

        List<SlashCommandData> commands = new ArrayList<>();

        for (AbstractCommand abstractCommand : commandManager.getCommands().values())
            if (abstractCommand.isEnabled())
                commands.add(abstractCommand.getCommandData());

        guild.updateCommands().addCommands(commands).queue();
    }

    public Activity buildActivity() {
        logger.debug("Building activity");

        Activity.ActivityType activityType = Activity.ActivityType.valueOf(config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getType());
        return Activity.of(activityType, config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getText());
    }

    public void updateMusicData() throws Exception {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        Files.writeString(new File(parameters.getDataPath() + "/music.json").toPath(), gson.toJson(musicData, MusicData.class));
    }

    private String validateFolderPath(String string) {
        if (string.startsWith("/"))
            string = string.substring(1);

        if (string.endsWith("/"))
            string = string.substring(0, string.length() - 1);

        return string;
    }
}
