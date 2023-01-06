package fr.alkanife.alkabot.configuration.json;

import fr.alkanife.alkabot.configuration.json.commands.JSONCommands;
import fr.alkanife.alkabot.configuration.json.guild.JSONGuild;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotifications;

public class JSONConfiguration {

    private String lang_file, shortcut_file;
    private boolean debug;
    private JSONAdmin admin;
    private JSONGuild guild;
    private JSONWelcomeMessage welcome_message;
    private JSONAutoRole auto_role;
    private JSONMusic music;
    private JSONCommands commands;
    private JSONNotifications notifications;

    public JSONConfiguration() {}

    public JSONConfiguration(String lang_file, String shortcut_file, boolean debug, JSONAdmin admin, JSONGuild guild, JSONWelcomeMessage welcome_message, JSONAutoRole auto_role, JSONMusic music, JSONCommands commands, JSONNotifications notifications) {
        this.lang_file = lang_file;
        this.shortcut_file = shortcut_file;
        this.debug = debug;
        this.admin = admin;
        this.guild = guild;
        this.welcome_message = welcome_message;
        this.auto_role = auto_role;
        this.music = music;
        this.commands = commands;
        this.notifications = notifications;
    }

    public String getLang_file() {
        return lang_file;
    }

    public void setLang_file(String lang_file) {
        this.lang_file = lang_file;
    }

    public String getShortcut_file() {
        return shortcut_file;
    }

    public void setShortcut_file(String shortcut_file) {
        this.shortcut_file = shortcut_file;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public JSONAdmin getAdmin() {
        return admin;
    }

    public void setAdmin(JSONAdmin admin) {
        this.admin = admin;
    }

    public JSONGuild getGuild() {
        return guild;
    }

    public void setGuild(JSONGuild guild) {
        this.guild = guild;
    }

    public JSONWelcomeMessage getWelcome_message() {
        return welcome_message;
    }

    public void setWelcome_message(JSONWelcomeMessage welcome_message) {
        this.welcome_message = welcome_message;
    }

    public JSONAutoRole getAuto_role() {
        return auto_role;
    }

    public void setAuto_role(JSONAutoRole auto_role) {
        this.auto_role = auto_role;
    }

    public JSONMusic getMusic() {
        return music;
    }

    public void setMusic(JSONMusic music) {
        this.music = music;
    }

    public JSONCommands getCommands() {
        return commands;
    }

    public void setCommands(JSONCommands commands) {
        this.commands = commands;
    }

    public JSONNotifications getNotifications() {
        return notifications;
    }

    public void setNotifications(JSONNotifications notifications) {
        this.notifications = notifications;
    }
}
