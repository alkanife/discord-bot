package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.admin.PingCommand;
import fr.alkanife.alkabot.command.admin.ReloadCommand;
import fr.alkanife.alkabot.command.admin.StatusCommand;
import fr.alkanife.alkabot.command.music.*;
import fr.alkanife.alkabot.command.utilities.CopyCommand;
import fr.alkanife.alkabot.command.utilities.InfoCommand;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.*;

public class CommandManager {

    @Getter
    private Alkabot alkabot;

    @Getter @Setter
    private Map<String, AbstractCommand> commands = new HashMap<>();
    @Getter @Setter
    private Map<String, AbstractAdminCommand> adminCommands = new HashMap<>();

    @Getter @Setter
    private TerminalCommandHandler terminalCommandHandler;
    @Getter @Setter
    private Thread terminalCommandHandlerThread;

    public CommandManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public void initialize() {
        terminalCommandHandler = new TerminalCommandHandler(alkabot);
        terminalCommandHandlerThread = new Thread(terminalCommandHandler, "Alkabot TCH");

        registerAdminCommands(new fr.alkanife.alkabot.command.admin.StopCommand(this),
                new StatusCommand(this),
                new PingCommand(this),
                new ReloadCommand(this));

        registerCommand(new AboutCommand(this));

        registerCommands(new ClearCommand(this), new DestroyCommand(this), new ForceplayCommand(this), new PlayCommand(this), new ShortcutCommand(this), new PlaynextCommand(this),
                new QueueCommand(this), new RemoveCommand(this), new ShuffleCommand(this), new SkipCommand(this), new fr.alkanife.alkabot.command.music.StopCommand(this));

        registerCommands(new CopyCommand(this), new InfoCommand(this));
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

    public void handleSlash(SlashCommandInteractionEvent event) {
        boolean success = true;

        try {
            AbstractCommand abstractCommand = getCommand(event.getName().toLowerCase(Locale.ROOT));

            if (abstractCommand == null)
                return;

            alkabot.verbose("Invoking command '" + event.getFullCommandName() + "'");
            abstractCommand.execute(event);
        } catch (Exception exception) {
            event.reply(alkabot.t("command.generic.error")).queue();
            alkabot.getLogger().error("Failed to handle a command:\n" + buildTrace(event));
            exception.printStackTrace();
            success = false;
        }

        alkabot.getNotificationManager().getSelfNotification().notifyCommand(event, success);
    }

    public void handleAdmin(AdminCommandExecution execution) {
        try {
            String[] command = execution.command().split(" ");

            if (command[0].equalsIgnoreCase("help")) {
                adminHelp(execution);
                return;
            }

            AbstractAdminCommand abstractAdminCommand = getAdminCommand(command[0]);

            if (abstractAdminCommand == null) {
                execution.reply("Unknown command. Type 'help' to see a list of administrative commands");
                return;
            }

            if (execution.isFromDiscord())
                alkabot.getLogger().info(execution.messageReceivedEvent().getAuthor().getName() + " executed admin command '" + execution.command() + "'");

            if (abstractAdminCommand.isDiscordOnly() && !execution.isFromDiscord()) {
                alkabot.getLogger().error("This command can only be executed from Discord.");
                return;
            }

            abstractAdminCommand.execute(execution);
        } catch (Exception exception) {
            execution.reply("An error prevented me from processing your command.");
            alkabot.getLogger().error("Failed to handle an admin command:", exception);
        }
    }

    public void adminHelp(AdminCommandExecution execution) {
        StringBuilder stringBuilder = new StringBuilder("Administrative commands:");

        for (AbstractAdminCommand command : adminCommands.values())
            stringBuilder.append("\n - ").append(command.getUsage()).append(": ").append(command.getDescription());

        execution.reply(stringBuilder.toString());
    }

    public String buildTrace(SlashCommandInteractionEvent event) {
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

    public AbstractCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

    public AbstractAdminCommand getAdminCommand(String commandName) {
        return adminCommands.get(commandName);
    }

}
