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
        if (musicManager.getAlkabot().getConfig().getMusicConfig().isAutoStop()) {
            AudioChannel voiceChannel = musicManager.getAlkabot().getGuild().getAudioManager().getConnectedChannel();

            if (voiceChannel != null) {
                if (voiceChannel.getMembers().size() == 1) {
                    if (!endReason.equals(AudioTrackEndReason.STOPPED)) {
                        musicManager.getAlkabot().verbose("Stopping the music because I'm alone");
                        musicManager.getAlkabot().getMusicManager().reset();

                        if (musicManager.getAlkabot().getMusicManager().getLastMusicCommandChannel() != null) {
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle(musicManager.getAlkabot().t("command.music.generic.alone.title"));
                            embedBuilder.setColor(Colors.BIG_RED);
                            embedBuilder.setDescription(musicManager.getAlkabot().t("command.music.generic.alone.description"));

                            musicManager.getAlkabot().getMusicManager().getLastMusicCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                        }
                        return;
                    }
                }
            }
        }

        if (endReason.mayStartNext)
            musicManager.goNext();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        if (retriedTracks.contains(track.getInfo().title)) {
            musicManager.getAlkabot().verbose("Failed to play '" + track.getInfo().title + "'");

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(musicManager.getAlkabot().t("command.music.play.error.fail.title"));
            embedBuilder.setColor(Colors.BIG_RED);
            embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                    + " " + musicManager.getAlkabot().t("command.music.generic.by") + " [" + track.getInfo().author + "](" + track.getInfo().uri + ")\n\n" +
                    musicManager.getAlkabot().t("command.music.play.error.fail.message"));
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");

            if (musicManager != null)
                musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            musicManager.getAlkabot().verbose("Retrying to play '" + track.getInfo().title + "'...");
            retriedTracks.add(track.getInfo().title);

            musicManager.getTrackScheduler().queue(new AlkabotTrack(track, musicManager.getAlkabot().getJda().getSelfUser().getName(), musicManager.getAlkabot().getJda().getSelfUser().getId(), true), false);
        }
    }
}
