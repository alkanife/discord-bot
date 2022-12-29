package fr.alkanife.alkabot.music.playlists;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.alkanife.alkabot.Alkabot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PlaylistsManager {

    public void write() throws IOException {
        Alkabot.getLogger().info("Writing playlists");
        Gson gson = new Gson();
        Type typeDate = new TypeToken<List<Playlist>>(){}.getType();
        String json = gson.toJson(Alkabot.getPlaylists(), typeDate);

        Files.writeString(Paths.get(Alkabot.absolutePath() + "/playlists.json"), json);
        Alkabot.getLogger().info(Alkabot.getPlaylists().size() + " playlists were written");
    }

    public void read() throws IOException {
        read(false);
    }

    public void read(boolean reload) throws IOException {
        Alkabot.getLogger().info((reload ? "(RELOAD) " : "") +"Reading playlists");
        File plFile = new File(Alkabot.absolutePath() + "/playlists.json");

        if (!plFile.exists()) {
            return;
        }

        String plRaw = Files.readString(plFile.toPath());

        Gson gson = new Gson();
        Type typeDate = new TypeToken<List<Playlist>>(){}.getType();
        Alkabot.setPlaylists(gson.fromJson(plRaw, typeDate));

        Alkabot.getLogger().info(Alkabot.getPlaylists().size() + " playlists available");
    }
}
