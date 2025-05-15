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
package dev.alka.discordbot.command.admin.general;

import dev.alka.discordbot.command.CommandManager;
import dev.alka.discordbot.command.AdminCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand extends AdminCommand {

    public PingCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getUsage() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Get Discord latency";
    }

    @Override
    public AdminCommandTarget getCommandTarget() {
        return AdminCommandTarget.TERMINAL_AND_DISCORD;
    }

    @Override
    public void handleDiscord(String query, MessageReceivedEvent event) {
        discordBot.getJda().getRestPing().queue(aLong -> event.getMessage().reply("Pong! (restping= " + aLong + " ms)").queue());
    }

    @Override
    public void handleTerminal(String query) {
        discordBot.getJda().getRestPing().queue(aLong -> replyTerminal("Pong! (restping= " + aLong + " ms)"));
    }
}