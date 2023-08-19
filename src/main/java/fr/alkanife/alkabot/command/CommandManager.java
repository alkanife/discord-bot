package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.admin.AbstractAdminCommand;
import fr.alkanife.alkabot.command.admin.TerminalCommandRunnable;
import fr.alkanife.alkabot.commands.AboutCommand;
import fr.alkanife.alkabot.commands.admin.PingCommand;
import fr.alkanife.alkabot.commands.admin.ReloadCommand;
import fr.alkanife.alkabot.commands.admin.StatusCommand;
import fr.alkanife.alkabot.commands.music.*;
import fr.alkanife.alkabot.commands.utilities.CopyCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class CommandManager {

    @Getter
    private Alkabot alkabot;

    @Getter @Setter
    private Map<String, AbstractCommand> commands = new HashMap<>();
    @Getter @Setter
    private Map<String, AbstractAdminCommand> adminCommands = new HashMap<>();

    @Getter @Setter
    private TerminalCommandRunnable terminalCommandHandler;
    @Getter @Setter
    private Thread terminalCommandHandlerThread;

    public CommandManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public void initialize() {
        terminalCommandHandler = new TerminalCommandRunnable(alkabot);
        terminalCommandHandlerThread = new Thread(terminalCommandHandler, "Alkabot TCH");

        registerAdminCommands(new fr.alkanife.alkabot.commands.admin.StopCommand(this),
                new StatusCommand(this),
                new PingCommand(this),
                new ReloadCommand(this));

        registerCommand(new AboutCommand(this));

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
                new fr.alkanife.alkabot.commands.music.StopCommand(this),
                new NowplayingCommand(this));

        registerCommands(new CopyCommand(this));/*,
                new InfoCommand(this)); Info command disabled for now #TODO*/
    }

    public void registerCommands(AbstractCommand... abstractCommands) {
        for (AbstractCommand abstractCommand : abstractCommands)
            registerCommand(abstractCommand);
    }

    public void registerCommand(AbstractCommand abstractCommand) {
        if (abstractCommand.isEnabled()) {
            alkabot.verbose("Adding command " + abstractCommand.getClass().getName());
            commands.put(abstractCommand.getName(), abstractCommand);
        }
    }

    public void registerAdminCommands(AbstractAdminCommand... abstractAdminCommands) {
        for (AbstractAdminCommand abstractAdminCommand : abstractAdminCommands)
            registerAdminCommand(abstractAdminCommand);
    }

    public void registerAdminCommand(AbstractAdminCommand abstractAdminCommand) {
        alkabot.verbose("Adding ADMIN command " + abstractAdminCommand.getClass().getName());
        adminCommands.put(abstractAdminCommand.getName(), abstractAdminCommand);
    }

    public AbstractCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

    public AbstractAdminCommand getAdminCommand(String commandName) {
        return adminCommands.get(commandName);
    }

}
