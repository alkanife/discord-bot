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
package dev.alka.discordbot.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import dev.alka.discordbot.CliArguments;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class Logs {

    private static final String CONSOLE_PATTERN = "%date{dd MMM HH:mm:ss} %gray(|) %boldGreen(%-15.-15logger{0}) %gray(|) %highlight(%-5.5level) %gray(|) %msg%n";
    private static final String CONSOLE_DEBUG_PATTERN = "%date{dd MMM HH:mm:ss.SSS} %gray(|) %boldYellow(%thread) %gray(|) %boldGreen(%file:%line) %gray(|) %highlight(%level) %gray(|) %msg%n";

    private static final String FILE_PATTERN = "%date{dd MMM HH:mm:ss} | %logger{0} | %level | %msg%n";
    private static final String FILE_DEBUG_PATTERN = "%date{dd MMM HH:mm:ss.SSS} | %thread | %file:%line | %level | %msg%n";

    public static void setupRootLogger(CliArguments args) {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        root.addAppender(createConsoleAppender(args, root));

        if (!args.isDisableFileLogging())
            root.addAppender(createFileAppender(args, root));

        root.setLevel(args.isDebugJDA() ? Level.DEBUG : Level.INFO);
    }

    private static ConsoleAppender<ILoggingEvent> createConsoleAppender(CliArguments args, Logger root) {
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

    private static RollingFileAppender<ILoggingEvent> createFileAppender(CliArguments args, Logger root) {
        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setPattern(args.isDebug() || args.isDebugJDA() ? FILE_DEBUG_PATTERN : FILE_PATTERN);
        fileEncoder.setOutputPatternAsHeader(true);
        fileEncoder.setContext(root.getLoggerContext());
        fileEncoder.setCharset(StandardCharsets.UTF_8);
        fileEncoder.start();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(root.getLoggerContext());
        rollingFileAppender.setEncoder(fileEncoder);
        rollingFileAppender.setFile(args.getLatestLogFilePath());
        rollingFileAppender.setAppend(true);

        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setContext(root.getLoggerContext());
        rollingPolicy.setFileNamePattern(args.getArchiveLogFilePath());
        rollingPolicy.setMaxFileSize(FileSize.valueOf(args.getLogFileMaxSize()));
        rollingPolicy.setMaxHistory(args.getLogArchiveMaxHistory());
        rollingPolicy.setTotalSizeCap(FileSize.valueOf(args.getLogFileTotalSizeCap()));
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.start();

        return rollingFileAppender;
    }

    public static Logger createLogger(CliArguments args, Class<?> clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);

        logger.setLevel(args.isDebug() ? Level.DEBUG : Level.INFO);

        return logger;
    }
}