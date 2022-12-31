package fr.alkanife.alkabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class AbstractCommand {

    public abstract String getName();
    public abstract String getDescription();
    public abstract String exampleUsage();
    public abstract boolean isEnabled();
    public abstract SlashCommandData getCommandData();

    public abstract void execute(SlashCommandInteractionEvent event);
}
