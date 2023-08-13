package fr.alkanife.alkabot.util.tool;

import fr.alkanife.alkabot.Alkabot;

public abstract class JsonLoader {

    public final Alkabot alkabot;

    public boolean success = false;

    public JsonLoader(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public abstract String getReloadMessage();

    public void load() throws Exception {
        processLoad(false);
    }

    public void reload() throws Exception {
        alkabot.getLogger().info(getReloadMessage());

        processLoad(true);

        alkabot.getLogger().info("Reload complete");
    }

    public abstract void processLoad(boolean reload) throws Exception;

    public void changingBecauseNoValue(String field, String newValue, String noValueField) {
        alkabot.verbose("Changing '" + field + "' to '" + newValue + "', there is no value in '" + noValueField + "'");
    }

    public void changeNull(String field, String newValue) {
        alkabot.verbose("'" + field + "' is null, changing it to '" + newValue + "'");
    }

    public void cantContinue(String field) {
        alkabot.getLogger().error("Can't continue because the '" + field + "' field is empty");
    }
}
