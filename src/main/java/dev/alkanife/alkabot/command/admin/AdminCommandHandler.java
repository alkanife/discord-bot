package dev.alkanife.alkabot.command.admin;

import dev.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdminCommandHandler {

    public AdminCommandHandler(@NotNull Alkabot alkabot, @NotNull String query, @Nullable MessageReceivedEvent event) {
        try {
            String[] querySplit = query.split(" ");
            String commandString = querySplit[0].toLowerCase();
            AdminCommand command = alkabot.getCommandManager().getAdminCommand(commandString);

            if (command == null) {
                if (event == null) {
                    alkabot.getLogger().error("Unknown command, type 'help' to see a list of admin commands ('{}')", commandString);
                } else {
                    event.getMessage().reply("Unknown command, type 'help' to see a list of admin commands ('" + commandString + "')").queue();
                }
                return;
            }

            if (event == null) {
                command.handleTerminal(query);
            } else {
                alkabot.getLogger().info("{} executed admin command '{}'", event.getAuthor().getName(), commandString);
                alkabot.getLogger().debug("Complete command: {}", event.getMessage().getContentRaw());
                command.handleDiscord(query, event);
            }

        } catch (Exception exception) {
            if (event != null)
                event.getMessage().reply("Error! *Check console for more details!*").queue();

            alkabot.getLogger().error("Failed to handle an admin command '{}'", query, exception);
        }
    }
}
