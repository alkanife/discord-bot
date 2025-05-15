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
import lombok.Setter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.util.List;

public class ConfigManager extends JsonManipulation {

    @Getter @Setter
    private String langFilePath;

    @Getter @Setter
    private List<String> administratorIDs;

    @Getter
    private String guildID;
    @Getter @Setter
    private OnlineStatus onlineStatus;
    @Getter @Setter
    private boolean isShowingActivity;
    @Getter @Setter
    private Activity activity;

    private final String newConfigMessage = "No config file was found, a new one will be created";
    private final FileManipulation fileManipulation;

    public ConfigManager(DiscordBot discordBot) {
        super(discordBot);
        fileManipulation = new FileManipulation(discordBot, new File(discordBot.getCliArguments().getConfigPath()));
    }

    public boolean grab() {
        discordBot.getLogger().info("Grabbing config");

        try {
            JsonKeys jsonKeys = new JsonKeys("{}");
            boolean isNew = true;

            if (fileManipulation.readFile().succeed()) {
                isNew = false;
                jsonKeys = new JsonKeys(fileManipulation.getFileContent());
            } else {
                discordBot.getLogger().warn(newConfigMessage);
            }

            langFilePath = (String) jsonKeys.getKey("lang_file_path", "lang/en.json");

            administratorIDs = (List<String>) jsonKeys.getKey("administrator_ids", List.of(""));

            guildID = (String) jsonKeys.getKey("guild.id", "CHANGE ME");

            onlineStatus = OnlineStatus.valueOf((String) jsonKeys.getKey("guild.presence.status", "ONLINE"));
            isShowingActivity = (Boolean) jsonKeys.getKey("guild.presence.activity.show", false);

            // Activity type
            Object at = jsonKeys.getKey("guild.presence.activity.type", null);
            Object activityText = jsonKeys.getKey("guild.presence.activity.text", null);

            if (at != null && activityText != null) {
                activity = Activity.of(Activity.ActivityType.valueOf((String) at), (String) activityText);
            }

            return fileManipulation.writeFile(isNew, jsonKeys.getJson()).succeed();
        } catch (Exception exception) {
            discordBot.getLogger().error("Failed to parse config", exception);
            return false;
        }
    }

    public boolean update() {
        discordBot.getLogger().info("Updating config");

        try {
            JsonKeys jsonKeys = new JsonKeys("{}");
            boolean isNew = true;

            if (fileManipulation.readFile().succeed()) {
                isNew = false;
                jsonKeys = new JsonKeys(fileManipulation.getFileContent());
            } else {
                discordBot.getLogger().warn(newConfigMessage);
            }

            jsonKeys.updateKey("lang_file_path", langFilePath);
            jsonKeys.updateKey("administrator_ids", administratorIDs);
            jsonKeys.updateKey("guild.id", guildID);
            jsonKeys.updateKey("guild.presence.status", onlineStatus);
            jsonKeys.updateKey("guild.presence.activity.show", isShowingActivity);

            if (isShowingActivity) {
                jsonKeys.updateKey("guild.presence.activity.type", activity.getType().name());
                jsonKeys.updateKey("guild.presence.activity.text", activity.getName());
            } else {
                jsonKeys.updateKey("guild.presence.activity.type", null);
                jsonKeys.updateKey("guild.presence.activity.text", null);
            }

            return fileManipulation.writeFile(isNew, jsonKeys.getJson()).succeed();
        } catch (Exception exception) {
            discordBot.getLogger().error("Failed to update config", exception);
            return false;
        }
    }
}
