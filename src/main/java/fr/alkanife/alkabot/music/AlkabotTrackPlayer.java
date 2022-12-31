package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class AlkabotTrackPlayer {

    private final MusicManager musicManager;
    private boolean retrying = false;

    public AlkabotTrackPlayer(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    public void play(AlkabotTrack alkabotTrack) {
        if (alkabotTrack == null)
            throw new NullPointerException("The track cannot be null!");

        Alkabot.debug("Trying to play '" + alkabotTrack.getTitle() + "' by '" + alkabotTrack.getArtists() + "' (" + alkabotTrack.getUrl() + ")...");

        musicManager.getAudioPlayerManager().loadItemOrdered(musicManager.getPlayer(), alkabotTrack.getQuery(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                retrying = false;
                Alkabot.debug("Now playing: " + track.getInfo().uri);
                musicManager.getPlayer().startTrack(track, false);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // spotify, likely
                retrying = false;

                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = playlist.getTracks().get(0);

                if (alkabotTrack.getQuery().startsWith("ytsearch")) {
                    Alkabot.debug("Now playing (from spotify query): " + firstTrack.getInfo().uri);
                    musicManager.getPlayer().startTrack(firstTrack, false);
                }
            }

            @Override
            public void noMatches() {
                retrying = false;
                Alkabot.debug("No matches!");

                if (musicManager.getLastMusicCommandChannel() != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-notfound-title"));
                    embedBuilder.setColor(Colors.BIG_RED);
                    embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ")"
                            + " " + Alkabot.t("jukebox-by") + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ")\n\n" +
                            Alkabot.t("jukebox-playing-error-added-by") + " <@" + alkabotTrack.getAddedByID() + ">" + "\n" +
                            Alkabot.t("jukebox-playing-error-origin") + " " + alkabotTrack.getTrackSource() + "\n\n" +
                            Alkabot.t("jukebox-playing-error-notfound-description"));
                    embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                    musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }

                play(musicManager.getTrackScheduler().getQueue().poll());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (retrying) {
                    if (musicManager.getLastMusicCommandChannel() != null) {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-title"));
                        embedBuilder.setColor(Colors.BIG_RED);
                        embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ")"
                                + " " + Alkabot.t("jukebox-by") + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ")\n\n" +
                                Alkabot.t("jukebox-playing-error-added-by") + " <@" + alkabotTrack.getAddedByID() + ">" + "\n" +
                                Alkabot.t("jukebox-playing-error-origin") + " " + alkabotTrack.getTrackSource() + "\n\n" +
                                Alkabot.t("jukebox-playing-error-message"));
                        embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                        musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    }

                    retrying = false;
                    play(musicManager.getTrackScheduler().getQueue().poll());
                    Alkabot.debug("Failed to play this track! Skipping...");
                } else {
                    Alkabot.debug("Failed to play this track! Retrying...");
                    retrying = true;
                    play(alkabotTrack);
                }
            }
        });
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }
}
