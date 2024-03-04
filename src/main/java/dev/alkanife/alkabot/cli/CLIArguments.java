package dev.alkanife.alkabot.cli;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CLIArguments {

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
            names = { "-debug" },
            description = "Enable debug mode",
            order = 3
    )
    private boolean debug = false;

    @Parameter(
            names = { "-debug-jda" },
            description = "Enable debug mode for JDA and Lavaplayer",
            order = 4
    )
    private boolean debugJDA = false;

    @Parameter(
            names = { "-track-time" },
            description = "Track Alkabot loading times",
            order = 5
    )
    private boolean trackTime = false;

    @Parameter(
            names = { "-disable-file-logging" },
            description = "Disable file logging",
            order = 6
    )
    private boolean disableFileLogging = false;

    @Parameter(
            names = { "-latest-log-file-name" },
            description = "The name of the latest log file, with extension",
            order = 7
    )
    private String latestLogFileName = "latest.log";

    @Parameter(
            names = { "-log-file-name-pattern" },
            description = "Name pattern for log files, with extension",
            order = 8
    )
    private String logFileNamePattern = "%d{yyyy-MM-dd}.%i.log";

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
            names = { "-log-file-max-history" },
            description = "Maximum number of log files to keep",
            order = 11
    )
    private int logFileMaxHistory = 10;

    @Parameter(
            names = { "-config-file-path" },
            description = "Path to the configuration file, with extension",
            order = 12
    )
    private String configFilePath = "config/config.json";

    @Parameter(
            names = { "-tokens-file-path" },
            description = "Path to the token file, with extension",
            order = 13
    )
    private String tokensFilePath = "config/tokens.json";

    @Parameter(
            names = { "-data-directory-path" },
            description = "Path to the data directory",
            order = 14
    )
    private String dataDirectoryPath = "data/";

    @Parameter(
            names = { "-lang-directory-path" },
            description = "Path to the language directory",
            order = 15
    )
    private String langDirectoryPath = "lang/";

    @Parameter(
            names = { "-logs-directory-path" },
            description = "Path to the log directory",
            order = 16
    )
    private String logsDirectoryPath = "logs/";

    @Parameter(
            names = { "-override-discord-token" },
            description = "Override the Discord token",
            order = 17
    )
    private String overrideDiscordToken = null;

    @Parameter(
            names = { "-override-spotify-client-id" },
            description = "Override the Spotify client ID",
            order = 18
    )
    private String overrideSpotifyClientId = null;

    @Parameter(
            names = { "-override-spotify-client-secret" },
            description = "Override the Spotify client secret",
            order = 19
    )
    private String overrideSpotifyClientSecret = null;

    @Parameter(
            names = { "-override-lang" },
            description = "Override the language specified in the configuration",
            order = 20
    )
    private String overrideLang = null;
}
