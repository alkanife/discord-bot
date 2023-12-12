package dev.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alkanife.alkabot.lang.Lang;
import lombok.*;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlkabotTrack {

    private String query;
    private String url;
    private Date dateAdded;
    private String addedByID;
    private String thumbUrl;
    private String title;
    private List<String> artistList;
    private long duration;

    private boolean retried = false;

    public AlkabotTrack(AudioTrack track, String addedByID) {
        this.url = track.getInfo().uri;

        this.query = this.url;

        this.dateAdded = new Date();
        this.addedByID = addedByID;
        this.thumbUrl = "https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg";
        this.title = track.getInfo().title;
        this.artistList = Collections.singletonList(track.getInfo().author);
        this.duration = track.getDuration();
    }

    public AlkabotTrack(Track track, String addedByID) {
        this.url = "https://open.spotify.com/track/" + track.getId();
        this.dateAdded = new Date();
        this.addedByID = addedByID;

        if (track.getAlbum() != null)
            if (track.getAlbum().getImages() != null)
                if (track.getAlbum().getImages().length > 0)
                    this.thumbUrl = track.getAlbum().getImages()[0].getUrl();

        if (thumbUrl == null)
            this.thumbUrl = Lang.getImage("music.spotify.thumbnail");

        this.title = track.getName();

        List<String> artists = new ArrayList<>();
        if (track.getArtists() != null)
            for (ArtistSimplified artist1 : track.getArtists())
                artists.add(artist1.getName());
        this.artistList = artists;

        this.duration = track.getDurationMs();

        StringBuilder stringBuilder = new StringBuilder("ytsearch: ").append(this.title);

        if (!artists.isEmpty())
            for (String a : artists)
                stringBuilder.append(" ").append(a);

        this.query = stringBuilder.toString();
    }

    public String getArtists() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String artist : getArtistList()) {
            if (!first)
                stringBuilder.append(", ");
            stringBuilder.append(artist);

            first = false;
        }
        return stringBuilder.toString();
    }
}
