package fr.alkanife.alkabot.commands;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class AboutCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.about.description");
    }

    @Override
    public String exampleUsage() {
        return "about";
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().isAbout();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}
