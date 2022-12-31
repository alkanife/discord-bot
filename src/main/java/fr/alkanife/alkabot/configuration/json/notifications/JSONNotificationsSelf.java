package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotificationsSelf {

    private String channel_id;
    private boolean admin, commands;

    public JSONNotificationsSelf() {}

    public JSONNotificationsSelf(String channel_id, boolean admin, boolean commands) {
        this.channel_id = channel_id;
        this.admin = admin;
        this.commands = commands;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isCommands() {
        return commands;
    }

    public void setCommands(boolean commands) {
        this.commands = commands;
    }
}
