package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsMessage;
import fr.alkanife.alkabot.listener.MessageListener;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.NotifUtils;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

public class MessageNotification extends AbstractNotification {

    private final JSONNotificationsMessage jsonNotificationsMessage;

    public MessageNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MESSAGE);
        jsonNotificationsMessage = Alkabot.getConfig().getNotifications().getMessage();
    }

    public void notifyEdit(MessageUpdateEvent event) {
        if (!jsonNotificationsMessage.isEdit())
            return;

        String beforeMessage = null;

        for (CachedMessage sentMessage : MessageListener.cachedMessageList)
            if (sentMessage.getId() == event.getMessageIdLong())
                beforeMessage = StringUtils.limitString(sentMessage.getContent(), 1000);

        if (beforeMessage == null)
            beforeMessage = Alkabot.t("notification.generic.unknown");

        User user = event.getAuthor();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = NotifUtils.addUserAvatar(embedBuilder, user);
        embedBuilder.setColor(Colors.CYAN);
        embedBuilder.setTitle(Alkabot.t("notification.message.edit.title"));
        embedBuilder.setDescription("[" + Alkabot.t("notification.message.generic.message") + "](" + event.getMessage().getJumpUrl() + ")");
        embedBuilder.addField(Alkabot.t("notification.generic.channel"), NotifUtils.notifChannel(event.getChannel()), true);
        embedBuilder.addField(Alkabot.t("notification.message.generic.author"), NotifUtils.notifUser(user), true);
        embedBuilder.addField(Alkabot.t("notification.message.edit.before"), beforeMessage, false);

        String after = StringUtils.limitString(event.getMessage().getContentDisplay(), 1000);

        if (after.equals(""))
            after = Alkabot.t("notification.generic.attachment");

        embedBuilder.addField(Alkabot.t("notification.message.edit.after"), after, false);

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
        embedBuilder.setTitle(Alkabot.t("notification.message.delete.title"));
        embedBuilder.setColor(Colors.RED);
        embedBuilder.addField(Alkabot.t("notification.generic.channel"), NotifUtils.notifChannel(messageChannelUnion), true);

        if (cachedMessage == null) {
            embedBuilder.addField(Alkabot.t("notification.message.generic.author"), Alkabot.t("notification.generic.unknown"), true);
            embedBuilder.addField(Alkabot.t("notification.message.generic.message"), Alkabot.t("notification.generic.unknown"), false);
        } else {
            Member member = Alkabot.getGuild().getMemberById(cachedMessage.getAuthor());
            String author = cachedMessage.getAuthor() + " (" + Alkabot.t("notification.message.delete.not_a_member") + ")";

            if (member != null) {
                author = member.getUser().getAsTag() + " (" + member.getAsMention() + ")";
                embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
            }

            embedBuilder.addField(Alkabot.t("notification.message.generic.author"), author, true);
            embedBuilder.addField(Alkabot.t("notification.message.generic.message"), StringUtils.limitString(cachedMessage.getContent().equals("") ? Alkabot.t("notification.generic.attachment") : cachedMessage.getContent(), 1000), false);
        }

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }
}