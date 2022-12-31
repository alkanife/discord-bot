package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotificationsMessage {

    private String channel_id;
    private int cache;
    private boolean edit, delete;

    public JSONNotificationsMessage() {}

    public JSONNotificationsMessage(String channel_id, int cache, boolean edit, boolean delete) {
        this.channel_id = channel_id;
        this.cache = cache;
        this.edit = edit;
        this.delete = delete;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public int getCache() {
        return cache;
    }

    public void setCache(int cache) {
        this.cache = cache;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
