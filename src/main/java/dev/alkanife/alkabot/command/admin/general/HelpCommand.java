package dev.alkanife.alkabot.command.admin.general;

import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.command.admin.AdminCommand;
import dev.alkanife.alkabot.command.admin.AdminCommandTarget;
import dev.alkanife.alkabot.util.command.CommandList;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;
import java.util.List;

public class HelpCommand extends AdminCommand {

    public HelpCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "help [page]";
    }

    @Override
    public String getDescription() {
        return "Display admin commands (pages are only for Discord users)";
    }

    @Override
    public AdminCommandTarget getCommandTarget() {
        return AdminCommandTarget.TERMINAL_AND_DISCORD;
    }

    @Override
    public void handleDiscord(String query, MessageReceivedEvent event) { // #TODO: pagination, but only when there will be like >15 admin commands (which is very unlikely)
        List<String> commands = getCommands(AdminCommandTarget.TERMINAL);

        StringBuilder reply = new StringBuilder("```asciidoc\n");
        reply.append("[ Admin commands: ").append(commandManager.getAdminCommands().size()).append(" - page 1/1 ]\n\n");
        reply.append(" Some commands may not work inside Discord and are meant to be executed inside the terminal.\n");
        reply.append(" These commands are marked with an asterisk *.\n\n");

        for (String command : commands)
            reply.append("- ").append(command).append("\n");

        reply.append("```");

        event.getMessage().reply(reply.toString()).queue();
    }

    @Override
    public void handleTerminal(String query) {
        List<String> commands = getCommands(AdminCommandTarget.DISCORD);

        replyTerminal("--------------------");
        replyTerminal("Some commandes may not work inside the Terminal, and are meant to be executed in a Discord channel.");
        replyTerminal("These commands are marked with an asterisk *.");
        replyTerminal(" ");
        replyTerminal("Admin commands (" + commandManager.getAdminCommands().size() + "):");
        replyTerminal(" ");

        for (String command : commands)
            replyTerminal(command);

        replyTerminal("--------------------");
    }

    private List<String> getCommands(AdminCommandTarget warnTarget) {
        LinkedHashMap<String, String> commands = new LinkedHashMap<>();

        for (AdminCommand adminCommand : commandManager.getAdminCommands().values()) {
            if (adminCommand.getCommandTarget().equals(warnTarget)) {
                commands.put(adminCommand.getUsage() + "*", adminCommand.getDescription());
            } else {
                commands.put(adminCommand.getUsage(), adminCommand.getDescription());
            }
        }

        CommandList commandList = new CommandList(commands);
        return commandList.getLines();
    }
}
