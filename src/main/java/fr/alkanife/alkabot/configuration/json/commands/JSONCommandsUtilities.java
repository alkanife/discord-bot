package fr.alkanife.alkabot.configuration.json.commands;

public class JSONCommandsUtilities {

    private JSONCommandsUtilitiesInfo info;
    private boolean copy;

    public JSONCommandsUtilities() {}

    public JSONCommandsUtilities(JSONCommandsUtilitiesInfo info, boolean copy) {
        this.info = info;
        this.copy = copy;
    }

    public JSONCommandsUtilitiesInfo getInfo() {
        return info;
    }

    public void setInfo(JSONCommandsUtilitiesInfo info) {
        this.info = info;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }
}
