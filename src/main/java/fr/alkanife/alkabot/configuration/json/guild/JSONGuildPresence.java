package fr.alkanife.alkabot.configuration.json.guild;

public class JSONGuildPresence {

    private String status;
    private JSONGuildPresenceActivity activity;

    public JSONGuildPresence() {}

    public JSONGuildPresence(String status, JSONGuildPresenceActivity activity) {
        this.status = status;
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public JSONGuildPresenceActivity getActivity() {
        return activity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActivity(JSONGuildPresenceActivity activity) {
        this.activity = activity;
    }
}
