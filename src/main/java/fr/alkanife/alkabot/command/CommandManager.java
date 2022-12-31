package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.admin.ShutdownCommand;
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

    public CommandManager() {}

    public void initialize() {
        registerAdminCommands(new ShutdownCommand(), new StatusCommand());

        registerCommand(new AboutCommand());

        registerCommands(new ClearCommand(), new DestroyCommand(), new ForceplayCommand(), new PlayCommand(), new PlaylistCommand(), new PlaynextCommand(),
                new QueueCommand(), new RemoveCommand(), new ShuffleCommand(), new SkipCommand(), new StopCommand());

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

    public void registerCommands(AbstractCommand... abstractCommands) {
        for (AbstractCommand abstractCommand : abstractCommands)
            registerCommand(abstractCommand);
    }

    public void registerCommand(AbstractCommand abstractCommand) {
        Alkabot.debug("Adding " + abstractCommand.getClass().getName());
        commands.put(abstractCommand.getName(), abstractCommand);
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
            event.reply(Alkabot.t("generic.command.error")).queue();
            Alkabot.getLogger().error("Failed to handle a command:\n" + buildTrace(event));
            exception.printStackTrace();
        }
    }

    public void handleAdmin(MessageReceivedEvent event) {
        try {
            String[] command = event.getMessage().getContentRaw().toLowerCase(Locale.ROOT).split(" ");
            AbstractAdminCommand abstractAdminCommand = getAdminCommand(command[0]);

            if (abstractAdminCommand == null) {
                CommandManager.adminHelp(event);
                return;
            }

            Alkabot.debug("Invoking command '" + event.getMessage().getContentRaw() + "'");
            abstractAdminCommand.execute(event);
        } catch (Exception exception) {
            event.getMessage().reply("An error prevented me from processing your command, check logs").queue();
            Alkabot.getLogger().error("Failed to handle an admin command:");
            exception.printStackTrace();
        }
    }

    public static void adminHelp(MessageReceivedEvent event) {
        StringBuilder stringBuilder = new StringBuilder("Administrative commands:");

        for (AbstractAdminCommand command : Alkabot.getCommandManager().getAdminCommands())
            stringBuilder.append("- `").append(command.getUsage()).append("`: ").append(command.getDescription()).append("\n");

        event.getMessage().reply(stringBuilder.toString()).queue();
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
