package dev.alkanife.alkabot.util;

import dev.alkanife.alkabot.Alkabot;

public class TimeTracker {

    private final Alkabot alkabot;

    private long startTime;

    public TimeTracker() {
        this.alkabot = Alkabot.getInstance();
    }

    public void start() {
        if (!alkabot.getArgs().isTrackTime())
            return;

        startTime = System.currentTimeMillis();
    }

    public void end(String message) {
        if (!alkabot.getArgs().isTrackTime())
            return;

        long timeSpan = System.currentTimeMillis() - startTime;

        alkabot.getLogger().debug(message + ": " + timeSpan + "ms");
    }
}
