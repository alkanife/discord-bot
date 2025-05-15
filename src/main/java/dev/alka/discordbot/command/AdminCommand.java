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
package dev.alka.discordbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class AdminCommand extends BotCommand {

    public AdminCommand(CommandManager commandManager) {
        super(commandManager);
    }

    public abstract String getUsage();
    public abstract AdminCommandTarget getCommandTarget();

    public abstract void handleDiscord(String query, MessageReceivedEvent event);
    public abstract void handleTerminal(String query);

    public void replyTerminal(String message) {
        discordBot.getLogger().info("[{}] {}", getName(), message);
    }

    public enum AdminCommandTarget {
        DISCORD, TERMINAL, TERMINAL_AND_DISCORD;
    }
}