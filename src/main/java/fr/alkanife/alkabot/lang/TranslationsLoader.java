package fr.alkanife.alkabot.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.util.tool.JsonLoader;
import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class TranslationsLoader extends JsonLoader {

    @Getter
    private HashMap<String, Object> translations = new HashMap<>();

    public TranslationsLoader(Alkabot alkabot) {
        super(alkabot);
    }

    @Override
    public String getReloadMessage() {
        return "Reloading translations";
    }

    @Override
    public void processLoad(boolean reload) throws Exception {
        File file = new File(alkabot.getParameters().getLangPath() + "/" + alkabot.getConfig().getLangFile() + ".json");
        alkabot.verbose(file.getPath());

        String content = Files.readString(file.toPath());
        Gson gson = new GsonBuilder().serializeNulls().create();

        // Read JSON
        Map<?, LinkedTreeMap<? , ?>> map = gson.fromJson(content, Map.class);
        for (Map.Entry<?, LinkedTreeMap<? , ?>> entry : map.entrySet())
            readEntry(entry.getKey()+"", entry);

        // Setup dates
        if (translations.get("date.format") == null) {
            alkabot.getLogger().warn("No date format provided, using default");
        } else {
            Lang.setDateFormat(translations.get("date.format").toString());
        }
        alkabot.verbose("date format: "+Lang.getDateFormat());

        if (translations.get("date.locale") == null) {
            alkabot.getLogger().warn("No date locale provided, using ENGLISH");
        } else {
            Locale locale = Locale.forLanguageTag(translations.get("date.locale").toString());
            Lang.setDateLocale(locale);
            alkabot.verbose("Using date locale " + locale.toString());
        }

        // Set translations
        Lang.setTranslations(translations);

        alkabot.getLogger().info("Loaded " + translations.size() + " translations");
        success = true;
    }

    public void readEntry(String address, LinkedTreeMap.Entry<?, ?> entry) {
        if (entry.getValue() instanceof LinkedTreeMap<?, ?> mapValue) {
            for (LinkedTreeMap.Entry<?, ?> entryValue : mapValue.entrySet())
                readEntry(address + "." + entryValue.getKey(), entryValue);
        } else {
            translations.put(address + "." + entry.getKey(), entry.getValue());
            //alkabot.verbose(address + " = " + entry.getValue().toString());
        }
    }
}
