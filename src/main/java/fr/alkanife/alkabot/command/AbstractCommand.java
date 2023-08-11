package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class AbstractCommand {

    public CommandManager commandManager;
    public Alkabot alkabot;

    public AbstractCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.alkabot = commandManager.getAlkabot();
    }

    public abstract String getName();
    public abstract String getDescription();
    public abstract boolean isEnabled();
    public abstract SlashCommandData getCommandData();

    public abstract void execute(SlashCommandInteractionEvent event);
}
