// Copyright (C) 2024 - Arthur Beau ("Alkanife", "Alka") [https://alka.dev]
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package dev.alka.discordbot;

import ch.qos.logback.classic.Logger;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;
import dev.alka.discordbot.command.CommandManager;
import dev.alka.discordbot.discord.GuildManager;
import dev.alka.discordbot.discord.event.EventListenerManager;
import dev.alka.discordbot.file.ConfigManager;
import dev.alka.discordbot.file.SecretsManager;
import dev.alka.discordbot.util.Logs;
import dev.alka.utils.builds.BuildMeta;
import dev.alka.utils.builds.BuildUtils;
import dev.alka.utils.cli.PrettyUsage;
import dev.alka.utils.jcommander.JCommandArranger;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;

public class DiscordBot {

    @Getter
    private static DiscordBot instance;
    @Getter
    private final CliArguments cliArguments;
    @Getter
    private BuildMeta buildMeta;
    @Getter
    private Logger logger;
    @Getter
    private JDA jda;

    @Getter
    private EventListenerManager eventListenerManager;
    @Getter
    private SecretsManager secretsManager;
    @Getter
    private ConfigManager configManager;
    @Getter
    private GuildManager guildManager;
    @Getter
    private CommandManager commandManager;

    public DiscordBot(String[] args) {
        instance = this;

        cliArguments = new CliArguments();
        JCommander jCommander = JCommander.newBuilder().programName("discord-bot").addObject(cliArguments).build();

        String[] parsedArgs = args;

        try {
            parsedArgs = parseArguments(args);
        } catch (Exception exception) {
            System.out.println("\u001B[0;31mFailed to use the argument file '" + args[0] + "'\u001B[0m");
            return;
        }
        
        try {
            jCommander.parse(parsedArgs);
        } catch (ParameterException exception) {
            System.out.println("\u001B[0;31mInvalid arguments, see correct usage with '-help'\u001B[0m");
            return;
        }

        if (getCliArguments().isHelp() || (!getCliArguments().isStart() && !getCliArguments().isVersion())) {
            System.out.println("\u001B[0;31mNo arguments given, showing usage\u001B[0m");
            System.out.println();
            printUsage(jCommander);
            return;
        }

        buildMeta = BuildUtils.readBuild("discord-bot");

        if (getCliArguments().isVersion()) {
            System.out.println("Using version " + buildMeta.getVersionAndBuildTime());

            if (!buildMeta.getGitRevision().equals("none"))
                System.out.println("Git: " + buildMeta.getGitRevision());

            return;
        }

        try {
            Logs.setupRootLogger(getCliArguments());
            logger = Logs.createLogger(getCliArguments(), DiscordBot.class);
        } catch (Exception exception) {
            System.out.println("\u001B[0;31mAn error occurred while creating the logger.\nIf you edited the logger configuration, please check the options used and the error below.\n");
            exception.printStackTrace();
            System.out.print("\u001B[0m");
            return;
        }

        logger.debug("-----------------------------------------------");
        logger.debug("DEBUG MODE ENABLED");
        logger.debug("Please do not use the debug mode in production.");
        logger.debug("-----------------------------------------------");
        logger.debug(getCliArguments().toString());

        logger.info("Starting Alka's Discord bot version {}", buildMeta.getVersion());
        logger.info("Copyright (C) 2024 - Arthur Beau (\"Alkanife\", \"Alka\") [https://alka.dev]");

        if (BuildUtils.isSnapshot(buildMeta.getVersion()))
            logger.warn("This version is an experiment and some features are not finished, take extra care!");

        eventListenerManager = new EventListenerManager(this);
        secretsManager = new SecretsManager(this);
        configManager = new ConfigManager(this);
        guildManager = new GuildManager(this);
        commandManager = new CommandManager(this);

        if (!secretsManager.grab() || !configManager.grab())
            return;

        commandManager.load();

        createAndBuildJDA();
    }

    private void createAndBuildJDA() {
        logger.debug("Creating JDA");

        String error = "No discord token was found, or there was an error while getting the bot token";

        if (secretsManager.getDiscordToken() == null) {
            logger.error(error);
            return;
        }

        if (secretsManager.getDiscordToken().isEmpty()) {
            logger.error(error);
            return;
        }

        JDABuilder jdaBuilder = JDABuilder.create(secretsManager.getDiscordToken(), EnumSet.allOf(GatewayIntent.class));
        jdaBuilder.setRawEventsEnabled(true);
        jdaBuilder.enableCache(EnumSet.allOf(CacheFlag.class));
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        eventListenerManager.addEventListeners(jdaBuilder);

        logger.info("Connecting to Discord");
        jda = jdaBuilder.build();
    }

    private String[] parseArguments(String[] args) throws IOException {
        if (args.length == 0) {
            return args;
        }

        if (args[0].toLowerCase().contains(".txt")) {
            String content = Files.readString(Paths.get(args[0]));

            return content.trim().split("\\s+");
        }

        return args;
    }

    private void printUsage(JCommander jCommander) {
        String jarName = BuildUtils.getJarName(DiscordBot.class);

        System.out.println("Usage....: \u001B[0;32mjava -jar \u001B[0;34m" + jarName + " \u001B[0;32m[options...]\u001B[0m");
        System.out.println("        \u001B[1;30mor \u001B[0;32mjava -jar \u001B[0;34m" + jarName + " \u001B[0;32moptions.txt\u001B[0m");
        System.out.println();
        System.out.println("Example..: \u001B[0;32mjava -jar \u001B[0;34m" + jarName + "\u001B[0;32m -start -debug \u001B[1;30m...\u001B[0m");
        System.out.println();
        System.out.println("Options:");
        System.out.println();

        List<ParameterDescription> orderedParameters = JCommandArranger.arrange(jCommander);
        PrettyUsage prettyUsage = getPrettyUsage(orderedParameters);

        for (String command : prettyUsage.getLines())
            System.out.println(command);
    }

    @NotNull
    private PrettyUsage getPrettyUsage(List<ParameterDescription> orderedParameters) {
        PrettyUsage prettyUsage = new PrettyUsage();
        prettyUsage.setPaddingLeft(2);
        prettyUsage.importValue("\u001B[0;32mArgument\u001B[0m", "    \u001B[0mDescription \u001B[1;30m(default value)\u001B[0m");
        prettyUsage.importValue(" ", " ");

        for (ParameterDescription param : orderedParameters) {
            String name = "\u001B[0;32m" + param.getNames().replaceAll(",", "\u001B[1;30m,\u001B[0;32m");

            String description = "\u001B[0m" + param.getDescription();

            if (name.contains(","))
                description = "              " + description;

            String defaultValue = ""+param.getDefault();

            if (!defaultValue.equals("null") && !defaultValue.isEmpty()) {
                if (defaultValue.equals("false"))
                    defaultValue = "not active";

                if (defaultValue.equals("true"))
                    defaultValue = "active";
            }

            defaultValue = " \u001B[1;30m(" + defaultValue + ")\u001B[0m";

            prettyUsage.importValue(name, description + defaultValue);
        }

        return prettyUsage;
    }

    public void shutdown() {
        commandManager.getTerminalCommandHandler().setRunning(false);
        commandManager.getTerminalCommandHandlerThread().interrupt();
        jda.shutdown();
        System.exit(0);
    }

    public void setPresence() {
        jda.getPresence().setStatus(configManager.getOnlineStatus());

        if (configManager.isShowingActivity())
            jda.getPresence().setActivity(configManager.getActivity());
        else
            jda.getPresence().setActivity(null);
    }

}
