package dev.alkanife.alkabot.command.music;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.music.MusicManager;
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
                .addOption(OptionType.INTEGER, "tracks", Lang.get("command.music.skip.tracks"), false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = alkabot.getMusicManager();

        if (musicManager.getPlayer().getPlayingTrack() == null) {
            event.reply(Lang.get("command.music.skip.error.not_playing")).queue();
            return;
        }

        OptionMapping skipSize = event.getOption("tracks");
        int skip = 0;
        if (skipSize != null) {
            long skipLong = skipSize.getAsLong();

            if (skipLong >= musicManager.getTrackScheduler().getQueue().size() || skipLong <= 0) {
                event.reply(Lang.get("command.music.skip.error.invalid")).queue();
                return;
            }

            for (skip = 0; skip < skipLong; skip++)
                musicManager.getTrackScheduler().getQueue().remove();
        }

        musicManager.goNext();

        if (skipSize == null || skip == 0)
            event.reply(Lang.get("command.music.skip.success.one")).queue();
        else
            event.reply(Lang.t("command.music.skip.success.multiple").parse("tracks", String.valueOf(skip)).getValue()).queue();
    }
}
