package fr.alkanife.alkabot.configuration.json;

public class JSONWelcomeMessage {

    private boolean enable;
    private String channel_id;

    public JSONWelcomeMessage() {}

    public JSONWelcomeMessage(boolean enable, String channel_id) {
        this.enable = enable;
        this.channel_id = channel_id;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
}
