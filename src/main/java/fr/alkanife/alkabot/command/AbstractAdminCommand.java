package fr.alkanife.alkabot.command;

public abstract class AbstractAdminCommand {

    public abstract String getName();
    public abstract String getUsage();
    public abstract String getDescription();

    public abstract void execute(AdminCommandExecution execution);
}
