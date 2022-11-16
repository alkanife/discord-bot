package fr.alkanife.alkabot.lang;

import fr.alkanife.alkabot.Alkabot;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class TranslationsLoader {

    private HashMap<String, Object> translations = new HashMap<>();

    public TranslationsLoader(boolean reload) throws FileNotFoundException {
        Alkabot.getLogger().info((reload ? "(RELOAD) " : "") + "Reading " + Alkabot.absolutePath() + "/lang.yml");

        File langFile = new File(Alkabot.absolutePath() + "/lang.yml");

        if (!langFile.exists()) {
            Alkabot.getLogger().warn("Translation file not found");
            return;
        }

        // Reading lang file
        InputStream inputStream = new FileInputStream(langFile);
        Yaml yaml = new Yaml();
        translations = yaml.load(inputStream);

        Alkabot.getLogger().info("Loaded " + translations.size() + " translations");
    }

    public HashMap<String, Object> getTranslations() {
        return translations;
    }

}
