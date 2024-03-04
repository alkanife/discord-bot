package dev.alkanife.alkabot.command.music;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class NowplayingCommand extends AbstractCommand {

    public NowplayingCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.nowplaying.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isNowplaying();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (alkabot.getMusicManager().getPlayer().getPlayingTrack() == null) {
            event.reply(Lang.get("command.music.nowplaying.error.not_playing")).queue();
            return;
        }

        event.deferReply().queue();
        alkabot.getMusicManager().nowPlaying(event);
    }
}
