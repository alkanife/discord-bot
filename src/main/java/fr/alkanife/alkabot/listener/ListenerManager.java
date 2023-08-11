package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDABuilder;

@AllArgsConstructor
public class ListenerManager {

    private final Alkabot alkabot;

    public void initialize(JDABuilder jdaBuilder) {
        jdaBuilder.addEventListeners(new ReadyListener(alkabot),
                new CommandListener(alkabot),
                new MessageListener(alkabot),
                new MemberListener(alkabot),
                new ModeratorListener(alkabot),
                new VoiceListener(alkabot));
    }

}
