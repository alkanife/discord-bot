package dev.alkanife.alkabot.command.admin;

import dev.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record AdminCommandExecution(Alkabot alkabot, String command, MessageReceivedEvent messageReceivedEvent) {

    public boolean isFromDiscord() {
        return messageReceivedEvent != null;
    }

    public void reply(String s) {
        if (isFromDiscord()) {
            messageReceivedEvent.getMessage().reply("```yaml\n" + s + "\n```").queue();
        } else {
            String[] lines = s.split("\n");
            for (String line : lines)
                alkabot.getLogger().info(line);
        }
    }

}
