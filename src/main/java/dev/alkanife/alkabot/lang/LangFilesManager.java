package dev.alkanife.alkabot.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.file.FileManipulation;
import dev.alkanife.alkabot.file.ManipulationState;
import dev.alkanife.alkabot.util.timetracker.TimeTracker;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LangFilesManager extends FileManipulation {

    private HashMap<String, Object> translations = new HashMap<>();

    public enum defaultLanguages {
        en_US, fr_FR
    }

    public LangFilesManager(@NotNull Alkabot alkabot) {
        super(alkabot, new File(alkabot.getConfig().getLangFilePath()));
    }

    public boolean exportLanguage(File file) {
        InputStream inputStream = getClass().getResourceAsStream("/lang/" + file.getName());

        if (inputStream == null) {
            getAlkabot().getLogger().error("Internal error: there is no '{}' language pack available", file.getName());
            return false;
        }

        getAlkabot().getLogger().debug("Exporting a new language pack to '{}'", file.getAbsolutePath());

        try {
            if (getFile().getParentFile() != null)
                getFile().getParentFile().mkdirs();

            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception exception) {
            getAlkabot().getLogger().error("An error occurred while exporting a language pack", exception);
            return false;
        }

        return true;
    }

    public boolean exportDefaultLanguages() {
        for (defaultLanguages defaultLanguage : defaultLanguages.values()) {
            File file = new File("lang/" + defaultLanguage.name() + ".json");

            if (!exportLanguage(file)) {
                return false;
            }
        }
        return true;
    }

    public boolean createNewFile() {
        if (!exportLanguage(getFile()))
            return false;

        try {
            String content = Files.readString(getFile().toPath());
            setFileContent(content);
            return true;
        } catch (Exception exception) {
            getAlkabot().getLogger().error("Internal error: failed to read the new language pack", exception);
            return false;
        }
    }

    private void readEntry(String address, LinkedTreeMap.Entry<?, ?> entry) {
        if (entry.getValue() instanceof LinkedTreeMap<?, ?> mapValue) {
            for (LinkedTreeMap.Entry<?, ?> entryValue : mapValue.entrySet())
                readEntry(address + "." + entryValue.getKey(), entryValue);
        } else {
            translations.put(address, entry.getValue());
            //alkabot.getLogger().debug(address + " = " + entry.getValue().toString());
        }
    }

    public boolean load(boolean reload) {
        getAlkabot().getLogger().debug("{}Loading language pack from '{}'", reload ? "(Re)" : "", getFile().getAbsolutePath());
        String tracking = TimeTracker.startUnique("lang-load");

        ManipulationState readState = readFile();

        if (readState.equals(ManipulationState.FILE_DONT_EXISTS)) {
            if (!createNewFile()) {
                TimeTracker.kill(tracking);
                return false;
            }
        } else {
            if (readState.failed()) {
                TimeTracker.kill(tracking);
                return false;
            }
        }

        try {
            Gson gson = new GsonBuilder().serializeNulls().create();

            Map<?, LinkedTreeMap<?, ?>> map = gson.fromJson(getFileContent(), Map.class);

            if (map == null) {
                getAlkabot().getLogger().error("The language pack at '{}' is empty!", getFile().getAbsolutePath());
                TimeTracker.kill(tracking);
                return false;
            }

            for (Map.Entry<?, LinkedTreeMap<?, ?>> entry : map.entrySet())
                readEntry(entry.getKey() + "", entry);
        } catch (JsonSyntaxException exception) {
            getAlkabot().getLogger().error("Invalid JSON syntax in the language pack at '{}'", getFile().getAbsolutePath());
            getAlkabot().getLogger().error("Caused by {}", exception.getMessage());
            TimeTracker.kill(tracking);
            return false;
        } catch (Exception exception) {
            getAlkabot().getLogger().error("An error occurred while loading the language pack at '{}'", getFile().getAbsolutePath(), exception);
            TimeTracker.kill(tracking);
            return false;
        }

        // Setup dates
        if (translations.get("date.format") == null) {
            getAlkabot().getLogger().warn("No date format provided by the language pack, using default");
        } else {
            Lang.setDateFormat(translations.get("date.format").toString());
        }
        getAlkabot().getLogger().debug("Using date format '{}'", Lang.getDateFormat());

        if (translations.get("date.locale") == null) {
            getAlkabot().getLogger().warn("No date locale provided by the language pack, using english");
        } else {
            Locale locale = Locale.forLanguageTag(translations.get("date.locale").toString());
            Lang.setDateLocale(locale);
            getAlkabot().getLogger().debug("Using date locale '{}'", locale.toString());
        }

        Lang.setTranslations(translations);
        translations = null;

        TranslationHandler.NULL_VALUE = Lang.get("notification.generic.unknown");

        getAlkabot().getLogger().debug("Finished loading {} values from the language pack", Lang.getTranslations().size());
        TimeTracker.end(tracking);
        return true;
    }

    public boolean load() {
        return load(false);
    }

    public boolean reload() {
        return load(true);
    }

    // TODO
    /*public boolean save() {
        return true;
    }*/
}
