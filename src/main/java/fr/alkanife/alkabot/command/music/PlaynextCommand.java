package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PlaynextCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "playnext";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.playnext.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isPlaynext();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "input", Alkabot.t("command.music.play.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        PlayCommand.play(event, true, false);
    }
}
