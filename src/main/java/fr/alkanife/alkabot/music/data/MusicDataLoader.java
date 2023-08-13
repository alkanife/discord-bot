package fr.alkanife.alkabot.music.data;

import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.util.tool.JsonLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class MusicDataLoader extends JsonLoader {

    public MusicDataLoader(Alkabot alkabot) {
        super(alkabot);
    }

    @Override
    public String getReloadMessage() {
        return "Reloading music data";
    }

    @Override
    public void processLoad(boolean reload) throws Exception {
        File file = new File(alkabot.getParameters().getDataPath() + "/music.json");
        alkabot.verbose(file.getPath());
        String content = Files.readString(file.toPath());
        MusicData musicData = new GsonBuilder().serializeNulls().create().fromJson(content, MusicData.class);

        if (musicData == null)
            musicData = new MusicData();

        if (musicData.getShortcutList() == null)
            musicData.setShortcutList(new ArrayList<>());

        success = true;
        alkabot.setMusicData(musicData);
    }
}
