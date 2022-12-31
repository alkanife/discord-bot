package fr.alkanife.alkabot.music;

public abstract class AbstractMusic {

    private final MusicManager musicManager;

    public AbstractMusic(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }
}
