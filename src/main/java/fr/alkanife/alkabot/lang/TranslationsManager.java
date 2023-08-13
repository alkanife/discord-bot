package fr.alkanife.alkabot.lang;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TranslationsManager {

    @Getter
    private Alkabot alkabot;
    @Getter @Setter
    private HashMap<String, String> translations = new HashMap<>();
    @Getter @Setter
    private HashMap<String, List<String>> randomTranslations = new HashMap<>();

    public TranslationsManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public String translate(String key, String... values) {
        if (translations.containsKey(key)) {
            String translation = translations.get(key);

            if (translation == null)
                return nullTranslation(key);

            return replaceValues(translation, values);
        } else
            return missingTranslation(key);
    }

    public String translateRandom(String key, String... values) {
        if (randomTranslations.containsKey(key)) {
            List<String> randomTl = randomTranslations.get(key);

            if (randomTl == null)
                return nullTranslation(key);

            String randomTranslation = randomTl.get(new Random().nextInt(randomTl.size()));

            return replaceValues(randomTranslation, values);
        } else
            return missingTranslation(key);
    }

    public String translateRandomImage(String key, String... values) {
        String s = translateRandom(key, values);

        if (!StringUtils.isURL(s))
            return "";

        return s;
    }

    private String nullTranslation(String key) {
        alkabot.getLogger().warn("Null translation at " + key);
        return "{" + key + "}";
    }

    private String missingTranslation(String key) {
        alkabot.getLogger().warn("Missing translation at " + key);
        return "{" + key + "}";
    }

    private String replaceValues(String translation, String... values) {
        if (values != null) {
            if (values.length > 0) {
                int i = 1;
                for (String value : values) {
                    String query = "<" + i + ">";
                    translation = translation.replace(query, value);
                    i++;
                }
            }
        }

        return translation;
    }
}
