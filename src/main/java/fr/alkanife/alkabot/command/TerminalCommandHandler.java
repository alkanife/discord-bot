package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

public class TerminalCommandHandler implements Runnable {

    private final Alkabot alkabot;

    @Getter @Setter
    private boolean running;
    @Getter
    private Scanner scanner;

    public TerminalCommandHandler(Alkabot alkabot) {
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
                    if (!line.equals(""))
                        if (!line.equals("\n"))
                            alkabot.getCommandManager().handleAdmin(new AdminCommandExecution(alkabot, line.toLowerCase(), null));
                }
            }
        }
        scanner.close();
    }
}
