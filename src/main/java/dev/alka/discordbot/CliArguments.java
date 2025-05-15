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
package dev.alka.discordbot;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CliArguments {

    @Parameter(
            names = { "-help", "-h" },
            description = "Display usage",
            help = true,
            order = 1
    )
    private boolean help = false;

    @Parameter(
            names = { "-version", "-v" },
            description = "Display version",
            order = 2
    )
    private boolean version = false;

    @Parameter(
            names = { "-start" },
            description = "Start the bot!",
            help = true,
            order = 3
    )
    private boolean start = false;

    @Parameter(
            names = { "-debug" },
            description = "Enable debug mode",
            order = 4
    )
    private boolean debug = false;

    @Parameter(
            names = { "-debug-jda" },
            description = "Enable debug mode for JDA and Lavaplayer",
            order = 5
    )
    private boolean debugJDA = false;

    @Parameter(
            names = { "-disable-file-logging" },
            description = "Disable file logging",
            order = 6
    )
    private boolean disableFileLogging = false;

    @Parameter(
            names = { "-latest-log-file-path" },
            description = "Path to the latest log file, with extension",
            order = 7
    )
    private String latestLogFilePath = "latest.log";

    @Parameter(
            names = { "-archive-log-file-path" },
            description = "Path to the log archive, with name pattern",
            order = 8
    )
    private String archiveLogFilePath = "logs/%d{yyyy-MM-dd}.%i.log";

    @Parameter(
            names = { "-log-file-max-size" },
            description = "Maximum size of a log file",
            order = 9
    )
    private String logFileMaxSize = "10MB";

    @Parameter(
            names = { "-log-file-total-size-cap" },
            description = "Maximum size of all log files",
            order = 10
    )
    private String logFileTotalSizeCap = "1GB";

    @Parameter(
            names = { "-log-archive-max-history" },
            description = "Maximum number of log files to keep",
            order = 11
    )
    private int logArchiveMaxHistory = 10;

    @Parameter(
            names = { "-secrets" },
            description = "JSON file containing secret tokens",
            order = 11
    )
    private String secretsPath = "secrets.json";

    @Parameter(
            names = { "-config" },
            description = "JSON file containing the configuration",
            order = 11
    )
    private String configPath = "config.json";

}
