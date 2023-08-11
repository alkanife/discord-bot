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
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent guildVoiceUpdateEvent) {
        if (guildVoiceUpdateEvent.getMember().getUser().isBot())
            return;

        AudioChannelUnion joinChannelUnion = guildVoiceUpdateEvent.getChannelJoined();
        AudioChannelUnion leftChannelUnion = guildVoiceUpdateEvent.getChannelLeft();

        if (joinChannelUnion == null && leftChannelUnion == null)
            return;

        if (joinChannelUnion == null) {
            alkabot.getNotificationManager().getVoiceNotification().notifyLeave(guildVoiceUpdateEvent);
            return;
        }

        if (leftChannelUnion == null) {
            alkabot.getNotificationManager().getVoiceNotification().notifyJoin(guildVoiceUpdateEvent);
            return;
        }

        alkabot.getNotificationManager().getVoiceNotification().notifyMove(guildVoiceUpdateEvent);
    }

}
