package fr.alkanife.alkabot.music;

import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.tools.JsonLoader;

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
        String content = Files.readString(new File(alkabot.getParameters().getDataPath() + "/music.json").toPath());
        MusicData musicData = new GsonBuilder().serializeNulls().create().fromJson(content, MusicData.class);

        if (musicData == null)
            musicData = new MusicData();

        if (musicData.getShortcutList() == null)
            musicData.setShortcutList(new ArrayList<>());

        success = true;
        alkabot.setMusicData(musicData);
    }
}
