package fr.alkanife.alkabot.configuration.json;

public class JSONMusic {

    private boolean stop_when_alone;

    public JSONMusic() {}

    public JSONMusic(boolean stop_when_alone) {
        this.stop_when_alone = stop_when_alone;
    }

    public boolean isStop_when_alone() {
        return stop_when_alone;
    }

    public void setStop_when_alone(boolean stop_when_alone) {
        this.stop_when_alone = stop_when_alone;
    }
}
