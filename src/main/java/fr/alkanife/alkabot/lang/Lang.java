package fr.alkanife.alkabot.lang;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.HashMap;
import java.util.Locale;

public class Lang {

    @Getter
    @Setter
    private static HashMap<String, Object> translations = new HashMap<>();

    @Getter
    @Setter
    private static String defaultImageURL = "https://cdn.discordapp.com/embed/avatars/1.png";

    @Getter
    @Setter
    private static String dateFormat = "EEE, d MMM yyyy HH:mm:ss";

    @Getter
    @Setter
    private static Locale dateLocale = Locale.ENGLISH;

    @Getter
    @Setter
    private static Color defaultColor = new Color(255, 255, 255);

    /**
     * Get key's value
     *
     * @param key JSON key
     * @return The value
     */
    public static String get(String key) {
        return new TranslationHandler(key).getValue();
    }

    /**
     * Get key's image
     *
     * @param key JSON key
     * @return The value
     */
    public static String getImage(String key) {
        return new TranslationHandler(key).getImage();
    }

    /**
     * Get key's image
     *
     * @param key JSON key
     * @return The value
     */
    public static Color getColor(String key) {
        return new TranslationHandler(key).getColor();
    }

    /**
     * Create a translation handler for the key
     *
     * @param key JSON key
     * @return The value
     */
    public static TranslationHandler t(String key) {
        return new TranslationHandler(key);
    }
}
