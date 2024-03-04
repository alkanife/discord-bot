package dev.alkanife.alkabot.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import dev.alkanife.alkabot.Alkabot;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class LangFilesManager {

    @Getter
    private final Alkabot alkabot;
    @Getter
    private final File langDirectory;

    private HashMap<String, Object> translations = new HashMap<>();

    public LangFilesManager(Alkabot alkabot) {
        this.alkabot = alkabot;
        this.langDirectory = new File(alkabot.getArgs().getLangDirectoryPath());

        alkabot.getLogger().debug("Using lang directory at absolute path: " + langDirectory.getAbsolutePath());
    }

    public boolean handleDirectory() {
        alkabot.getLogger().info("Preparing language files");

        if (langDirectory.exists()) {
            if (!langDirectory.isDirectory()) {
                alkabot.getLogger().error("The path to the language files must be to a directory. (Found a file at " + langDirectory.getAbsolutePath() + ")");
                return false;
            }

            try {
                if (isDirectoryEmpty(langDirectory.toPath())) {
                    alkabot.getLogger().info("The language directory is empty!");
                    return exportDefaultLangFiles();
                } else {
                    return true;
                }
            } catch (Exception exception) {
                alkabot.getLogger().error("The bot is not able to see the contents of the language directory at '" + langDirectory.getAbsolutePath() + "'. Alkabot does not have either read or write privileges for the path you specified. To view the full error, enable the debug mode.");
                alkabot.getLogger().debug("Full trace:", exception);
            }

            return false;
        } else {
            alkabot.getLogger().info("The language directory was not found at the specified path. Creating a new one at '" + langDirectory.getAbsolutePath() + "'");

            try {
                langDirectory.mkdirs();
            } catch (Exception exception) {
                alkabot.getLogger().error("Failed to create directory for language files at specified path: " + langDirectory.getAbsolutePath() + ". The bot does not have either read or write privileges for the path you specified. To view the full error, enable the debug mode.");
                alkabot.getLogger().debug("Full trace:", exception);
                return false;
            }

            return exportDefaultLangFiles();
        }
    }

    public boolean exportDefaultLangFiles() {
        alkabot.getLogger().info("Exporting default lang files to '" + langDirectory.getAbsolutePath() + "'");

        String[] langs = {"en_US", "fr_FR"};

        for (String lang : langs) {
            try {
                File file = new File(alkabot.getArgs().getLangDirectoryPath() + "/" + lang + ".json");

                if (file.exists()) {
                    alkabot.getLogger().debug("Skipping " + file.getName() + " (already exists)");
                    continue;
                }

                alkabot.getLogger().debug("Exporting " + file.getName());

                InputStream inputStream = getClass().getResourceAsStream("/lang/" + lang + ".json");

                if (inputStream == null)
                    throw new NullPointerException("Invalid resource call");

                Files.copy(inputStream, file.toPath());
            } catch (Exception exception) {
                alkabot.getLogger().error("Internal error: failed to export '" + lang + ".json' to the language directory. To view the full error, enable the debug mode.");
                alkabot.getLogger().debug("Full trace:", exception);
                return false;
            }
        }

        return true;
    }

    public boolean load() {
        String langFileName = alkabot.getConfig().getLangFile();

        if (alkabot.getArgs().getOverrideLang() != null) {
            langFileName = alkabot.getArgs().getOverrideLang();
            alkabot.getLogger().info("(override) Using language file '" + langFileName + ".json'");
        }

        File file = new File(alkabot.getArgs().getLangDirectoryPath() + "/" + langFileName + ".json");

        alkabot.getLogger().info("Reading language from '" + file.getName() + "'");
        alkabot.getLogger().debug("Absolute language file path: " + file.getAbsolutePath());

        if (!file.exists()) {
            alkabot.getLogger().error("No language file was found at path '" + file.getAbsolutePath() + "'");
            return false;
        }

        if (file.isDirectory()) {
            alkabot.getLogger().error("The language file at path '" + file.getAbsolutePath() + "' is a directory.");
            return false;
        }

        String content;

        try {
            content = Files.readString(file.toPath());
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to read or access language file '" + file.getAbsolutePath() + "'. The file format may not be valid, or the bot may not have access to it. To view the full error, enable the debug mode.");
            alkabot.getLogger().debug("Full trace:", exception);
            return false;
        }

        alkabot.getLogger().info("Loading language pack");

        // Read values
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();

            Map<?, LinkedTreeMap<? , ?>> map = gson.fromJson(content, Map.class);
            for (Map.Entry<?, LinkedTreeMap<? , ?>> entry : map.entrySet())
                readEntry(entry.getKey()+"", entry);
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to read JSON from file '" + file.getAbsolutePath() + "'. Please check the syntax of your file before reporting this error. To view the full error, enable the debug mode.");
            alkabot.getLogger().debug("Full trace:", exception);
            return false;
        }

        // Setup dates
        if (translations.get("date.format") == null) {
            alkabot.getLogger().warn("No date format provided, using default");
        } else {
            Lang.setDateFormat(translations.get("date.format").toString());
        }
        alkabot.getLogger().debug("Using date format '" + Lang.getDateFormat() + "'");

        if (translations.get("date.locale") == null) {
            alkabot.getLogger().warn("No date locale provided, using english");
        } else {
            Locale locale = Locale.forLanguageTag(translations.get("date.locale").toString());
            Lang.setDateLocale(locale);
            alkabot.getLogger().debug("Using date locale '" + locale.toString() + "'");
        }

        Lang.setTranslations(translations);
        translations = null;

        alkabot.getLogger().info("Finished loading " + Lang.getTranslations().size() + " values from '" + file.getName() + "'");

        return true;
    }

    public boolean isDirectoryEmpty(Path path) throws IOException {
        try (Stream<Path> entries = Files.list(path)) {
            return entries.findFirst().isEmpty();
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
}
