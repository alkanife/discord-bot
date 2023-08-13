package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.configuration.json.notifications.MessageNotifConfig;
import fr.alkanife.alkabot.listener.MessageListener;
import fr.alkanife.alkabot.util.Colors;
import fr.alkanife.alkabot.util.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

public class MessageNotification extends AbstractNotification {

    private final MessageNotifConfig jsonNotificationsMessage;

    public MessageNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MESSAGE);
        jsonNotificationsMessage = getNotificationManager().getAlkabot().getConfig().getNotifConfig().getMessageNotifConfig();
    }

    public void notifyEdit(MessageUpdateEvent event) {
        if (!jsonNotificationsMessage.isEdit())
            return;

        String beforeMessage = null;

        for (CachedMessage sentMessage : MessageListener.cachedMessageList)
            if (sentMessage.getId() == event.getMessageIdLong())
                beforeMessage = StringUtils.limitString(sentMessage.getContent(), 1000);

        if (beforeMessage == null)
            beforeMessage = getNotificationManager().getAlkabot().t("notification.generic.unknown");

        User user = event.getAuthor();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = addUserAvatar(embedBuilder, user);
        embedBuilder.setColor(Colors.CYAN);
        embedBuilder.setTitle(getNotificationManager().getAlkabot().t("notification.message.edit.title"));
        embedBuilder.setDescription("[" + getNotificationManager().getAlkabot().t("notification.message.generic.message") + "](" + event.getMessage().getJumpUrl() + ")");
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.channel"), notifChannel(event.getChannel()), true);
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.message.generic.author"), notifUser(user), true);
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.message.edit.before"), beforeMessage, false);

        String after = StringUtils.limitString(event.getMessage().getContentDisplay(), 1000);

        if (after.equals(""))
            after = getNotificationManager().getAlkabot().t("notification.generic.attachment");

        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.message.edit.after"), after, false);

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    public void notifyDelete(MessageDeleteEvent event) {
        if (!jsonNotificationsMessage.isDelete())
            return;

        MessageChannelUnion messageChannelUnion = event.getChannel();

        CachedMessage cachedMessage = null;

        for (CachedMessage sentMessage : MessageListener.cachedMessageList)
            if (sentMessage.getId() == event.getMessageIdLong())
                cachedMessage = sentMessage;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getNotificationManager().getAlkabot().t("notification.message.delete.title"));
        embedBuilder.setColor(Colors.RED);
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.channel"), notifChannel(messageChannelUnion), true);

        if (cachedMessage == null) {
            embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.message.generic.author"), getNotificationManager().getAlkabot().t("notification.generic.unknown"), true);
            embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.message.generic.message"), getNotificationManager().getAlkabot().t("notification.generic.unknown"), false);
        } else {
            Member member = getNotificationManager().getAlkabot().getGuild().getMemberById(cachedMessage.getAuthor());
            String author = cachedMessage.getAuthor() + " (" + getNotificationManager().getAlkabot().t("notification.message.delete.not_a_member") + ")";

            if (member != null) {
                author = member.getUser().getName() + " (" + member.getAsMention() + ")";
                embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
            }

            embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.message.generic.author"), author, true);
            embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.message.generic.message"), StringUtils.limitString(cachedMessage.getContent().equals("") ? getNotificationManager().getAlkabot().t("notification.generic.attachment") : cachedMessage.getContent(), 1000), false);
        }

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }
}