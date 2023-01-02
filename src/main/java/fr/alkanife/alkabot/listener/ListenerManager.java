package fr.alkanife.alkabot.listener;

import net.dv8tion.jda.api.JDABuilder;

public class ListenerManager {

    public ListenerManager() {}

    public void initialize(JDABuilder jdaBuilder) {
        jdaBuilder.addEventListeners(new ReadyListener(),
                new CommandListener(),
                new MessageListener(),
                new MemberListener(),
                new ModeratorListener(),
                new VoiceListener());
    }

}
