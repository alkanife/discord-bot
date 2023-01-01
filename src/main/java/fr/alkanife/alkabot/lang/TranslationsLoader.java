package fr.alkanife.alkabot.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import fr.alkanife.alkabot.Alkabot;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationsLoader {

    private HashMap<String, String> translations = new HashMap<>();
    private HashMap<String, List<String>> randomTranslations = new HashMap<>();

    public TranslationsLoader(boolean reload) throws FileNotFoundException {
        Alkabot.getLogger().info((reload ? "(RELOAD) " : "") + "Reading lang file...");

        File langFile;
        try {
            langFile = new File(Alkabot.getConfig().getLang_file());

            Alkabot.debug("Full lang file path: " + langFile.getPath());

            if (!langFile.exists()) {
                Alkabot.getLogger().error("The lang file was not found");
                return;
            }
        } catch (Exception exception) {
            Alkabot.getLogger().error("Invalid lang file path");
            exception.printStackTrace();
            return;
        }

        String langFileContent;
        try {
            langFileContent = Files.readString(langFile.toPath());
        } catch (IOException exception) {
            Alkabot.getLogger().error("Failed to read the lang file content!");
            exception.printStackTrace();
            return;
        }

        try {
            readJSON(langFileContent);
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to read the JSON of the lang file");
            exception.printStackTrace();
        }


        Alkabot.getLogger().info("Loaded " + translations.size() + " translations");
    }

    public HashMap<String, String> getTranslations() {
        return translations;
    }

    public HashMap<String, List<String>> getRandomTranslations() {
        return randomTranslations;
    }

    public void readJSON(String content) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        Map<?, LinkedTreeMap<? , ?>> map = gson.fromJson(content, Map.class);

        for (Map.Entry<?, LinkedTreeMap<? , ?>> entry : map.entrySet())
            listTreeMap((String) entry.getKey(), entry.getValue());
    }

    public void listTreeMap(String previous, LinkedTreeMap<?, ?> map) {
        for (LinkedTreeMap.Entry<? , ?> entry : map.entrySet()) {
            if (entry.getValue() instanceof LinkedTreeMap<?, ?>) {
                listTreeMap(previous + "." + entry.getKey(), (LinkedTreeMap<?, ?>) entry.getValue());
            } else if (entry.getValue() instanceof String) {
                translations.put(previous + "." + entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof List) {
                randomTranslations.put(previous + "." + entry.getKey(), (List<String>) entry.getValue());
            }
        }
    }

}
