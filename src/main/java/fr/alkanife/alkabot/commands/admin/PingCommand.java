package fr.alkanife.alkabot.commands.admin;

import fr.alkanife.alkabot.command.admin.AbstractAdminCommand;
import fr.alkanife.alkabot.command.admin.AdminCommandExecution;
import fr.alkanife.alkabot.command.CommandManager;

public class PingCommand extends AbstractAdminCommand {

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
        return "Pong!";
    }

    @Override
    public boolean isDiscordOnly() {
        return true;
    }

    @Override
    public void execute(AdminCommandExecution execution) {
        alkabot.getJda().getRestPing().queue(aLong -> execution.messageReceivedEvent().getMessage().reply("Pong! (" + aLong + " ms)").queue());
    }
}
