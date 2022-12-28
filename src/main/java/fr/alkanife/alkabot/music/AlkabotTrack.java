package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;

public class AlkabotTrack {

    private String query;
    private String url;
    private String trackSource;
    private Date dateAdded;
    private String addedByID;
    private boolean priority;
    private String thumbUrl;
    private String title;
    private List<String> artist;
    private long duration;

    private boolean retried = false;

    public AlkabotTrack () {}

    public AlkabotTrack(String query, String url, String trackSource, Date dateAdded, String addedByID, boolean priority, String thumbUrl, String title, List<String> artist, long duration) {
        this.query = query;
        this.url = url;
        this.trackSource = trackSource;
        this.dateAdded = dateAdded;
        this.addedByID = addedByID;
        this.priority = priority;
        this.thumbUrl = thumbUrl;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public AlkabotTrack(AudioTrack track, String trackSource, String addedByID, boolean priority) {
        this.url = track.getInfo().uri;

        this.query = this.url;

        this.trackSource = trackSource;
        this.dateAdded = new Date();
        this.addedByID = addedByID;
        this.priority = priority;
        this.thumbUrl = "https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg";
        this.title = track.getInfo().title;
        this.artist = Collections.singletonList(track.getInfo().author);
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
        this.artist = artists;

        this.duration = track.getDurationMs();

        StringBuilder stringBuilder = new StringBuilder("ytsearch: ").append(this.title);

        if (artists.size() > 0)
            for (String a : artists)
                stringBuilder.append(" ").append(a);

        this.query = stringBuilder.toString();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTrackSource() {
        return trackSource;
    }

    public void setTrackSource(String trackSource) {
        this.trackSource = trackSource;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getAddedByID() {
        return addedByID;
    }

    public void setAddedByID(String addedByID) {
        this.addedByID = addedByID;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getArtistList() {
        return artist;
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

    public void setArtist(List<String> artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isRetried() {
        return retried;
    }

    public void setRetried(boolean retried) {
        this.retried = retried;
    }
}
