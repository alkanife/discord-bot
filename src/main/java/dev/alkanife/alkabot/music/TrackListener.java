package dev.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.alkanife.alkabot.lang.Lang;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackListener extends AudioEventAdapter {

    @Getter
    private final MusicManager musicManager;

    private final List<String> retriedTracks = new ArrayList<>();

    public TrackListener(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (musicManager.getAlkabot().getConfig().getMusicConfig().isAutoStop()) {
            AudioChannel voiceChannel = musicManager.getAlkabot().getGuild().getAudioManager().getConnectedChannel();

            if (voiceChannel != null) {
                if (voiceChannel.getMembers().size() == 1) {
                    if (!endReason.equals(AudioTrackEndReason.STOPPED)) {
                        musicManager.getAlkabot().getLogger().debug("Stopping the music because I'm alone");
                        player.stopTrack();
                        musicManager.getTrackScheduler().setQueue(new LinkedBlockingQueue<>());
                        musicManager.getAlkabot().getGuild().getAudioManager().closeAudioConnection();

                        if (musicManager.getAlkabot().getMusicManager().getLastMusicCommandChannel() != null) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle(
                                    Lang.t("music.auto_stop.title")
                                            .parseGuildName(musicManager.getAlkabot().getGuild())
                                            .parseChannelName(musicManager.getAlkabot().getMusicManager().getLastMusicCommandChannel())
                                            .getValue()
                            );
                            embed.setColor(Lang.getColor("music.auto_stop.color"));
                            embed.setThumbnail(
                                    Lang.t("music.auto_stop.icon")
                                            .parseGuildAvatar(musicManager.getAlkabot().getGuild())
                                            .parseBotAvatars(musicManager.getAlkabot())
                                            .getImage()
                            );
                            embed.setDescription(
                                    Lang.t("music.auto_stop.description")
                                            .parseChannel(musicManager.getAlkabot().getMusicManager().getLastMusicCommandChannel())
                                            .parseGuildName(musicManager.getAlkabot().getGuild())
                                            .parseQueue(musicManager)
                                            .getValue()
                            );

                            musicManager.getAlkabot().getMusicManager().getLastMusicCommandChannel().sendMessageEmbeds(embed.build()).queue();
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
            musicManager.getAlkabot().getLogger().debug("Failed to play '" + track.getInfo().title + "'");

            AlkabotTrack alkabotTrack = new AlkabotTrack(track, musicManager.getAlkabot().getJda().getSelfUser().getId());

            EmbedBuilder embed = MusicUtils.createGenericMusicFailEmbed(alkabotTrack, musicManager);

            if (musicManager.getLastMusicCommandChannel() != null)
                musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embed.build()).queue();

            retriedTracks.remove(track.getInfo().title);
        } else {
            musicManager.getAlkabot().getLogger().debug("Retrying to play '" + track.getInfo().title + "'...");
            retriedTracks.add(track.getInfo().title);

            musicManager.getTrackScheduler().queue(new AlkabotTrack(track, musicManager.getAlkabot().getJda().getSelfUser().getId()), 0, false);
        }
    }
}
