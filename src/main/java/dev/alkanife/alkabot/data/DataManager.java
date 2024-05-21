package dev.alkanife.alkabot.data;

import dev.alkanife.alkabot.Alkabot;
import lombok.Getter;

public class DataManager {

    @Getter
    private MusicDataManager musicDataManager;

    public DataManager(Alkabot alkabot) {
        musicDataManager = new MusicDataManager(alkabot);
    }

}
