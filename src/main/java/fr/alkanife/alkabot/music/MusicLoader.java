package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicLoader {

    public static boolean loadRetrying = false;
    public static boolean playLoadRetrying = false;

    // From command
    public static void load(SlashCommandInteractionEvent slashCommandInteractionEvent, final String url, boolean priority) {
        Alkabot.getAudioPlayerManager().loadItemOrdered(Alkabot.getAudioPlayer(), url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                loadRetrying = false;

                String id = "";
                if (slashCommandInteractionEvent.getMember() != null)
                    id = slashCommandInteractionEvent.getMember().getId();

                AlkabotTrack alkabotTrack = new AlkabotTrack(track, Alkabot.t("jukebox-command-play-source-url"), id, priority);

                Alkabot.getTrackScheduler().queue(alkabotTrack);

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(Alkabot.t("jukebox-command-play-added-title") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
                embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ") " + Alkabot.t("jukebox-by")
                        + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ") " + Alkabot.musicDuration(alkabotTrack.getDuration()) +
                        (priority ? "" : ("\n\n" + (Alkabot.t("jukebox-command-play-added-position") + " `" + (Alkabot.getTrackScheduler().getQueue().size() + 1) + "`"))));
                embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                loadRetrying = false;

                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = playlist.getTracks().get(0);

                String id = "";
                if (slashCommandInteractionEvent.getMember() != null)
                    id = slashCommandInteractionEvent.getMember().getId();

                if (url.startsWith("ytsearch")) {
                    AlkabotTrack alkabotTrack = new AlkabotTrack(firstTrack, Alkabot.t("jukebox-command-play-source-yt-search"), id, priority);

                    Alkabot.getTrackScheduler().queue(alkabotTrack);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-command-play-added-title") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
                    embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ") " + Alkabot.t("jukebox-by")
                            + " [" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ") " + Alkabot.musicDuration(alkabotTrack.getDuration()) +
                            (priority ? "" : ("\n\n" + (Alkabot.t("jukebox-command-play-added-position") + " `" + (Alkabot.getTrackScheduler().getQueue().size() + 1) + "`"))));
                    embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                } else {
                    List<AlkabotTrack> alkabotTrackList = new ArrayList<>();

                    String source = Alkabot.t("jukebox-command-play-source-yt-search-playlist") + " / \"" + playlist.getName() + "\"";

                    for (AudioTrack audioTrack : playlist.getTracks())
                        alkabotTrackList.add(new AlkabotTrack(audioTrack, source, id, priority));

                    AlkabotTrack firstAlkabotTrack = new AlkabotTrack(firstTrack, source, id, priority);

                    Alkabot.getTrackScheduler().queuePlaylist(firstAlkabotTrack, alkabotTrackList);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-command-play-playlist-added") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
                    embedBuilder.setDescription("[" + playlist.getName() + "](" + url + ")\n\n" +
                            Alkabot.t("jukebox-command-play-playlist-entries") + " `" + playlist.getTracks().size() + "`\n" +
                            Alkabot.t("jukebox-command-play-playlist-newtime") + " `" + Alkabot.playlistDuration(Alkabot.getTrackScheduler().getQueueDuration()) + "`");

                    embedBuilder.setThumbnail(firstAlkabotTrack.getThumbUrl());

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                loadRetrying = false;

                slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("jukebox-command-play-nomatches")).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                Alkabot.getLogger().warn("Load fail - retry = " + loadRetrying);
                if (loadRetrying) {
                    slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("jukebox-command-play-error")).queue();
                    loadRetrying = false;
                } else {
                    loadRetrying = true;
                    MusicLoader.load(slashCommandInteractionEvent, url, priority);
                }
            }
        });
    }

    public static void loadSpotifyPlaylist(SlashCommandInteractionEvent slashCommandInteractionEvent, final String url, boolean priority) {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(Alkabot.getConfig().getSpotify().getClient_id())
                .setClientSecret(Alkabot.getConfig().getSpotify().getClient_secret())
                .build();

        // access token
        String access = "";

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();

        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            access = clientCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            Alkabot.getLogger().error("Failed to get spotify client credentials:");
            e.printStackTrace();
            slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("jukebox-command-play-spotify-error")).queue();
        }
        spotifyApi.setAccessToken(access);

        // tracks playlists
        String id = url.replaceAll("https://open.spotify.com/playlist/", "");

        try {
            GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi
                    .getPlaylistsItems(id)
                    .build();

            final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

            String memberID = "";
            if (slashCommandInteractionEvent.getMember() != null)
                memberID = slashCommandInteractionEvent.getMember().getId();

            String source = Alkabot.t("jukebox-command-play-source-spotify-playlist") + " / \"" + url + "\"";

            List<AlkabotTrack> alkabotTrackList = new ArrayList<>();

            for (PlaylistTrack playlistTrack : playlistTrackPaging.getItems()) {
                Track track = (Track) playlistTrack.getTrack();
                if (track != null)
                    alkabotTrackList.add(new AlkabotTrack(track, source, memberID, priority));
            }

            Alkabot.getTrackScheduler().queuePlaylist(alkabotTrackList.get(0), alkabotTrackList);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("jukebox-command-play-playlist-added") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
            embedBuilder.setDescription("[" + Alkabot.t("jukebox-command-spotify-playlist") + "](" + url + ")\n\n" +
                    Alkabot.t("jukebox-command-play-playlist-entries") + " `" + alkabotTrackList.size() + "`\n" +
                    Alkabot.t("jukebox-command-play-playlist-newtime") + " `" + Alkabot.playlistDuration(Alkabot.getTrackScheduler().getQueueDuration()) + "`");

            embedBuilder.setThumbnail(alkabotTrackList.get(0).getThumbUrl());

            slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (Exception e) {
            Alkabot.getLogger().error("Failed to get spotify playlist:");
            e.printStackTrace();
            slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("jukebox-command-play-spotify-error")).queue();
        }
    }

    // From the queue
    public static void play(AlkabotTrack alkabotTrack) {
        if (alkabotTrack == null)
            throw new NullPointerException("The track cannot be null!");

        Alkabot.getAudioPlayerManager().loadItemOrdered(Alkabot.getAudioPlayer(), alkabotTrack.getQuery(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                playLoadRetrying = false;
                Alkabot.getTrackScheduler().getPlayer().startTrack(track, false);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // spotify, likely
                playLoadRetrying = false;

                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = playlist.getTracks().get(0);

                if (alkabotTrack.getQuery().startsWith("ytsearch"))
                    Alkabot.getTrackScheduler().getPlayer().startTrack(firstTrack, false);
            }

            @Override
            public void noMatches() {
                playLoadRetrying = false;

                if (Alkabot.getLastCommandChannel() != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-notfound-title"));
                    embedBuilder.setColor(Colors.BIG_RED);
                    embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ")"
                            + " " + Alkabot.t("jukebox-by") + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ")\n\n" +
                            Alkabot.t("jukebox-playing-error-added-by") + " <@" + alkabotTrack.getAddedByID() + ">" + "\n" +
                            Alkabot.t("jukebox-playing-error-origin") + " " + alkabotTrack.getTrackSource() + "\n\n" +
                            Alkabot.t("jukebox-playing-error-notfound-description"));
                    embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                    Alkabot.getLastCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }

                MusicLoader.play(Alkabot.getTrackScheduler().getQueue().poll());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (playLoadRetrying) {
                    if (Alkabot.getLastCommandChannel() != null) {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-title"));
                        embedBuilder.setColor(Colors.BIG_RED);
                        embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ")"
                                + " " + Alkabot.t("jukebox-by") + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ")\n\n" +
                                Alkabot.t("jukebox-playing-error-added-by") + " <@" + alkabotTrack.getAddedByID() + ">" + "\n" +
                                Alkabot.t("jukebox-playing-error-origin") + " " + alkabotTrack.getTrackSource() + "\n\n" +
                                Alkabot.t("jukebox-playing-error-message"));
                        embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                        Alkabot.getLastCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    }

                    playLoadRetrying = false;
                    MusicLoader.play(Alkabot.getTrackScheduler().getQueue().poll());
                } else {
                    playLoadRetrying = true;
                    MusicLoader.play(alkabotTrack);
                }
            }
        });
    }
}