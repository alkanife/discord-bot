package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.notification.CachedMessage;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageListener extends ListenerAdapter {

    public static List<CachedMessage> cachedMessageList = new ArrayList<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent messageReceivedEvent) {
        if (!Alkabot.getConfig().getNotifications().getMessage().isEdit()
                || !Alkabot.getConfig().getNotifications().getMessage().isDelete())
            return;

        if (messageReceivedEvent.getChannelType().equals(ChannelType.PRIVATE))
            return;

        if (messageReceivedEvent.getAuthor().isBot())
            return;

        // delete the oldest message if the size is > than allowed
        if (cachedMessageList.size() >= Alkabot.getConfig().getNotifications().getMessage().getCache())
            cachedMessageList.remove(0);

        cachedMessageList.add(new CachedMessage(messageReceivedEvent.getMessageIdLong(),
                messageReceivedEvent.getMessage().getContentDisplay(),
                messageReceivedEvent.getAuthor().getIdLong()));
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent messageUpdateEvent) {
        if (messageUpdateEvent.getAuthor().isBot())
            return;

        Alkabot.getNotificationManager().getMessageNotification().notifyEdit(messageUpdateEvent);
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent messageDeleteEvent) {
        Alkabot.getNotificationManager().getMessageNotification().notifyDelete(messageDeleteEvent);
    }

}
