package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotificationsVoice {

    private String channel_id;
    private boolean join, leave, move;

    public JSONNotificationsVoice() {}

    public JSONNotificationsVoice(String channel_id, boolean join, boolean leave, boolean move) {
        this.channel_id = channel_id;
        this.join = join;
        this.leave = leave;
        this.move = move;
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

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}
