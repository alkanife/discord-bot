package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record AdminCommandExecution(String command, MessageReceivedEvent messageReceivedEvent) {

    public boolean isFromDiscord() {
        return messageReceivedEvent != null;
    }

    public void reply(String s) {
        if (isFromDiscord())
            messageReceivedEvent.getMessage().reply("```yaml\n" + s + "\n```").queue();
        else
            Alkabot.getLogger().info(s);
    }

}
