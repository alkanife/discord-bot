package dev.alkanife.alkabot.file;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.util.timetracker.TimeTracker;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;

public abstract class FileManipulation {

    @Getter
    private final Alkabot alkabot;
    @Getter
    private final File file;

    @Getter @Setter
    private String fileContent;

    public FileManipulation(@NotNull Alkabot alkabot, @NotNull File file) {
        this.alkabot = alkabot;
        this.file = file;
    }

    public @NotNull ManipulationState readFile() {
        fileContent = null;

        if (!file.exists()) {
            return ManipulationState.FILE_DONT_EXISTS;
        }

        if (file.isDirectory()) {
            return ManipulationState.FILE_IS_DIRECTORY;
        }

        alkabot.getLogger().debug("Reading content of '" + file.getAbsolutePath() + "'");

        String tracking = TimeTracker.startUnique("read-file");

        try {
            fileContent = Files.readString(file.toPath());
        } catch (Exception exception) {
            alkabot.getLogger().error(FileErrorMessages.failedToReadOrAccess(file), exception);
            TimeTracker.kill(tracking);
            return ManipulationState.ERROR_READ;
        }

        TimeTracker.end(tracking);

        return ManipulationState.SUCCESS;
    }

    public @NotNull ManipulationState writeFile(boolean isNew, @Nullable String content) {
        alkabot.getLogger().debug((isNew ? "Creating new file to '" : "Writing to '") + file.getAbsolutePath() + "'");

        if (content == null)
            return ManipulationState.NO_CONTENT_GIVEN;

        String tracking = TimeTracker.startUnique("write-file");

        try {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
        } catch (Exception exception) {
            alkabot.getLogger().error(FileErrorMessages.failedToCreateParents(file), exception);
            TimeTracker.kill(tracking);
            return ManipulationState.PARENT_DIRECTORY_ERROR;
        }

        try {
            Files.writeString(file.toPath(), content);
        } catch (Exception exception) {
            alkabot.getLogger().error(FileErrorMessages.failedToWrite(file), exception);
            TimeTracker.kill(tracking);
            return ManipulationState.ERROR_WRITE;
        }

        TimeTracker.end(tracking);
        return ManipulationState.SUCCESS;
    }

    public @NotNull ManipulationState deleteFile() {
        alkabot.getLogger().debug("Deleting file at '" + file.getAbsolutePath() + "'");

        String tracking = TimeTracker.startUnique("delete-file");

        try {
            file.delete();
        } catch (Exception exception) {
            alkabot.getLogger().error(FileErrorMessages.failedToDelete(file));
            TimeTracker.kill(tracking);
            return ManipulationState.ERROR_DELETE;
        }

        TimeTracker.end(tracking);
        return ManipulationState.SUCCESS;
    }
}
