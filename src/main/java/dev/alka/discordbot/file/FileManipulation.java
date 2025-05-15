// Copyright (C) 2024 - Arthur Beau ("Alkanife", "Alka") [https://alka.dev]
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package dev.alka.discordbot.file;

import dev.alka.discordbot.DiscordBot;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;

public class FileManipulation {

    @Getter
    private final DiscordBot discordBot;
    @Getter
    private final File file;

    @Getter @Setter
    private String fileContent;

    public FileManipulation(@NotNull DiscordBot discordBot, @NotNull File file) {
        this.discordBot = discordBot;
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

        discordBot.getLogger().debug("Reading content of '{}'", file.getAbsolutePath());

        try {
            fileContent = Files.readString(file.toPath());
        } catch (Exception exception) {
            discordBot.getLogger().error(FileErrorMessages.failedToReadOrAccess(file), exception);
            return ManipulationState.ERROR_READ;
        }

        return ManipulationState.SUCCESS;
    }

    public @NotNull ManipulationState writeFile(boolean isNew, @Nullable String content) {
        discordBot.getLogger().debug("{}{}'", isNew ? "Creating new file to '" : "Writing to '", file.getAbsolutePath());

        if (content == null)
            return ManipulationState.NO_CONTENT_GIVEN;

        try {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
        } catch (Exception exception) {
            discordBot.getLogger().error(FileErrorMessages.failedToCreateParents(file), exception);
            return ManipulationState.PARENT_DIRECTORY_ERROR;
        }

        try {
            Files.writeString(file.toPath(), content);
        } catch (Exception exception) {
            discordBot.getLogger().error(FileErrorMessages.failedToWrite(file), exception);
            return ManipulationState.ERROR_WRITE;
        }

        return ManipulationState.SUCCESS;
    }

    public @NotNull ManipulationState deleteFile() {
        discordBot.getLogger().debug("Deleting file at '{}'", file.getAbsolutePath());

        try {
            file.delete();
        } catch (Exception exception) {
            discordBot.getLogger().error(FileErrorMessages.failedToDelete(file));
            return ManipulationState.ERROR_DELETE;
        }

        return ManipulationState.SUCCESS;
    }
}