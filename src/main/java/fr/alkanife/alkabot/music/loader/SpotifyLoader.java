package fr.alkanife.alkabot.music.loader;

import fr.alkanife.alkabot.music.AbstractMusic;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
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

    public void load(SlashCommandInteractionEvent slashCommandInteractionEvent, final String url, boolean priority, boolean force) {
        if (!musicManager.getAlkabot().isSpotifySupport())
            return;

        musicManager.getAlkabot().verbose("Loading music from '" + url + "' (" + priority + ")...");

        musicManager.getAlkabot().verbose("Requesting Spotify client credentials...");
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
            musicManager.getAlkabot().getLogger().error("Failed to get spotify client credentials:");
            e.printStackTrace();
            slashCommandInteractionEvent.getHook().sendMessage(musicManager.getAlkabot().t("command.music.play.error.spotify")).queue();
        }
        spotifyApi.setAccessToken(access);

        musicManager.getAlkabot().verbose("Requesting Spotify playlist info...");
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

            String source = musicManager.getAlkabot().t("command.music.play.source.spotify") + " / \"" + url + "\"";

            List<AlkabotTrack> alkabotTrackList = new ArrayList<>();

            for (PlaylistTrack playlistTrack : playlistTrackPaging.getItems()) {
                Track track = (Track) playlistTrack.getTrack();
                if (track != null)
                    alkabotTrackList.add(new AlkabotTrack(track, source, memberID, priority));
            }

            musicManager.getAlkabot().getMusicManager().getTrackScheduler().queuePlaylist(alkabotTrackList.get(0), alkabotTrackList, force);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(musicManager.getAlkabot().t("command.music.play.title_playlist") + " " + (priority ? musicManager.getAlkabot().t("command.music.play.priority") : ""));
            embedBuilder.setDescription("[" + musicManager.getAlkabot().t("command.music.generic.spotify_playlist") + "](" + url + ")\n\n" +
                    musicManager.getAlkabot().t("command.music.play.entries") + " `" + alkabotTrackList.size() + "`\n" +
                    musicManager.getAlkabot().t("command.music.play.newtime") + " `" + StringUtils.durationToString(musicManager.getAlkabot().getMusicManager().getTrackScheduler().getQueueDuration(), false, true) + "`");

            embedBuilder.setThumbnail(alkabotTrackList.get(0).getThumbUrl());

            slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            musicManager.getAlkabot().verbose("Spotify playlist loaded! (" + alkabotTrackList.size() + " tracks)");
            musicManager.getAlkabot().verbose("First track url: " + alkabotTrackList.get(0).getUrl());
        } catch (Exception e) {
            musicManager.getAlkabot().getLogger().error("Failed to get spotify playlist:");
            e.printStackTrace();
            slashCommandInteractionEvent.getHook().sendMessage(musicManager.getAlkabot().t("command.music.play.error.spotify")).queue();
        }
    }

}
