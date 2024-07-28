package dev.alkanife.alkabot.command.admin;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.command.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class AdminCommand {

    public CommandManager commandManager;
    public Alkabot alkabot;

    public AdminCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.alkabot = commandManager.getAlkabot();
    }

    public abstract String getName();
    public abstract String getUsage();
    public abstract String getDescription();
    public abstract AdminCommandTarget getCommandTarget();

    public abstract void handleDiscord(String query, MessageReceivedEvent event);
    public abstract void handleTerminal(String query);

    public void replyTerminal(String message) {
        alkabot.getLogger().info(">{}] {}", getName(), message);
    }
}
