package dev.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alkanife.alkabot.lang.Lang;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;

public class AlkabotTrackPlayer {

    @Getter
    private final MusicManager musicManager;
    private boolean retrying = false;

    public AlkabotTrackPlayer(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    public void play(AlkabotTrack alkabotTrack) {
        if (alkabotTrack == null) {
            musicManager.getAlkabot().getLogger().debug("End of queue");
            return;
        }

        musicManager.getAlkabot().getLogger().debug("Trying to play '" + alkabotTrack.getTitle() + "' by '" + alkabotTrack.getArtists() + "' (" + alkabotTrack.getUrl() + ")...");

        musicManager.getAudioPlayerManager().loadItemOrdered(musicManager.getPlayer(), alkabotTrack.getQuery(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                retrying = false;
                musicManager.getAlkabot().getLogger().debug("Now playing: " + track.getInfo().uri);
                musicManager.getPlayer().startTrack(track, false);
                musicManager.getTrackScheduler().setNowPlaying(alkabotTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // spotify, likely
                retrying = false;

                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = playlist.getTracks().get(0);

                if (alkabotTrack.getQuery().startsWith("ytsearch")) {
                    musicManager.getAlkabot().getLogger().debug("Now playing (from spotify query): " + firstTrack.getInfo().uri);
                    musicManager.getPlayer().startTrack(firstTrack, false);
                    musicManager.getTrackScheduler().setNowPlaying(alkabotTrack);
                }
            }

            @Override
            public void noMatches() {
                retrying = false;
                musicManager.getAlkabot().getLogger().debug("No matches!");

                if (musicManager.getLastMusicCommandChannel() != null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(
                            Lang.t("music.not_found_spotify.title")
                                    .parseGuildName(musicManager.getAlkabot().getGuild())
                                    .getValue()
                    );
                    embed.setColor(Lang.getColor("music.not_found_spotify.color"));
                    embed.setThumbnail(
                            Lang.t("music.not_found_spotify.icon")
                                    .parseGuildAvatar(musicManager.getAlkabot().getGuild())
                                    .parseBotAvatars(musicManager.getAlkabot())
                                    .parseTrackThumbnail(alkabotTrack)
                                    .getValue()
                    );
                    embed.setDescription(
                            Lang.t("music.not_found_spotify.description")
                                    .parseGuildName(musicManager.getAlkabot().getGuild())
                                    .parseQueue(musicManager)
                                    .parseTrack(alkabotTrack)
                                    .getValue()
                    );

                    musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embed.build()).queue();
                }

                play(musicManager.getTrackScheduler().getQueue().poll());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (retrying) {
                    if (musicManager.getLastMusicCommandChannel() != null) {
                        EmbedBuilder embed = MusicUtils.createGenericMusicFailEmbed(alkabotTrack, musicManager);
                        musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embed.build()).queue();
                    }

                    retrying = false;
                    play(musicManager.getTrackScheduler().getQueue().poll());
                    musicManager.getAlkabot().getLogger().debug("Failed to play this track! Skipping...");
                } else {
                    musicManager.getAlkabot().getLogger().debug("Failed to play this track! Retrying...");
                    retrying = true;
                    play(alkabotTrack);
                }

                musicManager.getAlkabot().getLogger().debug("Failed to LOAD (player)!");
                musicManager.getAlkabot().getLogger().error("vvvvvvvvvvvvvvv----------------------");
                musicManager.getAlkabot().getLogger().error("error:", exception);
                musicManager.getAlkabot().getLogger().error("^^^^^^^^^^^^^^^----------------------");
            }
        });
    }
}
