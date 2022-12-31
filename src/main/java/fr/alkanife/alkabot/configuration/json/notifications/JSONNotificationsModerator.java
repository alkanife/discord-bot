package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotificationsModerator {

    private String channel_id;
    private boolean ban, unban, kick, timeout, deafen_member, undeafen_member, mute_member, unmute_member, change_member_nickname;

    public JSONNotificationsModerator() {}

    public JSONNotificationsModerator(String channel_id, boolean ban, boolean unban, boolean kick, boolean timeout, boolean deafen_member, boolean undeafen_member, boolean mute_member, boolean unmute_member, boolean change_member_nickname) {
        this.channel_id = channel_id;
        this.ban = ban;
        this.unban = unban;
        this.kick = kick;
        this.timeout = timeout;
        this.deafen_member = deafen_member;
        this.undeafen_member = undeafen_member;
        this.mute_member = mute_member;
        this.unmute_member = unmute_member;
        this.change_member_nickname = change_member_nickname;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public boolean isUnban() {
        return unban;
    }

    public void setUnban(boolean unban) {
        this.unban = unban;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public boolean isDeafen_member() {
        return deafen_member;
    }

    public void setDeafen_member(boolean deafen_member) {
        this.deafen_member = deafen_member;
    }

    public boolean isUndeafen_member() {
        return undeafen_member;
    }

    public void setUndeafen_member(boolean undeafen_member) {
        this.undeafen_member = undeafen_member;
    }

    public boolean isMute_member() {
        return mute_member;
    }

    public void setMute_member(boolean mute_member) {
        this.mute_member = mute_member;
    }

    public boolean isUnmute_member() {
        return unmute_member;
    }

    public void setUnmute_member(boolean unmute_member) {
        this.unmute_member = unmute_member;
    }

    public boolean isChange_member_nickname() {
        return change_member_nickname;
    }

    public void setChange_member_nickname(boolean change_member_nickname) {
        this.change_member_nickname = change_member_nickname;
    }
}
