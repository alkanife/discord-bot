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
package dev.alka.discordbot.command.admin;

import dev.alka.discordbot.DiscordBot;
import dev.alka.discordbot.command.AdminCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AdminCommandHandler {

    public AdminCommandHandler(DiscordBot discordBot, String query, MessageReceivedEvent event) {
        try {
            String[] querySplit = query.split(" ");
            String commandString = querySplit[0].toLowerCase();
            AdminCommand command = discordBot.getCommandManager().getAdminCommand(commandString);

            if (command == null) {
                if (event == null) {
                    discordBot.getLogger().error("Unknown command, type 'help' to see a list of admin commands ('{}')", commandString);
                } else {
                    event.getMessage().reply("Unknown command, type 'help' to see a list of admin commands ('" + commandString + "')").queue();
                }
                return;
            }

            if (event == null) {
                command.handleTerminal(query);
            } else {
                discordBot.getLogger().info("{} executed admin command '{}'", event.getAuthor().getName(), commandString);
                discordBot.getLogger().debug("Complete command: {}", event.getMessage().getContentRaw());
                command.handleDiscord(query, event);
            }

        } catch (Exception exception) {
            if (event != null)
                event.getMessage().reply("Error! *Check console for more details!*").queue();

            discordBot.getLogger().error("Failed to handle an admin command '{}'", query, exception);
        }
    }
}