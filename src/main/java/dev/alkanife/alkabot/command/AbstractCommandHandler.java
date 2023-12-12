package dev.alkanife.alkabot.command;

import dev.alkanife.alkabot.Alkabot;
import lombok.Getter;

public abstract class AbstractCommandHandler {

    @Getter
    private final Alkabot alkabot;

    public AbstractCommandHandler(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

}
