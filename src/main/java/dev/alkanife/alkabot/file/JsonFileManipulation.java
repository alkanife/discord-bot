package dev.alkanife.alkabot.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.util.timetracker.TimeTracker;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class JsonFileManipulation extends FileManipulation {

    @Getter
    private final @NotNull Class<?> dataType;

    @Getter @Setter
    private @Nullable String override;

    public JsonFileManipulation(@NotNull Alkabot alkabot, @NotNull File file, @NotNull Class<?> dataType) {
        super(alkabot, file);
        this.dataType = dataType;
    }

    private String getDataSourceName() {
        return override == null ? getFile().getName() : "[override]";
    }

    private @Nullable String toJson(@Nullable Object object) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            return gson.toJson(object, dataType);
        } catch (Exception exception) {
            getAlkabot().getLogger().error("Invalid data object to json operation", exception);
            return null;
        }
    }

    private @Nullable Object fromJson(@Nullable String json) {
        try {
            return new GsonBuilder().serializeNulls().create().fromJson(json, dataType);
        } catch (JsonSyntaxException exception) {
            if (override == null) {
                getAlkabot().getLogger().error("Invalid JSON syntax from '{}'", getFile().getAbsolutePath());
            } else {
                getAlkabot().getLogger().error("Invalid JSON syntax from '[override]'");
            }

            getAlkabot().getLogger().error("Caused by {}", exception.getMessage());

            return null;
        } catch (Exception exception) {
            if (override == null) {
                getAlkabot().getLogger().error("Internal error while reading JSON from '{}'", getFile().getAbsolutePath(), exception);
            } else {
                getAlkabot().getLogger().error("Internal error while reading JSON override", exception);
            }

            return null;
        }
    }

    private @Nullable Object getDataFromFile() {
        if (override != null)
            return fromJson(override);

        ManipulationState readState = readFile();

        if (readState.equals(ManipulationState.FILE_DONT_EXISTS)) {
            Object newData = cleanData(null);
            String json = toJson(newData);

            if (json == null)
                return null;

            getAlkabot().getLogger().info("No file named '{}' was found at the specified path. Creating a new one...", getFile().getName());

            if (writeFile(true, json).failed())
                return null;

            return newData;
        } else {
            if (readState.failed())
                return null;

            if (getFileContent() == null)
                return null;

            return fromJson(getFileContent());
        }
    }

    public boolean load(boolean reload) {
        getAlkabot().getLogger().debug("{}Loading data from '{}'", reload ? "(Re)" : "", getDataSourceName());
        String tracking = TimeTracker.startUnique("json-load");

        Object data = getDataFromFile();

        if (data == null) {
            TimeTracker.kill(tracking);
            return false;
        }

        data = cleanData(data);

        boolean validation = validateLoad(data, reload);

        if (!save())
            getAlkabot().getLogger().warn("Failed to correct a file: {}", getFile().getName());

        TimeTracker.end(tracking);

        return validation;
    }

    public boolean load() {
        return load(false);
    }

    public boolean reload() {
        return load(true);
    }

    public boolean save() {
        getAlkabot().getLogger().debug("Saving data to '{}'", getFile().getName());

        String tracking = TimeTracker.startUnique("json-save");
        Object object = getDataObject();

        if (object == null) {
            TimeTracker.kill(tracking);
            return false;
        }

        String json = toJson(object);

        if (json == null) {
            TimeTracker.kill(tracking);
            return false;
        }

        ManipulationState writeState = writeFile(false, json);

        TimeTracker.end(tracking);

        return writeState.succeed();
    }

    public abstract boolean setup();
    public abstract boolean validateLoad(@NotNull Object data, boolean reload);
    public abstract @NotNull Object cleanData(@Nullable Object object);
    public abstract @Nullable Object getDataObject();
}
