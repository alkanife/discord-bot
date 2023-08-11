package fr.alkanife.alkabot.utils.tools;

import fr.alkanife.alkabot.Alkabot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogsCleaner {

    public LogsCleaner(Alkabot alkabot) throws IOException {
        alkabot.verbose("Moving old 'latest.log' file to the logs/ folder");

        String absolutePath = Paths.get("").toAbsolutePath().toString();

        File latestLogs = new File(absolutePath + "/latest.log");

        if (latestLogs.exists()) {
            alkabot.verbose("latest.log file existing");
            System.out.println("Cleaning logs...");

            File logsFolder = new File(absolutePath + "/logs");

            if (logsFolder.exists()) {
                alkabot.verbose("logs/ folder already existing");
                if (!logsFolder.isDirectory()) {
                    System.out.println(absolutePath + "/logs is not a directory");
                    return;
                }
            } else {
                System.out.println("No logs/ directory found, creating one");
                if (!logsFolder.mkdir())
                    System.out.println("Failed to create logs/ directory");
            }

            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String newPath = absolutePath + "/logs/before-" + date + ".log";

            alkabot.verbose("Moving latest.log file to " + newPath);
            Files.move(latestLogs.toPath(), Paths.get(newPath));
        } else {
            alkabot.verbose("No latest.log file");
        }
    }
}
