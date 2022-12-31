package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotifications {

    private JSONNotificationsSelf self;
    private JSONNotificationsMessage message;
    private JSONNotificationsMember member;
    private JSONNotificationsModerator moderator;
    private JSONNotificationsVoice voice;
    private JSONNotificationsGuild guild;

    public JSONNotifications() {}

    public JSONNotifications(JSONNotificationsSelf self, JSONNotificationsMessage message, JSONNotificationsMember member, JSONNotificationsModerator moderator, JSONNotificationsVoice voice, JSONNotificationsGuild guild) {
        this.self = self;
        this.message = message;
        this.member = member;
        this.moderator = moderator;
        this.voice = voice;
        this.guild = guild;
    }

    public JSONNotificationsSelf getSelf() {
        return self;
    }

    public void setSelf(JSONNotificationsSelf self) {
        this.self = self;
    }

    public JSONNotificationsMessage getMessage() {
        return message;
    }

    public void setMessage(JSONNotificationsMessage message) {
        this.message = message;
    }

    public JSONNotificationsMember getMember() {
        return member;
    }

    public void setMember(JSONNotificationsMember member) {
        this.member = member;
    }

    public JSONNotificationsModerator getModerator() {
        return moderator;
    }

    public void setModerator(JSONNotificationsModerator moderator) {
        this.moderator = moderator;
    }

    public JSONNotificationsVoice getVoice() {
        return voice;
    }

    public void setVoice(JSONNotificationsVoice voice) {
        this.voice = voice;
    }

    public JSONNotificationsGuild getGuild() {
        return guild;
    }

    public void setGuild(JSONNotificationsGuild guild) {
        this.guild = guild;
    }
}
