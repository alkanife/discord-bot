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
            description = "Track loading times (for debug purposes)",
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
            names = { "-secret-file-path" },
            description = "Path to the secret file, with extension",
            order = 12
    )
    private String secretFilePath = "config/secrets.json";

    @Parameter(
            names = { "-config-file-path" },
            description = "Path to the configuration file, with extension",
            order = 13
    )
    private String configFilePath = "config/config.json";

    @Parameter(
            names = { "-music-data-file-path" },
            description = "Path to the music data, with extension",
            order = 14
    )
    private String musicDataFilePath = "data/music.json";

    @Parameter(
            names = { "-override-lang-file-path" },
            description = "Override the language file path (ignore configuration)",
            order = 15
    )
    private String overrideLangFilePath = null;

    @Parameter(
            names = { "-override-secrets" },
            description = "Override secrets",
            order = 16
    )
    private String overrideSecrets = null;
}
