package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AdminCommandExecution {

    private final String command;
    private final MessageReceivedEvent messageReceivedEvent;

    public AdminCommandExecution(String command, MessageReceivedEvent messageReceivedEvent) {
        this.command = command;
        this.messageReceivedEvent = messageReceivedEvent;
    }

    public String getCommand() {
        return command;
    }

    public MessageReceivedEvent getMessageReceivedEvent() {
        return messageReceivedEvent;
    }

    public boolean isFromDiscord() {
        return messageReceivedEvent != null;
    }

    public void reply(String s) {
        if (isFromDiscord())
            messageReceivedEvent.getMessage().reply("```yaml\n" + s +"\n```").queue();
        else
            Alkabot.getLogger().info(s);
    }

}
