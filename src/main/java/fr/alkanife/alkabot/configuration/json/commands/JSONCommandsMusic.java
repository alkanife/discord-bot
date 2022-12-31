package fr.alkanife.alkabot.configuration.json.commands;

public class JSONCommandsMusic {

    private boolean play, playnext, forceplay, remove, skip, stop, destroy, shuffle, clear, queue;
    private JSONCommandsMusicPlaylist playlist;

    public JSONCommandsMusic() {}

    public JSONCommandsMusic(boolean play, boolean playnext, boolean forceplay, boolean remove, boolean skip, boolean stop, boolean destroy, boolean shuffle, boolean clear, boolean queue, JSONCommandsMusicPlaylist playlist) {
        this.play = play;
        this.playnext = playnext;
        this.forceplay = forceplay;
        this.remove = remove;
        this.skip = skip;
        this.stop = stop;
        this.destroy = destroy;
        this.shuffle = shuffle;
        this.clear = clear;
        this.queue = queue;
        this.playlist = playlist;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public boolean isPlaynext() {
        return playnext;
    }

    public void setPlaynext(boolean playnext) {
        this.playnext = playnext;
    }

    public boolean isForceplay() {
        return forceplay;
    }

    public void setForceplay(boolean forceplay) {
        this.forceplay = forceplay;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public boolean isQueue() {
        return queue;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
    }

    public JSONCommandsMusicPlaylist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(JSONCommandsMusicPlaylist playlist) {
        this.playlist = playlist;
    }
}
