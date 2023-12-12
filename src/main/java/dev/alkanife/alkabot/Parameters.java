package dev.alkanife.alkabot;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Parameters {

    @Parameter(
            names = { "--help", "-h"},
            description = "Print usage"
    )
    @Getter
    @ToString.Exclude
    private boolean help = false;

    @Parameter(
            names = { "--version", "-v" },
            description = "Print build version"
    )
    @Getter
    @ToString.Exclude
    private boolean version = false;

    @Parameter(
            names = { "--debug", "-dev"},
            description = "Enable debug mode"
    )
    @Getter
    @Setter
    private boolean debug = false;

    @Parameter(
            names = { "--debugjda", "-devjda"},
            description = "Enable debug mode for JDA and lavaplayer"
    )
    @Getter
    @Setter
    private boolean debugJDA = false;

    @Parameter(
            names = { "--debugall", "-devall"},
            description = "Enable debug mode for everything"
    )
    @Getter
    @Setter
    private boolean debugAll = false;

    @Parameter(
            names = { "--config", "-c"},
            description = "Configuration file path"
    )
    @Getter
    @Setter
    private String configurationPath = "config.json";

    @Parameter(
            names = { "--tokens", "-t"},
            description = "Tokens file path"
    )
    @Getter
    @Setter
    private String tokensPath = "tokens.json";

    @Parameter(
            names = { "--data", "-d"},
            description = "Data folder path"
    )
    @Getter
    @Setter
    private String dataPath = "data";

    @Parameter(
            names = { "--logs", "-l"},
            description = "Logs folder path"
    )
    @Getter
    @Setter
    private String logsPath = "logs";

    @Parameter(
            names = { "--langs", "-L"},
            description = "Lang folder path"
    )
    @Getter
    @Setter
    private String langPath = "lang";

    @Parameter(
            names = { "--generateFiles", "-gf"},
            description = "Generate default files"
    )
    @Getter
    @Setter
    private boolean generateFiles = false;

}
