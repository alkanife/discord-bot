package fr.alkanife.alkabot.music.playlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.alkanife.alkabot.Alkabot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {

    private List<Playlist> playlists = new ArrayList<>();

    public void write() throws IOException {
        Alkabot.getLogger().info("Writing playlists");
        Gson gson = new Gson();
        Type typeDate = new TypeToken<List<Playlist>>(){}.getType();
        String json = gson.toJson(playlists, typeDate);

        Files.writeString(Paths.get(Alkabot.getAbsolutePath() + "/playlists.json"), json);
        Alkabot.getLogger().info(playlists.size() + " playlists were written");
    }

    public void read() throws IOException {
        read(false);
    }

    public void read(boolean reload) throws IOException {
        Alkabot.getLogger().info((reload ? "(RELOAD) " : "") +"Reading playlists");
        File plFile = new File(Alkabot.getAbsolutePath() + "/playlists.json");

        if (!plFile.exists()) {
            return;
        }

        String plRaw = Files.readString(plFile.toPath());

        Gson gson = new Gson();
        Type typeDate = new TypeToken<List<Playlist>>(){}.getType();
        playlists = gson.fromJson(plRaw, typeDate);

        Alkabot.getLogger().info(playlists.size() + " playlists available");
    }

    public Playlist getPlaylist(String name) {
        Playlist pl = null;

        for (Playlist p : playlists)
            if (p.getName().equalsIgnoreCase(name))
                pl = p;

        return pl;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }
}
