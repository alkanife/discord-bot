package dev.alkanife.alkabot.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import dev.alkanife.alkabot.cli.CLIArguments;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class Logs {

    private static final String CONSOLE_PATTERN = "%date{dd MMM HH:mm:ss} %gray(|) %boldGreen(%-15.-15logger{0}) %gray(|) %highlight(%-5.5level) %gray(|) %msg%n";
    private static final String CONSOLE_DEBUG_PATTERN = "%date{dd MMM HH:mm:ss.SSS} %gray(|) %boldYellow(%thread) %gray(|) %boldGreen(%file:%line) %gray(|) %highlight(%level) %gray(|) %msg%n";

    private static final String FILE_PATTERN = "%date{dd MMM HH:mm:ss} | %logger{0} | %level | %msg%n";
    private static final String FILE_DEBUG_PATTERN = "%date{dd MMM HH:mm:ss.SSS} | %thread | %file:%line | %level | %msg%n";

    public static void setupRootLogger(CLIArguments args) {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        root.addAppender(createConsoleAppender(args, root));

        if (!args.isDisableFileLogging())
            root.addAppender(createFileAppender(args, root));

        if (args.isDebugJDA())
            root.setLevel(Level.DEBUG);
        else
            root.setLevel(Level.INFO);
    }

    private static ConsoleAppender<ILoggingEvent> createConsoleAppender(CLIArguments args, Logger root) {
        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setPattern(args.isDebug() || args.isDebugJDA() ? CONSOLE_DEBUG_PATTERN : CONSOLE_PATTERN);
        consoleEncoder.setContext(root.getLoggerContext());
        consoleEncoder.setCharset(StandardCharsets.UTF_8);
        consoleEncoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(root.getLoggerContext());
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();

        return consoleAppender;
    }

    private static RollingFileAppender<ILoggingEvent> createFileAppender(CLIArguments args, Logger root) {
        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setPattern(args.isDebug() || args.isDebugJDA() ? FILE_DEBUG_PATTERN : FILE_PATTERN);
        fileEncoder.setOutputPatternAsHeader(true);
        fileEncoder.setContext(root.getLoggerContext());
        fileEncoder.setCharset(StandardCharsets.UTF_8);
        fileEncoder.start();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(root.getLoggerContext());
        rollingFileAppender.setEncoder(fileEncoder);
        rollingFileAppender.setFile(args.getLogsDirectoryPath() + "/" + args.getLatestLogFileName());
        rollingFileAppender.setAppend(true);

        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setContext(root.getLoggerContext());
        rollingPolicy.setFileNamePattern(args.getLogsDirectoryPath() + "/" + args.getLogFileNamePattern());
        rollingPolicy.setMaxFileSize(FileSize.valueOf(args.getLogFileMaxSize()));
        rollingPolicy.setMaxHistory(30);
        rollingPolicy.setTotalSizeCap(FileSize.valueOf(args.getLogFileTotalSizeCap()));
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.start();

        return rollingFileAppender;
    }

    public static Logger createLogger(CLIArguments args, Class<?> clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);

        if (args.isDebug())
            logger.setLevel(Level.DEBUG);
        else
            logger.setLevel(Level.INFO);

        return logger;
    }
}
