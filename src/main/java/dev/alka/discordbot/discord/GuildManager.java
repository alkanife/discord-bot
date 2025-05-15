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
package dev.alka.discordbot.discord;

import dev.alka.discordbot.DiscordBot;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;

public class GuildManager {

    @Getter
    private final DiscordBot discordBot;

    @Getter @Setter
    private Guild guild;

    public GuildManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    public boolean loadGuild() {
        try {
            Guild guild = discordBot.getJda().getGuildById(discordBot.getConfigManager().getGuildID());

            if (guild == null) {
                discordBot.getLogger().error("The Discord guild '{}' was not found", discordBot.getConfigManager().getGuildID());
                return false;
            }

            this.guild = guild;

            return true;
        } catch (Exception exception) {
            discordBot.getLogger().error("Fatal: The given Discord guild ID is not valid");
            discordBot.getLogger().debug("Full trace:", exception);
            return false;
        }
    }
}