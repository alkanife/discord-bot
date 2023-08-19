package fr.alkanife.alkabot.commands.music;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.music.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SkipCommand extends AbstractCommand {

    public SkipCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.skip.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isSkip();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "input", Lang.get("command.music.skip.input_description"), false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = alkabot.getMusicManager();

        if (musicManager.getPlayer().getPlayingTrack() == null) {
            event.reply(Lang.get("command.music.generic.not_playing")).queue();
            return;
        }

        OptionMapping skipSize = event.getOption("input");
        int skip = 0;
        if (skipSize != null) {
            long skipLong = skipSize.getAsLong();

            if (skipLong >= musicManager.getTrackScheduler().getQueue().size()) {
                event.reply(Lang.get("command.music.generic.not_enough")).queue();
                return;
            }

            for (skip = 0; skip < skipLong; skip++)
                musicManager.getTrackScheduler().getQueue().remove();
        }

        musicManager.goNext();

        if (skipSize == null)
            event.reply(Lang.get("command.music.skip.one")).queue();
        else
            event.reply(Lang.get("command.music.skip.mult") + skip).queue();
    }
}
