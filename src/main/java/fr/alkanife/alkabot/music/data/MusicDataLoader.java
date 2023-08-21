package fr.alkanife.alkabot.music.data;

import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.log.Logs;
import fr.alkanife.alkabot.util.tool.JsonLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class MusicDataLoader extends JsonLoader {

    public MusicDataLoader(Alkabot alkabot) {
        super(alkabot, new File(alkabot.getParameters().getDataPath() + "/music.json"));
    }

    @Override
    public void processLoad() throws Exception {
        alkabot.getLogger().debug("Using music data at path '" + file.getPath() + "'");
        String content = Files.readString(file.toPath());
        MusicData musicData = new GsonBuilder().serializeNulls().create().fromJson(content, MusicData.class);

        if (musicData == null)
            musicData = new MusicData();

        if (musicData.getShortcutList() == null)
            musicData.setShortcutList(new ArrayList<>());

        alkabot.setMusicData(musicData);
    }
}
