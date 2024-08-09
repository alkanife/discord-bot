package dev.alkanife.alkabot.command.admin.general;

import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.command.admin.AdminCommand;
import dev.alkanife.alkabot.command.admin.AdminCommandTarget;
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
        alkabot.getJda().getRestPing().queue(aLong -> event.getMessage().reply("Pong! (" + aLong + " ms)").queue());
    }

    @Override
    public void handleTerminal(String query) {
        alkabot.getJda().getRestPing().queue(aLong -> replyTerminal("Pong! (" + aLong + " ms)"));
    }
}
