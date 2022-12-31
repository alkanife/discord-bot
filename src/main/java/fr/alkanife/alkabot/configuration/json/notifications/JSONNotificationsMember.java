package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotificationsMember {

    private String channel_id;
    private boolean join, leave;

    public JSONNotificationsMember() {}

    public JSONNotificationsMember(String channel_id, boolean join, boolean leave) {
        this.channel_id = channel_id;
        this.join = join;
        this.leave = leave;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public boolean isJoin() {
        return join;
    }

    public void setJoin(boolean join) {
        this.join = join;
    }

    public boolean isLeave() {
        return leave;
    }

    public void setLeave(boolean leave) {
        this.leave = leave;
    }
}
