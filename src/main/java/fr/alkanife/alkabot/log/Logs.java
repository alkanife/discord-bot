package fr.alkanife.alkabot.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.Parameters;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class Logs {

    private static final String CONSOLE_PATTERN = "%date{dd MMM HH:mm:ss} %gray(|) %boldGreen(%-15.-15logger{0}) %gray(|) %highlight(%-5.5level) %gray(|) %msg%n";
    private static final String CONSOLE_DEBUG_PATTERN = "%date{dd MMM HH:mm:ss.SSS} %gray(|) %boldYellow(%thread) %gray(|) %boldGreen(%file:%line) %gray(|) %highlight(%level) %gray(|) %msg%n";

    private static final String FILE_PATTERN = "";
    private static final String FILE_DEBUG_PATTERN = "";

    public static void setupRootLogger(Parameters parameters) {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern(parameters.isDebug() || parameters.isDebugJDA() ? CONSOLE_DEBUG_PATTERN : CONSOLE_PATTERN);
        ple.setContext(root.getLoggerContext());
        ple.setCharset(StandardCharsets.UTF_8);
        ple.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(root.getLoggerContext());
        consoleAppender.setEncoder(ple);
        consoleAppender.start();

        root.addAppender(consoleAppender);

        if (parameters.isDebugJDA())
            root.setLevel(Level.DEBUG);
        else
            root.setLevel(Level.INFO);
    }

    public static Logger createLogger(Class<?> clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);

        if (Alkabot.getInstance().getParameters().isDebug())
            logger.setLevel(Level.DEBUG);
        else
            logger.setLevel(Level.INFO);

        return logger;
    }

    public static String invalidFileMessage(String path) {
        return "Unable to load the file at path '" + path + "'. Please make sure that this file exists, and that the bot has access to it. If it's a JSON file, please verify the syntax. To generate default files, use the --generateFiles flag.";
    }
}
