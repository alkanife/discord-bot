package fr.alkanife.alkabot;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.configuration.ConfigLoader;
import fr.alkanife.alkabot.configuration.json.Configuration;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.lang.TranslationsManager;
import fr.alkanife.alkabot.listener.ListenerManager;
import fr.alkanife.alkabot.music.MusicData;
import fr.alkanife.alkabot.music.MusicDataLoader;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.music.Shortcut;
import fr.alkanife.alkabot.notification.NotificationManager;
import fr.alkanife.alkabot.tokens.TokenLoader;
import fr.alkanife.alkabot.tokens.Tokens;
import fr.alkanife.alkabot.utils.tools.BuildReader;
import fr.alkanife.alkabot.utils.tools.DefaultFilesGenerator;
import fr.alkanife.alkabot.utils.tools.LogsCleaner;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Alkabot {

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
    @Getter @Setter
    private TranslationsManager translationsManager;
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

            if (parameters.isHelp()) {
                jCommander.usage();
                return;
            }

            verbose("Provided parameters: " + parameters.toString());

            // Validate folder paths
            parameters.setLogsPath(validateFolderPath(parameters.getLogsPath()));
            parameters.setDataPath(validateFolderPath(parameters.getDataPath()));
            parameters.setLangPath(validateFolderPath(parameters.getLangPath()));

            // generate files
            if (parameters.isGenerateFiles()) {
                new DefaultFilesGenerator(this);
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

            if (version.equals("unknown"))
                System.out.println("Unable to find the current Alkabot version, be careful...");

            Thread.sleep(2000);

            // Moving old latest.log file
            try {
                new LogsCleaner(this);
            } catch (Exception exception) {
                printJavaError("/i\\ Failed to clean logs", exception);
            }

            // Create logger
            verbose("Switching to SLF4J logger");
            logger = LoggerFactory.getLogger(Alkabot.class);

            // Initializing tokens
            try {
                logger.info("Loading tokens");
                new TokenLoader(this).load();
            } catch (Exception exception) {
                printFileError("tokens", exception);
                return;
            }

            if (tokens.getDiscordToken() == null) {
                logger.error("No Discord token provided!");
                return;
            }

            // Initializing configuration
            try {
                logger.info("Loading config");
                new ConfigLoader(this).load();
            } catch (Exception exception) {
                printFileError("configuration", exception);
                return;
            }

            // Initializing translations
            try {
                logger.info("Loading translations");
                translationsManager = new TranslationsManager(this);
                new TranslationsLoader(this).load();
            } catch (Exception exception) {
                printFileError("translations", exception);
                return;
            }

            // Initializing commands
            // Always initialize command AFTER parsing the configuration
            commandManager = new CommandManager(this);
            commandManager.initialize();
            logger.info(commandManager.getCommands().size() + " commands ready");

            // Initializing music manager
            musicManager = new MusicManager(this);

            // Initializing Notification manager
            notificationManager = new NotificationManager(this);

            // Initializing music data
            try {
                logger.info("Loading music data");
                new MusicDataLoader(this).load();
            } catch (Exception exception) {
                printFileError("music data", exception);
                return;
            }

            // Initializing Listener Manager
            listenerManager = new ListenerManager(this);

            // Building JDA
            logger.info("Building JDA...");

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

            logger.info("Starting JDA");
            jda = jdaBuilder.build();
        } catch (Exception exception) {
            System.out.println("------------");
            printJavaError("An unexpected error prevented the bot to start:", exception);
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

        verbose("Guild: " + guild.getName());
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
                verbose("Welcome message channel: " + textChannel.getName());
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
                verbose("Auto-role: " + role.getName());
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

    public void printFileError(String file, Exception exception) {
        logger.error("Unable to load " + file + " (" + exception.toString() + ")");
        logger.error("Please make sure that this file exists, and that the bot has access to it.");
        logger.error("To generate default files, use the --generateFiles flag.");
        if (parameters.isVerbose())
            logger.error("[verbose] Detailed error: ", exception);
    }

    public void printJavaError(String message, Exception exception) {
        System.out.println(message + " (" + exception.toString() + ")");
        if (parameters.isVerbose()) {
            System.out.println("[verbose] Detailed error: ");
            exception.printStackTrace();
        }
    }

    public Activity buildActivity() {
        verbose("Building activity");

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

        verbose("Using folder path " + string);

        return string;
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
