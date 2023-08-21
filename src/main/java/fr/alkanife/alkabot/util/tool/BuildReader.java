package fr.alkanife.alkabot.util.tool;

import fr.alkanife.alkabot.Alkabot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BuildReader {

    public BuildReader(Alkabot alkabot) {
        String version = readResource("/version.txt");
        alkabot.setVersion(version);

        alkabot.setSnapshotBuild(version.contains("beta")
                || version.contains("dev")
                || version.contains("snapshot")
                || version.contains("preview")
                || version.contains("alpha"));

        alkabot.setBuild(readResource("/build.txt"));
    }

    private String readResource(String resourcePath) {
        String string = "unknown";

        if (resourcePath != null) {
            try {
                InputStream inputStream = BuildReader.class.getResourceAsStream(resourcePath);
                if (inputStream != null)
                    string = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            } catch (Exception ignore) {}
        }

        return string;
    }

}
