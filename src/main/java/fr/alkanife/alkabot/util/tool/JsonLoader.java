package fr.alkanife.alkabot.util.tool;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.log.Logs;

import java.io.File;

public abstract class JsonLoader {

    public final Alkabot alkabot;
    public final File file;
    public boolean reloading = false;

    public boolean success = false;

    public JsonLoader(Alkabot alkabot, File file) {
        this.alkabot = alkabot;
        this.file = file;
    }

    public void load() {
        try {
            processLoad();
            success = true;
            alkabot.getLogger().debug("Load complete");
        } catch (Exception exception) {
            alkabot.getLogger().error(Logs.invalidFileMessage(file.getAbsolutePath()), exception);
        }
    }

    public void reload() {
        reloading = true;
        load();
    }

    public abstract void processLoad() throws Exception;

    public void changingBecauseNoValue(String field, String newValue, String noValueField) {
        alkabot.getLogger().debug("Changing '" + field + "' to '" + newValue + "', there is no value in '" + noValueField + "'");
    }

    public void changeNull(String field, String newValue) {
        alkabot.getLogger().debug("'" + field + "' is null, changing it to '" + newValue + "'");
    }
}
