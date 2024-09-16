package dev.alkanife.alkabot.command;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.command.admin.AdminCommand;
import dev.alkanife.alkabot.command.admin.TerminalCommandRunnable;
import dev.alkanife.alkabot.command.admin.general.HelpCommand;
import dev.alkanife.alkabot.command.admin.general.PingCommand;
import dev.alkanife.alkabot.command.admin.general.StopbotCommand;
import dev.alkanife.alkabot.command.music.*;
import dev.alkanife.alkabot.util.timetracker.TimeTracker;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.*;

@Getter
public class CommandManager {

    private final Alkabot alkabot;

    @Setter
    private Map<String, AbstractCommand> commands = new HashMap<>();
    @Setter
    private LinkedHashMap<String, AdminCommand> adminCommands = new LinkedHashMap<>();

    @Setter
    private TerminalCommandRunnable terminalCommandHandler;
    @Setter
    private Thread terminalCommandHandlerThread;

    public CommandManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public void load() {
        TimeTracker.start("load-commands");
        alkabot.getLogger().debug("Setting up terminal command thread");
        terminalCommandHandler = new TerminalCommandRunnable(alkabot);
        terminalCommandHandlerThread = new Thread(terminalCommandHandler, "Alkabot TCH");

        alkabot.getLogger().debug("Loading commands");
        registerAdminCommands(
                new HelpCommand(this),
                new PingCommand(this),
                new StopbotCommand(this));

        registerCommands(new ClearCommand(this),
                new DestroyCommand(this),
                new ForceplayCommand(this),
                new PlayCommand(this),
                new ShortcutCommand(this),
                new PlaynextCommand(this),
                new QueueCommand(this),
                new RemoveCommand(this),
                new ShuffleCommand(this),
                new SkipCommand(this),
                new StopCommand(this),
                new NowplayingCommand(this),
                new VolumeCommand(this));

        alkabot.getLogger().debug("{} commands enabled", commands.size());
        TimeTracker.end("load-commands");
    }

    public void registerCommands(AbstractCommand... abstractCommands) {
        for (AbstractCommand abstractCommand : abstractCommands)
            registerCommand(abstractCommand);
    }

    public void registerCommand(AbstractCommand abstractCommand) {
        if (abstractCommand.isEnabled()) {
            alkabot.getLogger().debug("Adding command {}", abstractCommand.getClass().getName());
            commands.put(abstractCommand.getName(), abstractCommand);
        }
    }

    public void registerAdminCommands(AdminCommand... abstractAdminCommands) {
        for (AdminCommand abstractAdminCommand : abstractAdminCommands)
            registerAdminCommand(abstractAdminCommand);
    }

    public void registerAdminCommand(AdminCommand abstractAdminCommand) {
        alkabot.getLogger().debug("Adding command {} (admin)", abstractAdminCommand.getClass().getName());
        adminCommands.put(abstractAdminCommand.getName(), abstractAdminCommand);
    }

    public AbstractCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

    public AdminCommand getAdminCommand(String commandName) {
        return adminCommands.get(commandName);
    }

    public void updateCommandsToDiscord() {
        alkabot.getLogger().debug("Updating commands to Discord");

        List<SlashCommandData> commands = new ArrayList<>();

        for (AbstractCommand abstractCommand : this.commands.values())
            if (abstractCommand.isEnabled())
                commands.add(abstractCommand.getCommandData());

        alkabot.getGuild().updateCommands().addCommands(commands).queue();
    }
}
