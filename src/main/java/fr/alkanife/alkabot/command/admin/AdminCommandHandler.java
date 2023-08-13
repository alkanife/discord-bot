package fr.alkanife.alkabot.command.admin;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommandHandler;

public class AdminCommandHandler extends AbstractCommandHandler {

    public AdminCommandHandler(Alkabot alkabot, AdminCommandExecution execution) {
        super(alkabot);

        try {
            String[] command = execution.command().split(" ");

            if (command[0].equalsIgnoreCase("help")) {
                adminHelp(execution);
                return;
            }

            AbstractAdminCommand abstractAdminCommand = alkabot.getCommandManager().getAdminCommand(command[0]);

            if (abstractAdminCommand == null) {
                execution.reply("Unknown command. Type 'help' to see a list of administrative commands");
                return;
            }

            if (execution.isFromDiscord())
                alkabot.getLogger().info(execution.messageReceivedEvent().getAuthor().getName() + " executed admin command '" + execution.command() + "'");

            if (abstractAdminCommand.isDiscordOnly() && !execution.isFromDiscord()) {
                alkabot.getLogger().error("This command can only be executed from Discord.");
                return;
            }

            abstractAdminCommand.execute(execution);
        } catch (Exception exception) {
            execution.reply("An error prevented me from processing your command.");
            alkabot.getLogger().error("Failed to handle an admin command:", exception);
        }
    }

    public void adminHelp(AdminCommandExecution execution) {
        StringBuilder stringBuilder = new StringBuilder("Administrative commands:");

        for (AbstractAdminCommand command : getAlkabot().getCommandManager().getAdminCommands().values())
            stringBuilder.append("\n - ").append(command.getUsage()).append(": ").append(command.getDescription());

        execution.reply(stringBuilder.toString());
    }

}
