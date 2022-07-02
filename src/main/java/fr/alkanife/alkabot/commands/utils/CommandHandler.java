package fr.alkanife.alkabot.commands.utils;

import fr.alkanife.alkabot.commands.AdminCommands;
import fr.alkanife.alkabot.commands.BotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);

                method.setAccessible(true);

                BotCommand simpleCommand = new BotCommand(command.name(), command.administrative(), object, method);

                commands.put(command.name(), simpleCommand);
            }
        }
    }

    public void handleSlash(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        try {
            BotCommand botCommand = getCommand(slashCommandInteractionEvent.getName().toLowerCase(Locale.ROOT));

            if (botCommand == null)
                return;

            Parameter[] parameters = botCommand.getMethod().getParameters();

            if (parameters.length != 1)
                return;

            if (parameters[0].getType().equals(SlashCommandInteractionEvent.class)) {
                botCommand.getMethod().invoke(botCommand.getObject(), slashCommandInteractionEvent);

                //success(slashCommandEvent);
            }
        } catch (Exception exception) {
            //fail(slashCommandEvent, exception);
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
                botCommand.getMethod().invoke(botCommand.getObject(), messageReceivedEvent);
                //TODO success
            }
        } catch (Exception exception) {
            messageReceivedEvent.getMessage().reply("An error prevented me from processing your command, check logs").queue();
            exception.printStackTrace();
        }
    }

}
