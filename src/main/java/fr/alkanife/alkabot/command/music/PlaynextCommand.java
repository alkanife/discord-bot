package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PlaynextCommand extends AbstractCommand {

    public PlaynextCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "playnext";
    }

    @Override
    public String getDescription() {
        return alkabot.t("command.music.playnext.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isPlaynext();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "input", alkabot.t("command.music.play.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        PlayCommand.play(event, true, false);
    }
}
