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