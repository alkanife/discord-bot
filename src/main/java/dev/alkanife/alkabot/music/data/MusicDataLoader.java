package dev.alkanife.alkabot.music.data;

import dev.alkanife.alkabot.Alkabot;

public class MusicDataLoader /*extends JsonDataFileManager*/ {

    public MusicDataLoader(Alkabot alkabot) {
       // super(alkabot, new File(alkabot.getParameters().getDataPath() + "/music.json")); TODO fix this
    }

    //@Override
    public void processLoad() throws Exception {
        /*alkabot.getLogger().debug("Using music data at path '" + file.getPath() + "'");
        String content = Files.readString(file.toPath());
        MusicData musicData = new GsonBuilder().serializeNulls().create().fromJson(content, MusicData.class);

        if (musicData == null)
            musicData = new MusicData();

        if (musicData.getShortcutList() == null)
            musicData.setShortcutList(new ArrayList<>());

        alkabot.setMusicData(musicData);*/
    }
}
