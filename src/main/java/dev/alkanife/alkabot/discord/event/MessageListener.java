package dev.alkanife.alkabot.discord.event;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.notification.CachedMessage;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MessageListener extends ListenerAdapter {

    private final Alkabot alkabot;

    public static List<CachedMessage> cachedMessageList = new ArrayList<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent messageReceivedEvent) {
        if (!alkabot.getConfig().getNotifConfig().getMessageNotifConfig().isEdit()
                || !alkabot.getConfig().getNotifConfig().getMessageNotifConfig().isDelete())
            return;

        if (messageReceivedEvent.getChannelType().equals(ChannelType.PRIVATE))
            return;

        if (messageReceivedEvent.getAuthor().isBot())
            return;

        // delete the oldest message if the size is > than allowed
        if (cachedMessageList.size() >= alkabot.getConfig().getNotifConfig().getMessageNotifConfig().getCache())
            cachedMessageList.remove(0);

        cachedMessageList.add(new CachedMessage(messageReceivedEvent.getMessageIdLong(),
                messageReceivedEvent.getMessage().getContentDisplay(),
                messageReceivedEvent.getAuthor().getIdLong()));
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent messageUpdateEvent) {
        if (messageUpdateEvent.getAuthor().isBot())
            return;

        alkabot.getNotificationManager().getMessageNotification().notifyEdit(messageUpdateEvent);
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent messageDeleteEvent) {
        alkabot.getNotificationManager().getMessageNotification().notifyDelete(messageDeleteEvent);
    }

}
