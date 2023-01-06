package fr.alkanife.alkabot.command.admin;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractAdminCommand;
import fr.alkanife.alkabot.command.AdminCommandExecution;

public class PingCommand extends AbstractAdminCommand {

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
        return "Pong!";
    }

    @Override
    public boolean isDiscordOnly() {
        return true;
    }

    @Override
    public void execute(AdminCommandExecution execution) {
        Alkabot.getJda().getRestPing().queue(aLong -> execution.getMessageReceivedEvent().getMessage().reply("Pong! (" + aLong + " ms)").queue());
    }
}
