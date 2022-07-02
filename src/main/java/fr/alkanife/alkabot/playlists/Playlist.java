package fr.alkanife.alkabot.playlists;

public class Playlist {

    private String name, url, user_id;

    public Playlist() {}

    public Playlist(String name, String url, String user_id) {
        this.name = name;
        this.url = url;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getUser_id() {
        return user_id;
    }
}
