package fr.alkanife.alkabot.music.loader;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.music.AbstractMusic;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.utils.StringUtils;
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

public class SpotifyLoader extends AbstractMusic {

    public SpotifyLoader(MusicManager musicManager) {
        super(musicManager);
    }

    public void load(SlashCommandInteractionEvent slashCommandInteractionEvent, final String url, boolean priority) {
        if (!Alkabot.supportSpotify())
            return;

        Alkabot.debug("Loading music from '" + url + "' (" + priority + ")...");

        Alkabot.debug("Requesting Spotify client credentials...");
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(Alkabot.getTokens().getSpotify().getClient_id())
                .setClientSecret(Alkabot.getTokens().getSpotify().getClient_secret())
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

        Alkabot.debug("Requesting Spotify playlist info...");
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

            getMusicManager().getTrackScheduler().queuePlaylist(alkabotTrackList.get(0), alkabotTrackList);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("jukebox-command-play-playlist-added") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
            embedBuilder.setDescription("[" + Alkabot.t("jukebox-command-spotify-playlist") + "](" + url + ")\n\n" +
                    Alkabot.t("jukebox-command-play-playlist-entries") + " `" + alkabotTrackList.size() + "`\n" +
                    Alkabot.t("jukebox-command-play-playlist-newtime") + " `" + StringUtils.durationToString(getMusicManager().getTrackScheduler().getQueueDuration(), false, true) + "`");

            embedBuilder.setThumbnail(alkabotTrackList.get(0).getThumbUrl());

            slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            Alkabot.debug("Spotify playlist loaded! (" + alkabotTrackList.size() + " tracks)");
            Alkabot.debug("First track url: " + alkabotTrackList.get(0).getUrl());
        } catch (Exception e) {
            Alkabot.getLogger().error("Failed to get spotify playlist:");
            e.printStackTrace();
            slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("jukebox-command-play-spotify-error")).queue();
        }
    }

}
