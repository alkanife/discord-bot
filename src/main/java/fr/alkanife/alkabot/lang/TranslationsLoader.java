package fr.alkanife.alkabot.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.tools.JsonLoader;
import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationsLoader extends JsonLoader {

    @Getter
    private HashMap<String, String> translations = new HashMap<>();
    @Getter
    private HashMap<String, List<String>> randomTranslations = new HashMap<>();

    public TranslationsLoader(Alkabot alkabot) {
        super(alkabot);
    }

    @Override
    public String getReloadMessage() {
        return "Reloading translations";
    }

    @Override
    public void processLoad(boolean reload) throws Exception {
        String content = Files.readString(new File(alkabot.getParameters().getConfigurationPath()).toPath());

        readJSON(content);

        alkabot.getLogger().info("Loaded " + translations.size() + " translations");
        alkabot.getTranslationsManager().setTranslations(translations);
        alkabot.getTranslationsManager().setRandomTranslations(randomTranslations);
        success = true;
    }

    public void readJSON(String content) {
        /*
        * Known problem: throw an error when an entry is not in an object
        * Example:
        *
        * ERROR
        * -> "welcome_messages": ["", ""]
        *
        * NO ERROR
        * -> "welcome": {
        *   "messages": ["", ""]
        * }
        *
        * */

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
