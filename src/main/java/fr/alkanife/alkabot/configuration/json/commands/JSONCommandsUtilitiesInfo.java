package fr.alkanife.alkabot.configuration.json.commands;

public class JSONCommandsUtilitiesInfo {

    private boolean server, member, emote;

    public JSONCommandsUtilitiesInfo() {}

    public JSONCommandsUtilitiesInfo(boolean server, boolean member, boolean emote) {
        this.server = server;
        this.member = member;
        this.emote = emote;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    public boolean isEmote() {
        return emote;
    }

    public void setEmote(boolean emote) {
        this.emote = emote;
    }
}
