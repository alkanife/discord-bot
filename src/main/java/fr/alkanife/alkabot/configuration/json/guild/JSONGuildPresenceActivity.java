package fr.alkanife.alkabot.configuration.json.guild;

public class JSONGuildPresenceActivity {

    private boolean show;
    private String type;
    private String text;

    public JSONGuildPresenceActivity() {}

    public JSONGuildPresenceActivity(boolean show, String type, String text) {
        this.show = show;
        this.type = type;
        this.text = text;
    }

    public boolean isShow() {
        return show;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }
}
