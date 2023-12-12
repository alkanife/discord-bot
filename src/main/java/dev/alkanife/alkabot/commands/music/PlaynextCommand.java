package dev.alkanife.alkabot.commands.music;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
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
        return Lang.get("command.music.playnext.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isPlaynext();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "query", Lang.get("command.music.playnext.input.query"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().playCommand(event, "playnext", 0, false);
    }
}
