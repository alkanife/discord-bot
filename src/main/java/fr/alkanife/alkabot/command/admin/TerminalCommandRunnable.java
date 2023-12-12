package fr.alkanife.alkabot.command.admin;

import fr.alkanife.alkabot.Alkabot;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

public class TerminalCommandRunnable implements Runnable {

    private final Alkabot alkabot;

    @Getter @Setter
    private boolean running;
    @Getter
    private final Scanner scanner;

    public TerminalCommandRunnable(Alkabot alkabot) {
        this.alkabot = alkabot;
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
                            new AdminCommandHandler(alkabot, new AdminCommandExecution(alkabot, line.toLowerCase(), null));
                }
            }
        }
        scanner.close();
    }
}
