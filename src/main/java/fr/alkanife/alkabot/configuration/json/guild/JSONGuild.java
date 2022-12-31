package fr.alkanife.alkabot.configuration.json.guild;

public class JSONGuild {

    private String guild_id;
    private JSONGuildPresence presence;

    public JSONGuild() {}

    public JSONGuild(String guild_id, JSONGuildPresence presence) {
        this.guild_id = guild_id;
        this.presence = presence;
    }

    public String getGuild_id() {
        return guild_id;
    }

    public JSONGuildPresence getPresence() {
        return presence;
    }

    public void setGuild_id(String guild_id) {
        this.guild_id = guild_id;
    }

    public void setPresence(JSONGuildPresence presence) {
        this.presence = presence;
    }
}
