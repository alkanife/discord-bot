package fr.alkanife.alkabot.configuration.json.commands;

public class JSONCommands {

    private boolean about;
    private JSONCommandsMusic music;
    private JSONCommandsUtilities utilities;

    public JSONCommands() {}

    public JSONCommands(boolean about, JSONCommandsMusic music, JSONCommandsUtilities utilities) {
        this.about = about;
        this.music = music;
        this.utilities = utilities;
    }

    public boolean isAbout() {
        return about;
    }

    public void setAbout(boolean about) {
        this.about = about;
    }

    public JSONCommandsMusic getMusic() {
        return music;
    }

    public void setMusic(JSONCommandsMusic music) {
        this.music = music;
    }

    public JSONCommandsUtilities getUtilities() {
        return utilities;
    }

    public void setUtilities(JSONCommandsUtilities utilities) {
        this.utilities = utilities;
    }
}
