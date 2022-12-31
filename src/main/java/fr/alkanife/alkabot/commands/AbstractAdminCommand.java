package fr.alkanife.alkabot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class AbstractAdminCommand {

    public abstract String getName();
    public abstract String getUsage();
    public abstract String getDescription();

    public abstract void execute(MessageReceivedEvent event);
}
