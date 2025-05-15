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
package dev.alka.discordbot.command.admin;

import dev.alka.discordbot.DiscordBot;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

public class TerminalCommandRunnable implements Runnable {

    private final DiscordBot discordBot;

    @Getter @Setter
    private boolean running;
    @Getter
    private final Scanner scanner;

    public TerminalCommandRunnable(DiscordBot discordBot) {
        this.discordBot = discordBot;
        running = true;
        scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (running) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line != null) {
                    if (!line.isEmpty())
                        if (!line.equals("\n"))
                            new AdminCommandHandler(discordBot, line, null);
                }
            }
        }
        scanner.close();
    }
}