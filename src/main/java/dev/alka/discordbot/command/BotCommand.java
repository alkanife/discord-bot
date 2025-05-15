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

import dev.alka.discordbot.DiscordBot;

public abstract class BotCommand {

    public CommandManager commandManager;
    public DiscordBot discordBot;

    public BotCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.discordBot = commandManager.getDiscordBot();
    }

    public abstract String getName();
    public abstract String getDescription();

}
