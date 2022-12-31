package fr.alkanife.alkabot.commands.utils;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandHandler {

    private Map<String, BotCommand> commands = new HashMap<>();

    public Collection<BotCommand> getCommands() {
        return commands.values();
    }

    public BotCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

    public void registerCommands(Object... objects) {
        for (Object o : objects)
            registerCommand(o);
    }

    public void registerCommand(Object object) {
        Alkabot.debug("In: " + object.getClass().getName() + ":");
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);

                method.setAccessible(true);

                BotCommand simpleCommand = new BotCommand(command.name(), command.administrative(), object, method);

                commands.put(command.name(), simpleCommand);
                Alkabot.debug("  - " + simpleCommand.getName() + ", " + command.administrative());
            }
        }
    }

    public void handleSlash(SlashCommandInteractionEvent event) {
        try {
            BotCommand botCommand = getCommand(event.getName().toLowerCase(Locale.ROOT));

            if (botCommand == null)
                return;

            Parameter[] parameters = botCommand.getMethod().getParameters();

            if (parameters.length != 1)
                return;

            if (parameters[0].getType().equals(SlashCommandInteractionEvent.class)) {
                Alkabot.debug("Invoking '" + botCommand.getMethod().getName() + "' method (command: " + event.getFullCommandName() + ")");
                botCommand.getMethod().invoke(botCommand.getObject(), event);

                //success(slashCommandEvent);
            }
        } catch (Exception exception) {
            event.reply(Alkabot.t("command-error")).queue();
            Alkabot.getLogger().error("Failed to handle a command:\n" + buildTrace(event));
            exception.printStackTrace();
        }
    }

    public void handleAdmin(MessageReceivedEvent messageReceivedEvent) {
        try {
            String[] command = messageReceivedEvent.getMessage().getContentRaw().toLowerCase(Locale.ROOT).split(" ");
            BotCommand botCommand = getCommand(command[0]);

            if (botCommand == null) {
                AdminCommands.help(messageReceivedEvent);
                return;
            }

            Parameter[] parameters = botCommand.getMethod().getParameters();

            if (parameters.length != 1)
                return;

            if (parameters[0].getType().equals(MessageReceivedEvent.class)) {
                Alkabot.debug("Invoking '" + botCommand.getMethod().getName() + "' method (input: " + messageReceivedEvent.getMessage().getContentRaw() + ")");
                botCommand.getMethod().invoke(botCommand.getObject(), messageReceivedEvent);
            }
        } catch (Exception exception) {
            messageReceivedEvent.getMessage().reply("An error prevented me from processing your command, check logs").queue();
            Alkabot.getLogger().error("Failed to handle an admin command:");
            exception.printStackTrace();
        }
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
