package dev.alkanife.alkabot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alkanife.alkabot.Alkabot;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class JsonDataFileManager {

    @Getter
    private final Alkabot alkabot;
    @Getter
    private final File file;
    @Getter
    private final Class<?> dataType;

    @Getter @Setter
    private Object data;

    protected String readMessage;
    protected String createMessage;
    protected String updateMessage;
    protected String loadMessage;

    public JsonDataFileManager(Alkabot alkabot, File file, Class<?> dataType) {
        this.alkabot = alkabot;
        this.file = file;
        this.dataType = dataType;

        alkabot.getLogger().debug("Using file at absolute path: " + file.getAbsolutePath());
    }

    public boolean readFile() {
        data = null;

        if (file.exists()) {
            alkabot.getLogger().info(readMessage);

            String content;

            try {
                content = Files.readString(file.toPath());
            } catch (Exception exception) {
                alkabot.getLogger().error("Failed to read or access file '" + file.getAbsolutePath() + "'. The file format may not be valid, or the bot may not have access to it. To view the full error, enable the debug mode.");
                alkabot.getLogger().debug("Full trace:", exception);
                return false;
            }

            try {
                data = new GsonBuilder().serializeNulls().create().fromJson(content, dataType);
            } catch (Exception exception) {
                alkabot.getLogger().error("Failed to read JSON from file '" + file.getAbsolutePath() + "'. Please check the syntax of your file before reporting this error. To view the full error, enable the debug mode.");
                alkabot.getLogger().debug("Full trace:", exception);
                return false;
            }

            return true;
        } else {
            cleanData();
            return writeFile(true);
        }
    }

    public boolean writeFile() {
        return writeFile(false);
    }

    public boolean writeFile(boolean newFile) {
        if (newFile) {
            alkabot.getLogger().info(createMessage);
        } else {
            alkabot.getLogger().info(updateMessage);
        }

        if (data == null)
            return false;

        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to create parent directories for '" + file.getAbsolutePath() + "'. The bot does not have either read or write privileges for the path you specified. To view the full error, enable the debug mode.");
            alkabot.getLogger().debug("Full trace:", exception);
            return false;
        }

        try {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            Files.writeString(file.toPath(), gson.toJson(data, dataType));
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to create '" + file.getAbsolutePath() + "'. This error may occur if the bot does not have write or read privileges on the path you specified. To view the full error, enable the debug mode.");
            alkabot.getLogger().debug("Full trace:", exception);
            return false;
        }

        return true;
    }

    public boolean load() {
        return load(false);
    }

    public boolean reload() {
        return load(true);
    }

    private boolean load(boolean reloading) {
        getAlkabot().getLogger().info(loadMessage);

        if (data == null) {
            return false;
        }

        boolean loadSuccess = onLoad(reloading);

        if (loadSuccess)
            data = null;

        return loadSuccess;
    }

    public abstract void cleanData();
    public abstract boolean onLoad(boolean reloading);
}
