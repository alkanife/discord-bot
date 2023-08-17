package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class VoiceListener extends ListenerAdapter {

    private final Alkabot alkabot;

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getMember().getUser().isBot())
            return;

        AudioChannelUnion joinChannelUnion = event.getChannelJoined();
        AudioChannelUnion leftChannelUnion = event.getChannelLeft();

        if (joinChannelUnion == null && leftChannelUnion == null)
            return;

        if (joinChannelUnion == null) {
            alkabot.getNotificationManager().getVoiceNotification().notifyLeave(event.getMember(), event.getChannelLeft());
            return;
        }

        if (leftChannelUnion == null) {
            alkabot.getNotificationManager().getVoiceNotification().notifyJoin(event.getMember(), event.getChannelJoined());
            return;
        }

        alkabot.getNotificationManager().getVoiceNotification().notifyMove(event.getMember(), event.getChannelJoined(), event.getChannelLeft()); // todo: moderator move
    }

}
