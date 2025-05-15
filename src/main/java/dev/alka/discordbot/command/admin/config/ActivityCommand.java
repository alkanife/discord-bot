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
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class ActivityCommand extends AdminCommand {

    public ActivityCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getUsage() {
        return "activity types|activity show=boolean type=null|ActivityType text=null|string";
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
        return "activity";
    }

    @Override
    public String getDescription() {
        return "Change bot activity";
    }

    private String handle(String query) {
        String[] queryParts = query.split(" ");

        if (queryParts.length < 2) {
            return "Usage: " + getUsage();
        }

        if (queryParts[1].equalsIgnoreCase("types")) {
            StringBuilder response = new StringBuilder("Activity types: ");

            for (Activity.ActivityType type : Activity.ActivityType.values())
                response.append(type).append(" ");

            return response.toString();
        }

        boolean show = Boolean.parseBoolean(queryParts[1]);

        if (!show) {
            discordBot.getConfigManager().setShowingActivity(false);
            discordBot.getConfigManager().setActivity(null);

            if (discordBot.getConfigManager().update()) {
                discordBot.setPresence();
                return "Activity disabled successfully";
            } else {
                return "An error occurred while trying to update the activity, check the console";
            }
        }

        if (queryParts.length < 4) {
            return "Usage: " + getUsage();
        }

        String type = queryParts[2];
        Activity.ActivityType activityType;
        try {
            activityType = Activity.ActivityType.valueOf(type);
        } catch (Exception exception) {
            return "Invalid activity type '" + type + "'";
        }

        String text = String.join(" ", Arrays.copyOfRange(queryParts, 3, queryParts.length));

        Activity activity = Activity.of(activityType, text);

        discordBot.getConfigManager().setShowingActivity(true);
        discordBot.getConfigManager().setActivity(activity);

        if (discordBot.getConfigManager().update()) {
            discordBot.setPresence();
            return "Activity changed successfully";
        } else {
            return "An error occurred while trying to update the activity, check the console";
        }
    }
}
