package dev.alkanife.alkabot.util.command;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandList {

    private final HashMap<String, String> commands;

    @Getter @Setter
    private int paddingLeft = 0;
    @Getter @Setter
    private int paddingRight = 3;

    public CommandList(HashMap<String, String> commands) {
        this.commands = commands;
    }

    public List<String> getLines() {
        int commandWidth = calcNameWidth();

        List<String> lines = new ArrayList<>();

        for (String command : commands.keySet())
            lines.add(getPadding(paddingLeft) + formatCommand(commandWidth, command) + getPadding(paddingRight) + commands.get(command));

        return lines;
    }

    private int calcNameWidth() {
        int width = 0;

        for (String command : commands.keySet()) {
            if (command.length() > width) {
                width = command.length();
            }
        }

        return width;
    }
    private String formatCommand(int width, String name) {
        StringBuilder nameBuilder = new StringBuilder(name);

        while (nameBuilder.length() < width)
            nameBuilder.append(" ");

        return nameBuilder.toString();
    }

    private String getPadding(int padding) {
        return " ".repeat(Math.max(0, padding));
    }
}
