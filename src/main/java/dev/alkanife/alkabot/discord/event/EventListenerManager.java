package dev.alkanife.alkabot.discord.event;

import dev.alkanife.alkabot.Alkabot;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDABuilder;

@AllArgsConstructor
public class EventListenerManager {

    private final Alkabot alkabot;

    public void addEventListeners(JDABuilder jdaBuilder) {
        jdaBuilder.addEventListeners(new ReadyListener(alkabot),
                new CommandListener(alkabot),
                new MessageListener(alkabot),
                new MemberListener(alkabot),
                new ModeratorListener(alkabot),
                new VoiceListener(alkabot));
    }

}
