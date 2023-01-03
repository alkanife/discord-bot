package fr.alkanife.alkabot.music.shortcut;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alkanife.alkabot.Alkabot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ShortcutManager {

    private List<Shortcut> shortcuts = new ArrayList<>();

    public void write() throws IOException {
        Alkabot.getLogger().info("Writing shortcuts");
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        Type typeDate = new TypeToken<List<Shortcut>>(){}.getType();
        String json = gson.toJson(shortcuts, typeDate);

        Files.writeString(new File(Alkabot.getConfig().getShortcut_file()).toPath(), json);
        Alkabot.getLogger().info(shortcuts.size() + " shortcuts were written");
    }

    public void read() throws IOException {
        read(false);
    }

    public void read(boolean reload) throws IOException {
        Alkabot.getLogger().info((reload ? "(RELOAD) " : "") +"Reading shortcuts");
        File plFile = new File(Alkabot.getConfig().getShortcut_file());

        if (!plFile.exists()) {
            return;
        }

        String plRaw = Files.readString(plFile.toPath());

        Gson gson = new Gson();
        Type typeDate = new TypeToken<List<Shortcut>>(){}.getType();
        shortcuts = gson.fromJson(plRaw, typeDate);

        Alkabot.getLogger().info(shortcuts.size() + " shortcuts available");
    }

    public Shortcut getShortcut(String name) {
        Shortcut shortcut = null;

        for (Shortcut s : shortcuts)
            if (s.getName().equalsIgnoreCase(name))
                shortcut = s;

        return shortcut;
    }

    public List<Shortcut> getShortcuts() {
        return shortcuts;
    }

    public void setShortcuts(List<Shortcut> shortcuts) {
        this.shortcuts = shortcuts;
    }
}
