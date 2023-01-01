package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.admin.StatusCommand;
import fr.alkanife.alkabot.command.music.*;
import fr.alkanife.alkabot.command.utilities.CopyCommand;
import fr.alkanife.alkabot.command.utilities.InfoCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.*;

public class CommandManager {

    private Map<String, AbstractCommand> commands = new HashMap<>();
    private Map<String, AbstractAdminCommand> adminCommands = new HashMap<>();

    private TerminalCommandHandler terminalCommandHandler;
    private Thread terminalCommandHandlerThread;

    public CommandManager() {}

    public void initialize() {
        terminalCommandHandler = new TerminalCommandHandler();
        terminalCommandHandlerThread = new Thread(terminalCommandHandler, "Alkabot TCH");

        registerAdminCommands(new fr.alkanife.alkabot.command.admin.StopCommand(), new StatusCommand());

        registerCommand(new AboutCommand());

        registerCommands(new ClearCommand(), new DestroyCommand(), new ForceplayCommand(), new PlayCommand(), new PlaylistCommand(), new PlaynextCommand(),
                new QueueCommand(), new RemoveCommand(), new ShuffleCommand(), new SkipCommand(), new fr.alkanife.alkabot.command.music.StopCommand());

        registerCommands(new CopyCommand(), new InfoCommand());
    }

    public Collection<AbstractCommand> getCommands() {
        return commands.values();
    }

    public AbstractCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

    public Collection<AbstractAdminCommand> getAdminCommands() {
        return adminCommands.values();
    }

    public AbstractAdminCommand getAdminCommand(String commandName) {
        return adminCommands.get(commandName);
    }

    public TerminalCommandHandler getTerminalCommandHandler() {
        return terminalCommandHandler;
    }

    public void setTerminalCommandHandler(TerminalCommandHandler terminalCommandHandler) {
        this.terminalCommandHandler = terminalCommandHandler;
    }

    public Thread getTerminalCommandHandlerThread() {
        return terminalCommandHandlerThread;
    }

    public void setTerminalCommandHandlerThread(Thread terminalCommandHandlerThread) {
        this.terminalCommandHandlerThread = terminalCommandHandlerThread;
    }

    public void registerCommands(AbstractCommand... abstractCommands) {
        for (AbstractCommand abstractCommand : abstractCommands)
            registerCommand(abstractCommand);
    }

    public void registerCommand(AbstractCommand abstractCommand) {
        if (abstractCommand.isEnabled()) {
            Alkabot.debug("Adding " + abstractCommand.getClass().getName());
            commands.put(abstractCommand.getName(), abstractCommand);
        }
    }

    public void registerAdminCommands(AbstractAdminCommand... abstractAdminCommands) {
        for (AbstractAdminCommand abstractAdminCommand : abstractAdminCommands)
            registerAdminCommand(abstractAdminCommand);
    }

    public void registerAdminCommand(AbstractAdminCommand abstractAdminCommand) {
        Alkabot.debug("Adding " + abstractAdminCommand.getClass().getName());
        adminCommands.put(abstractAdminCommand.getName(), abstractAdminCommand);
    }

    public void handleSlash(SlashCommandInteractionEvent event) {
        try {
            AbstractCommand abstractCommand = getCommand(event.getName().toLowerCase(Locale.ROOT));

            if (abstractCommand == null)
                return;

            Alkabot.debug("Invoking command '" + event.getFullCommandName() + "'");
            abstractCommand.execute(event);
        } catch (Exception exception) {
            event.reply(Alkabot.t("command.generic.error")).queue();
            Alkabot.getLogger().error("Failed to handle a command:\n" + buildTrace(event));
            exception.printStackTrace();
        }
    }

    public void handleAdmin(AdminCommandExecution execution) {
        try {
            String[] command = execution.getCommand().split(" ");
            AbstractAdminCommand abstractAdminCommand = getAdminCommand(command[0]);

            Alkabot.getLogger().info("Invoking command '" + execution.getCommand() + "' (isFromDiscord=" + execution.isFromDiscord() + ")");

            if (abstractAdminCommand == null) {
                CommandManager.adminHelp(execution);
                return;
            }

            abstractAdminCommand.execute(execution);
        } catch (Exception exception) {
            execution.reply("An error prevented me from processing your command, check logs");
            Alkabot.getLogger().error("Failed to handle an admin command:");
            exception.printStackTrace();
        }
    }

    public static void adminHelp(AdminCommandExecution execution) {
        StringBuilder stringBuilder = new StringBuilder("Administrative commands:");

        for (AbstractAdminCommand command : Alkabot.getCommandManager().getAdminCommands())
            stringBuilder.append("\n- ").append(command.getUsage()).append(": ").append(command.getDescription());

        execution.reply(stringBuilder.toString());
    }

    public static String buildTrace(SlashCommandInteractionEvent event) {
        StringBuilder stringBuilder = new StringBuilder("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv").append("\n");

        stringBuilder.append("* getId() / getCommandId() -> ").append(event.getId()).append(" / ").append(event.getCommandId()).append("\n");
        stringBuilder.append("* getName() -> ").append(event.getName()).append("\n");
        stringBuilder.append("* getFullCommandName() -> ").append(event.getFullCommandName()).append("\n");
        stringBuilder.append("* getCommandString() -> ").append(event.getCommandString()).append("\n");
        stringBuilder.append("* getSubcommandGroup() -> ").append(event.getSubcommandGroup()).append("\n");
        stringBuilder.append("* getSubcommandName() -> ").append(event.getSubcommandName()).append("\n");
        stringBuilder.append("* getChannel().getName() -> ").append(event.getChannel().getName()).append("\n");
        stringBuilder.append("* getChannelType() -> ").append(event.getChannelType().name()).append("\n");

        stringBuilder.append("* getMember().getEffectiveName() -> ");
        if (event.getMember() == null)
            stringBuilder.append("null");
        else
            stringBuilder.append(event.getMember().getEffectiveName());

        stringBuilder.append("\n* getOptions() -> ").append(event.getOptions().size()).append("\n");
        int i = 0;
        for (OptionMapping optionMapping : event.getOptions()) {
            if (i != 0)
                stringBuilder.append("- ").append(i).append(" ---------").append("\n");

            stringBuilder.append(" * getName() ").append(optionMapping.getName()).append("\n");
            stringBuilder.append(" * getType() -> ").append(optionMapping.getType()).append("\n");
            stringBuilder.append(" * getChannelType() -> ").append(optionMapping.getChannelType().name()).append("\n");
            stringBuilder.append(" * optionMapping -> ").append(optionMapping).append("\n");

            i++;
        }
        
        stringBuilder.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        return stringBuilder.toString();
    }

}
