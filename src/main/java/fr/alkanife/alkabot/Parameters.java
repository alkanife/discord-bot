package fr.alkanife.alkabot;

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
            names = { "--verbose", "-V"},
            description = "Enable verbose mode"
    )
    @Getter
    @Setter
    private boolean verbose = false;

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

}
