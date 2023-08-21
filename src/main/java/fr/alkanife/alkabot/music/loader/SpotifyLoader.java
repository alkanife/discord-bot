package fr.alkanife.alkabot.music.loader;

import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.music.*;
import fr.alkanife.alkabot.util.StringUtils;
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

    public void load(SlashCommandInteractionEvent event, final String commandSource, final String query, final int position, boolean skipCurrent) {
        if (!musicManager.getAlkabot().isSpotifySupport())
            return;

        musicManager.getAlkabot().getLogger().debug("Loading spotify playlist from '" + query + "' (pos=" + position + ", skipCurrent=" + skipCurrent + ")");

        musicManager.getAlkabot().getLogger().debug("Requesting Spotify client credentials...");
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(musicManager.getAlkabot().getTokens().getSpotify().getClientId())
                .setClientSecret(musicManager.getAlkabot().getTokens().getSpotify().getClientSecret())
                .build();

        // access token
        String access = "";

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();

        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            access = clientCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            musicManager.getAlkabot().getLogger().error("Failed to get spotify client credentials:", e);
            event.getHook().sendMessage(Lang.get("command.music." + commandSource + ".error.spotify")).queue();
        }
        spotifyApi.setAccessToken(access);

        musicManager.getAlkabot().getLogger().debug("Requesting Spotify playlist info...");
        // tracks playlists
        String id = query.replaceAll("https://open.spotify.com/playlist/", "");

        try {
            GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi
                    .getPlaylistsItems(id)
                    .build();

            final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

            String memberID = "";
            if (event.getMember() != null)
                memberID = event.getMember().getId();

            List<AlkabotTrack> alkabotTrackList = new ArrayList<>();

            for (PlaylistTrack playlistTrack : playlistTrackPaging.getItems()) {
                Track track = (Track) playlistTrack.getTrack();
                if (track != null)
                    alkabotTrackList.add(new AlkabotTrack(track, memberID));
            }

            AlkabotTrackPlaylist alkabotTrackPlaylist = new AlkabotTrackPlaylist();
            alkabotTrackPlaylist.setTitle(Lang.get("music.spotify.title"));
            alkabotTrackPlaylist.setUrl(query);
            alkabotTrackPlaylist.setThumbnailUrl(Lang.getImage("music.spotify.thumbnail"));
            alkabotTrackPlaylist.setFirstTrack(alkabotTrackList.get(0));
            alkabotTrackPlaylist.setTracks(alkabotTrackList);

            int pos = musicManager.getTrackScheduler().queuePlaylist(alkabotTrackPlaylist, position, skipCurrent);

            EmbedBuilder embed = MusicUtils.createPlaylistAddedEmbed(commandSource, event, alkabotTrackPlaylist, musicManager, pos);
            event.getHook().sendMessageEmbeds(embed.build()).queue();

            musicManager.getAlkabot().getLogger().debug("Spotify playlist loaded! (" + alkabotTrackList.size() + " tracks)");
            musicManager.getAlkabot().getLogger().debug("First track url: " + alkabotTrackList.get(0).getUrl());
        } catch (Exception e) {
            musicManager.getAlkabot().getLogger().error("Failed to get spotify playlist:", e);
            event.getHook().sendMessage(Lang.get("command.music." + commandSource + ".error.spotify")).queue();
        }
    }

}
