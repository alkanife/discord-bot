package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.lang.Lang;
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
        if (alkabotTrack == null)
            throw new NullPointerException("The track cannot be null!");

        musicManager.getAlkabot().verbose("Trying to play '" + alkabotTrack.getTitle() + "' by '" + alkabotTrack.getArtists() + "' (" + alkabotTrack.getUrl() + ")...");

        musicManager.getAudioPlayerManager().loadItemOrdered(musicManager.getPlayer(), alkabotTrack.getQuery(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                retrying = false;
                musicManager.getAlkabot().verbose("Now playing: " + track.getInfo().uri);
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
                    musicManager.getAlkabot().verbose("Now playing (from spotify query): " + firstTrack.getInfo().uri);
                    musicManager.getPlayer().startTrack(firstTrack, false);
                }
            }

            @Override
            public void noMatches() {
                retrying = false;
                musicManager.getAlkabot().verbose("No matches!");

                if (musicManager.getLastMusicCommandChannel() != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Lang.get("command.music.play.error.not_found.title"));
                    //embedBuilder.setColor(Colors.BIG_RED);
                    embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ")"
                            + " " + Lang.get("command.music.generic.by") + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ")\n\n" +
                            Lang.get("command.music.generic.added_by") + " <@" + alkabotTrack.getAddedByID() + ">" + "\n" +
                            Lang.get("command.music.generic.origin") + " " + alkabotTrack.getTrackSource() + "\n\n" +
                            Lang.get("command.music.play.error.not_found.description"));
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
                        embedBuilder.setTitle(Lang.get("command.music.play.error.fail.title"));
                        //embedBuilder.setColor(Colors.BIG_RED);
                        embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ")"
                                + " " + Lang.get("command.music.generic.by") + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ")\n\n" +
                                Lang.get("command.music.generic.added_by") + " <@" + alkabotTrack.getAddedByID() + ">" + "\n" +
                                Lang.get("command.music.generic.origin") + " " + alkabotTrack.getTrackSource() + "\n\n" +
                                Lang.get("command.music.play.error.fail.message"));
                        embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                        musicManager.getLastMusicCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    }

                    retrying = false;
                    play(musicManager.getTrackScheduler().getQueue().poll());
                    musicManager.getAlkabot().verbose("Failed to play this track! Skipping...");
                } else {
                    musicManager.getAlkabot().verbose("Failed to play this track! Retrying...");
                    retrying = true;
                    play(alkabotTrack);
                }
            }
        });
    }
}
