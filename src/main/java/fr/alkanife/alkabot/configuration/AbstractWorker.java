package fr.alkanife.alkabot.configuration;

import fr.alkanife.alkabot.Alkabot;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWorker {

    private final boolean reload;
    private Status status;
    private List<String> logs;

    public AbstractWorker(boolean reload) {
        this.reload = reload;
        status = Status.PERFECT;
        logs = new ArrayList<>();
    }

    public void log(Level level, String s) {
        if (reload)
            s = "(reload) " + s;

        switch (level) {
            case ERROR -> Alkabot.getLogger().error(s);
            case WARN -> Alkabot.getLogger().warn(s);
            default -> Alkabot.getLogger().info(s);
        }

        if (reload)
            logs.add(level.name() + " " + s);
    }

    public enum Status {
        PERFECT,
        NOT_PERFECT,
        FAIL;
    }

    public boolean reload() {
        return reload;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
