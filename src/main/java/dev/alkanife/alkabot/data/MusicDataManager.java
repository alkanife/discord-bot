package dev.alkanife.alkabot.data;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.data.music.MusicData;
import dev.alkanife.alkabot.data.music.Shortcut;
import dev.alkanife.alkabot.file.JsonFileManipulation;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

public class MusicDataManager extends JsonFileManipulation {

    @Getter
    private MusicData musicData;

    public MusicDataManager(@NotNull Alkabot alkabot) {
        super(alkabot, new File(alkabot.getArgs().getMusicDataFilePath()), MusicData.class);
        musicData = (MusicData) cleanData(null);
    }

    @Override
    public boolean setup() {
        musicData = (MusicData) cleanData(new MusicData());
        save();
        return true;
    }

    @Override
    public boolean validateLoad(@NotNull Object data, boolean reload) {
        musicData = (MusicData) data;
        return true;
    }

    @NotNull
    @Override
    public Object cleanData(@Nullable Object object) {
        MusicData musicDataObject = (MusicData) object;

        if (musicDataObject == null)
            musicDataObject = new MusicData(100, new ArrayList<>());

        //TODO revoir ça c'était bizarre
        musicDataObject.setVolume(100);

        if (musicDataObject.getShortcutList() == null)
            musicDataObject.setShortcutList(new ArrayList<>());

        return musicDataObject;
    }

    @Nullable
    @Override
    public Object getDataObject() {
        return musicData;
    }

    @Nullable
    public Shortcut getShortcutByName(String name) {
        if (musicData == null)
            return null;

        Shortcut shortcut = null;

        for (Shortcut s : musicData.getShortcutList())
            if (s.getName().equalsIgnoreCase(name))
                shortcut = s;

        return shortcut;
    }
}
