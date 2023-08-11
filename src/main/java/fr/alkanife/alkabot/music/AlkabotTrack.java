package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.*;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
public class AlkabotTrack {

    @Getter @Setter
    private String query;
    @Getter @Setter
    private String url;
    @Getter @Setter
    private String trackSource;
    @Getter @Setter
    private Date dateAdded;
    @Getter @Setter
    private String addedByID;
    @Getter @Setter
    private boolean priority;
    @Getter @Setter
    private String thumbUrl;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private List<String> artistList;
    @Getter @Setter
    private long duration;

    @Getter @Setter
    private boolean retried = false;

    public AlkabotTrack(AudioTrack track, String trackSource, String addedByID, boolean priority) {
        this.url = track.getInfo().uri;

        this.query = this.url;

        this.trackSource = trackSource;
        this.dateAdded = new Date();
        this.addedByID = addedByID;
        this.priority = priority;
        this.thumbUrl = "https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg";
        this.title = track.getInfo().title;
        this.artistList = Collections.singletonList(track.getInfo().author);
        this.duration = track.getDuration();
    }

    public AlkabotTrack(Track track, String trackSource, String addedByID, boolean priority) {
        this.url = "https://open.spotify.com/track/" + track.getId();
        this.trackSource = trackSource;
        this.dateAdded = new Date();
        this.addedByID = addedByID;
        this.priority = priority;

        if (track.getAlbum() != null)
            if (track.getAlbum().getImages() != null)
                if (track.getAlbum().getImages().length > 0)
                    this.thumbUrl = track.getAlbum().getImages()[0].getUrl();

        if (thumbUrl == null)
            this.thumbUrl = "https://share.alkanife.fr/alkabot/spotify.png";

        this.title = track.getName();

        List<String> artists = new ArrayList<>();
        if (track.getArtists() != null)
            for (ArtistSimplified artist1 : track.getArtists())
                artists.add(artist1.getName());
        this.artistList = artists;

        this.duration = track.getDurationMs();

        StringBuilder stringBuilder = new StringBuilder("ytsearch: ").append(this.title);

        if (artists.size() > 0)
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
