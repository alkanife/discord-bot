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
package dev.alka.discordbot.command.admin.config;

import dev.alka.discordbot.command.AdminCommand;
import dev.alka.discordbot.command.CommandManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class OnlineStatusCommand extends AdminCommand {

    public OnlineStatusCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getUsage() {
        return "onlinestatus ONLINE|DO_NOT_DISTURB|IDLE|INVISIBLE";
    }

    @Override
    public AdminCommandTarget getCommandTarget() {
        return AdminCommandTarget.TERMINAL_AND_DISCORD;
    }

    @Override
    public void handleDiscord(String query, MessageReceivedEvent event) {
        event.getMessage().reply(handle(query)).queue();
    }

    @Override
    public void handleTerminal(String query) {
        replyTerminal(handle(query));
    }

    @Override
    public String getName() {
        return "onlinestatus";
    }

    @Override
    public String getDescription() {
        return "Chnage bot status";
    }

    private String handle(String query) {
        String[] queryParts = query.split(" ");

        if (queryParts.length < 2) {
            return "Usage: " + getUsage();
        }

        String status = queryParts[1];
        OnlineStatus onlineStatus;
        try {
            onlineStatus = OnlineStatus.valueOf(status);
        } catch (Exception exception) {
            return "Invalid online status, see usage";
        }

        discordBot.getConfigManager().setOnlineStatus(onlineStatus);

        if (discordBot.getConfigManager().update()) {
            discordBot.setPresence();
            return "Status changed successfully";
        } else {
            return "An error occurred while trying to update the status, check the console";
        }
    }
}
