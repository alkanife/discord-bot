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
import dev.alka.discordbot.command.admin.TerminalCommandRunnable;
import dev.alka.discordbot.command.admin.config.ActivityCommand;
import dev.alka.discordbot.command.admin.config.OnlineStatusCommand;
import dev.alka.discordbot.command.admin.general.HelpCommand;
import dev.alka.discordbot.command.admin.general.PingCommand;
import dev.alka.discordbot.command.admin.general.StopbotCommand;
import dev.alka.discordbot.command.admin.general.UsageCommand;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.*;

@Getter
public class CommandManager {

    private final DiscordBot discordBot;

    @Setter
    private Map<String, SlashCommand> commands = new HashMap<>();
    @Setter
    private LinkedHashMap<String, AdminCommand> adminCommands = new LinkedHashMap<>();

    @Setter
    private TerminalCommandRunnable terminalCommandHandler;
    @Setter
    private Thread terminalCommandHandlerThread;

    public CommandManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    public void load() {
        discordBot.getLogger().debug("Setting up terminal command thread");
        terminalCommandHandler = new TerminalCommandRunnable(discordBot);
        terminalCommandHandlerThread = new Thread(terminalCommandHandler, "TerminalCommandThread");

        discordBot.getLogger().debug("Loading commands");
        registerAdminCommands(
                new HelpCommand(this),
                new PingCommand(this),
                new StopbotCommand(this),
                new UsageCommand(this),
                new ActivityCommand(this),
                new OnlineStatusCommand(this));

        discordBot.getLogger().info("{} commands enabled", commands.size() + adminCommands.size());
    }

    public void registerCommands(SlashCommand... slashCommands) {
        for (SlashCommand slashCommand : slashCommands)
            registerCommand(slashCommand);
    }

    public void registerCommand(SlashCommand slashCommand) {
        if (slashCommand.isEnabled()) {
            discordBot.getLogger().debug("Adding command {}", slashCommand.getClass().getName());
            commands.put(slashCommand.getName(), slashCommand);
        }
    }

    public void registerAdminCommands(AdminCommand... abstractAdminCommands) {
        for (AdminCommand abstractAdminCommand : abstractAdminCommands)
            registerAdminCommand(abstractAdminCommand);
    }

    public void registerAdminCommand(AdminCommand abstractAdminCommand) {
        discordBot.getLogger().debug("Adding command {} (admin)", abstractAdminCommand.getClass().getName());
        adminCommands.put(abstractAdminCommand.getName(), abstractAdminCommand);
    }

    public SlashCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

    public AdminCommand getAdminCommand(String commandName) {
        return adminCommands.get(commandName);
    }

    public void updateCommandsToDiscord() {
        discordBot.getLogger().debug("Updating commands to Discord");

        List<SlashCommandData> commands = new ArrayList<>();

        for (SlashCommand abstractCommand : this.commands.values())
            if (abstractCommand.isEnabled())
                commands.add(abstractCommand.getCommandData());

        discordBot.getGuildManager().getGuild().updateCommands().addCommands(commands).queue();
    }
}