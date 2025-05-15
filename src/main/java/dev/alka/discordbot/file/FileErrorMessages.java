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