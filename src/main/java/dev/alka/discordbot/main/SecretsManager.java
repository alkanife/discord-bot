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
package dev.alka.discordbot.main;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SecretsManager {

    private final DiscordBot bot;

    @Getter
    private String botToken;

    public SecretsManager(DiscordBot bot) {
        this.bot = bot;
    }

    public boolean getSecrets() {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream("secrets.properties")) {
            properties.load(input);

            botToken = properties.getProperty("bot-token");
            return true;
        } catch (IOException e) {
            bot.getLogger().error("An error occurred while getting the bot secrets, please verify your secrets.properties file", e);
            return false;
        }
    }

}
