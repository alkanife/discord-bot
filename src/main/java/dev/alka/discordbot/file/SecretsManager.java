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
package dev.alka.discordbot.file;

import dev.alka.discordbot.DiscordBot;
import dev.alka.utils.json.JsonKeys;
import lombok.Getter;

import java.io.File;

public class SecretsManager extends JsonManipulation {

    @Getter
    private String discordToken;

    public SecretsManager(DiscordBot discordBot) {
        super(discordBot);
    }

    public boolean grab() {
        discordBot.getLogger().info("Grabbing secrets");

        try {
            FileManipulation fileManipulation = new FileManipulation(discordBot, new File(discordBot.getCliArguments().getSecretsPath()));

            JsonKeys jsonKeys = new JsonKeys("{}");

            if (fileManipulation.readFile().succeed()) {
                jsonKeys = new JsonKeys(fileManipulation.getFileContent());
            } else {
                discordBot.getLogger().warn("No secrets file was found, a new one will be created");
            }

            discordToken = (String) jsonKeys.getKey("discord.token", "CHANGE ME");

            return fileManipulation.writeFile(true, jsonKeys.getJson()).succeed();
        } catch (Exception exception) {
            discordBot.getLogger().error("Failed to parse secrets", exception);
            return false;
        }
    }
}
