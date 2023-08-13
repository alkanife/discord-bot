package fr.alkanife.alkabot.util.tool;

import fr.alkanife.alkabot.Alkabot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogsCleaner {

    public LogsCleaner(Alkabot alkabot) throws IOException {
        String paramLogs = alkabot.getParameters().getLogsPath();

        alkabot.verbose("Moving old 'latest.log' file to the " + paramLogs + "/ folder");

        String absolutePath = Paths.get("").toAbsolutePath().toString();

        File latestLogs = new File(absolutePath + "/latest.log");

        if (latestLogs.exists()) {
            alkabot.verbose("latest.log file existing");
            System.out.println("Cleaning logs...");

            File logsFolder = new File(absolutePath + "/" + paramLogs);

            if (logsFolder.exists()) {
                alkabot.verbose(paramLogs + "/ folder already existing");
                if (!logsFolder.isDirectory()) {
                    System.out.println(absolutePath + "/" + paramLogs + " is not a directory");
                    return;
                }
            } else {
                System.out.println("No " + paramLogs + "/ directory found, creating one");
                if (!logsFolder.mkdir())
                    System.out.println("Failed to create " + paramLogs + "/ directory");
            }

            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String newPath = absolutePath + "/" + paramLogs + "/before-" + date + ".log";

            alkabot.verbose("Moving latest.log file to " + newPath);
            Files.move(latestLogs.toPath(), Paths.get(newPath));
        } else {
            alkabot.verbose("No latest.log file");
        }
    }
}
