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
package dev.alka.discordbot.discord.event;

import dev.alka.discordbot.DiscordBot;
import dev.alka.discordbot.command.admin.AdminCommandHandler;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandListener extends ListenerAdapter {

    private final DiscordBot discordBot;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getChannelType().equals(ChannelType.PRIVATE))
            return;

        if (event.getAuthor().isBot())
            return;

        if (!discordBot.getConfigManager().getAdministratorIDs().contains(event.getAuthor().getId()))
            return;

        new AdminCommandHandler(discordBot, event.getMessage().getContentRaw(), event);
    }

}