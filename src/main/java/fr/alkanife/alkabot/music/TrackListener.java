package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.util.ArrayList;
import java.util.List;

public class TrackListener extends AudioEventAdapter {

    private final MusicManager musicManager;

    private List<String> retriedTracks = new ArrayList<>();

    public TrackListener(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioChannel voiceChannel = Alkabot.getGuild().getAudioManager().getConnectedChannel();

        if (voiceChannel != null) {
            if (voiceChannel.getMembers().size() == 1) {
                Alkabot.getMusicManager().reset();

                if (Alkabot.getMusicManager().getLastMusicCommandChannel() != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-nomembers-title"));
                    embedBuilder.setColor(Colors.BIG_RED);
                    embedBuilder.setDescription(Alkabot.t("jukebox-playing-error-nomembers-desc"));

                    Alkabot.getMusicManager().getLastMusicCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }
                return;
            }
        }

        if (endReason.mayStartNext)
            musicManager.goNext();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (retriedTracks.contains(track.getInfo().title)) {
            embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-title"));
            embedBuilder.setColor(Colors.BIG_RED);
            embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                    + " " + Alkabot.t("jukebox-by") + " [" + track.getInfo().author + "](" + track.getInfo().uri + ")\n\n" +
                    Alkabot.t("jukebox-playing-error-message"));
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");
        } else {
            embedBuilder.setTitle(Alkabot.t("jukebox-command-play-added-title") + " " + Alkabot.t("jukebox-command-priority"));
            embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ") " + Alkabot.t("jukebox-by")
                    + " [" + track.getInfo().author + "](" + track.getInfo().uri + ") " + StringUtils.durationToString(track.getDuration(), true, false) +
                    "\n\n" + Alkabot.t("jukebox-playing-retrying"));
            embedBuilder.setColor(Colors.CYAN);
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");

            retriedTracks.add(track.getInfo().title);

            musicManager.getTrackScheduler().queue(new AlkabotTrack(track, Alkabot.getJDA().getSelfUser().getName(), Alkabot.getJDA().getSelfUser().getId(), true));
        }

        if (musicManager != null)
            musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

}
