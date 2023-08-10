package fr.alkanife.alkabot.lang;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TranslationsManager {

    private HashMap<String, String> translations = new HashMap<>();
    private HashMap<String, List<String>> randomTranslations = new HashMap<>();

    public TranslationsManager() {}

    public TranslationsManager(TranslationsLoader translationsLoader) {
        translations = translationsLoader.getTranslations();
        randomTranslations = translationsLoader.getRandomTranslations();
    }

    public HashMap<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(HashMap<String, String> translations) {
        this.translations = translations;
    }

    public HashMap<String, List<String>> getRandomTranslations() {
        return randomTranslations;
    }

    public void setRandomTranslations(HashMap<String, List<String>> randomTranslations) {
        this.randomTranslations = randomTranslations;
    }

    public String t(String key, String... values) {
        if (translations.containsKey(key)) {
            String translation = translations.get(key);

            if (translation == null)
                return nullTranslation(key);

            return replaceValues(translation, values);
        } else
            return missingTranslation(key);
    }

    public String tr(String key, String... values) {
        if (randomTranslations.containsKey(key)) {
            List<String> randomTl = randomTranslations.get(key);

            if (randomTl == null)
                return nullTranslation(key);

            String randomTranslation = randomTl.get(new Random().nextInt(randomTl.size()));

            return replaceValues(randomTranslation, values);
        } else
            return missingTranslation(key);
    }

    public String tri(String key, String... values) {
        String s = tr(key, values);

        if (!StringUtils.isURL(s))
            return "https://share.alkanife.fr/alkabot.png";

        return s;
    }

    private String nullTranslation(String key) {
        Alkabot.getLogger().warn("Null translation at " + key);
        return "{" + key + "}";
    }

    private String missingTranslation(String key) {
        Alkabot.getLogger().warn("Missing translation at " + key);
        return "{" + key + "}";
    }

    private String replaceValues(String translation, String... values) {
        if (values != null) {
            if (values.length > 0) {
                int i = 1;
                for (String value : values) {
                    String query = "[value" + i + "]";
                    translation = translation.replace(query, value);
                    i++;
                }
            }
        }

        return translation;
    }
}
