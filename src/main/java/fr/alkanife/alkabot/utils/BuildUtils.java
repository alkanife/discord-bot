package fr.alkanife.alkabot.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BuildUtils {

    public static String read(String file) {
        String string = "unknown";

        if (file != null) {
            try {
                InputStream inputStream = BuildUtils.class.getResourceAsStream(file);
                if (inputStream != null)
                    string = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            } catch (Exception ignore) {}
        }

        return string;
    }

}
