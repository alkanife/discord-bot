package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.music.playlist.Playlist;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PlayCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.play.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isPlay();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "input", Alkabot.t("command.music.play.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        play(event, false, false);
    }

    public static void play(SlashCommandInteractionEvent event, boolean priority, boolean force) {
        Alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        event.deferReply().queue();

        Alkabot.getMusicManager().connect(event.getMember());

        String url = event.getOption("input").getAsString();

        if (url.startsWith("https://open.spotify.com/playlist")) {
            Alkabot.getMusicManager().getSpotifyLoader().load(event, url, priority, force);
        } else {
            if (!StringUtils.isURL(url)) {
                Playlist playlist = Alkabot.getPlaylistManager().getPlaylist(url);

                if (playlist == null)
                    url = "ytsearch: " + url;
                else
                    url = playlist.getUrl();
            }

            Alkabot.getMusicManager().getLavaplayerLoader().load(event, url, priority, force);
        }
    }
}
