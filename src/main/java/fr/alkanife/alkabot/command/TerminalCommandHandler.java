package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;

import java.util.Locale;
import java.util.Scanner;

public class TerminalCommandHandler implements Runnable {

    private boolean running;
    private Scanner scanner;

    public TerminalCommandHandler() {
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
                            Alkabot.getCommandManager().handleAdmin(new AdminCommandExecution(line.toLowerCase(), null));
                }
            }
        }
        scanner.close();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
