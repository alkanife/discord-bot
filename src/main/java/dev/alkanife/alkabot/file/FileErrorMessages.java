package dev.alkanife.alkabot.file;

import java.io.File;

public class FileErrorMessages {

    public static String failedToReadOrAccess(File file) {
        return "Cannot access or read file '" + file.getAbsolutePath() + "'. Its format may not be valid, or the bot may not have access to it.";
    }

    public static String failedToCreateParents(File file) {
        return "Failed to create parent directories for '" + file.getAbsolutePath() + "'.";
    }

    public static String failedToWrite(File file) {
        return "Failed to write file '" + file.getAbsolutePath() + "'";
    }

    public static String failedToCreate(File file) {
        return "Failed to create file '" + file.getAbsolutePath() + "'";
    }

    public static String failedToDelete(File file) {
        return "Failed to delete file '" + file.getAbsolutePath() + "'";
    }

    public static String jsonError(File file) {
        return "Cannot read JSON from file '" + file.getAbsolutePath() + "'. Check the syntax";
    }
}
