package fr.alkanife.alkabot.utils;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlkabotUtils {

    public static void cleanLogs() throws IOException {
        Alkabot.debug("Moving old 'latest.log' file to the logs/ folder");

        String absolutePath = Paths.get("").toAbsolutePath().toString();

        File latestLogs = new File(absolutePath + "/latest.log");

        if (latestLogs.exists()) {
            Alkabot.debug("latest.log file existing");
            System.out.println("Cleaning logs...");

            File logsFolder = new File(absolutePath + "/logs");

            if (logsFolder.exists()) {
                Alkabot.debug("logs/ folder already existing");
                if (!logsFolder.isDirectory()) {
                    System.out.println(absolutePath + "/logs is not a directory");
                    return;
                }
            } else {
                System.out.println("No logs/ directory found, creating one");
                logsFolder.mkdir();
            }

            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String newPath = absolutePath + "/logs/before-" + date + ".log";

            Alkabot.debug("Moving latest.log file to " + newPath);
            Files.move(latestLogs.toPath(), Paths.get(newPath));
        } else {
            Alkabot.debug("No latest.log file");
        }
    }

    public static boolean isDevBuild() {
        String version = Alkabot.VERSION.toLowerCase();

        return version.contains("beta")
                || version.contains("dev")
                || version.contains("snapshot")
                || version.contains("alpha");
    }

    public static Activity buildActivity() {
        Alkabot.debug("Building activity");

        Activity.ActivityType activityType = Activity.ActivityType.valueOf(Alkabot.getConfig().getGuild().getPresence().getActivity().getType());
        return Activity.of(activityType, Alkabot.getConfig().getGuild().getPresence().getActivity().getText());
    }

}
