package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class QueueCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.queue.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isQueue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "input", Alkabot.t("command.music.queue.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}