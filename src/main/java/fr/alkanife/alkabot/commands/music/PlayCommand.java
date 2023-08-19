package fr.alkanife.alkabot.commands.music;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PlayCommand extends AbstractCommand {

    public PlayCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.play.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isPlay();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "query", Lang.get("command.music.play.input.query"), true)
                .addOption(OptionType.INTEGER, "position", Lang.get("command.music.play.input.position"), false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping posOption = event.getOption("position");
        int position = 9999999;

        if (posOption != null) {
            position = posOption.getAsInt();

            if (position < 0)
                position = 0;
        }

        alkabot.getMusicManager().playCommand(event, "play", position, false);
    }
}
