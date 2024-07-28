package dev.alkanife.alkabot.notification.notifier;

import dev.alkanife.alkabot.configuration.json.notifications.MessageNotifConfig;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.discord.event.MessageListener;
import dev.alkanife.alkabot.notification.CachedMessage;
import dev.alkanife.alkabot.notification.NotificationChannel;
import dev.alkanife.alkabot.notification.NotificationManager;
import dev.alkanife.alkabot.notification.NotificationUtils;
import dev.alkanife.alkabot.util.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class MessageNotifier extends Notifier {

    private final MessageNotifConfig jsonNotificationsMessage;

    public MessageNotifier(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MESSAGE);
        jsonNotificationsMessage = alkabot.getConfig().getNotifConfig().getMessageNotifConfig();
    }

    public void notifyEdit(MessageUpdateEvent event) {
        if (!jsonNotificationsMessage.isEdit())
            return;

        String beforeMessage = null;

        for (CachedMessage sentMessage : MessageListener.cachedMessageList)
            if (sentMessage.getId() == event.getMessageIdLong())
                beforeMessage = StringUtils.limitString(sentMessage.getContent(), 1000);

        if (beforeMessage == null)
            beforeMessage = Lang.t("notification.generic.unknown").getValue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(
                Lang.t("notification.message.edit.title")
                        .parseUserNames(event.getAuthor())
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setColor(Lang.t("notification.message.edit.color").getColor());
        embed.setThumbnail(
                Lang.t("notification.message.edit.icon")
                        .parseUserAvatars(event.getAuthor())
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(event.getGuild())
                        .getImage()
        );
        embed.setDescription(
                "[" + Lang.t("notification.message.edit.jump").getValue() + "](" + event.getMessage().getJumpUrl() + ")"
        );
        embed.addField(NotificationUtils.createChannelField("notification.message.edit", event.getChannel(), true));
        embed.addField(NotificationUtils.createUserField("notification.message.edit", event.getAuthor(), true));

        embed.addField(Lang.t("notification.message.edit.before").getValue(), beforeMessage, false);

        String after = StringUtils.limitString(event.getMessage().getContentDisplay(), 1000);

        if (after.isEmpty())
            after = Lang.t("notification.generic.attachment").getValue();

        embed.addField(Lang.t("notification.message.edit.after").getValue(), after, false);

        notificationManager.sendNotification(notificationChannel, new MessageCreateBuilder().addEmbeds(embed.build()).build());
    }

    public void notifyDelete(MessageDeleteEvent event) { // todo: detect moderator
        if (!jsonNotificationsMessage.isDelete())
            return;

        CachedMessage cachedMessage = null;

        for (CachedMessage sentMessage : MessageListener.cachedMessageList)
            if (sentMessage.getId() == event.getMessageIdLong())
                cachedMessage = sentMessage;

        User author = null;
        String message = null;

        if (cachedMessage != null) {
            Member member = alkabot.getGuild().getMemberById(cachedMessage.getAuthor());

            if (member != null)
                author = member.getUser();

            message = cachedMessage.getContent();
        }

        if (message == null)
            message = Lang.t("notification.generic.unknown").getValue();
        else
            message = StringUtils.limitString(cachedMessage.getContent().equals("") ? Lang.t("notification.generic.attachment").getValue() : cachedMessage.getContent(), 1000);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.message.delete.color").getColor());
        embed.setTitle(
                Lang.t("notification.message.delete.title")
                        .parseUserNames(author)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.message.delete.icon")
                        .parseUserAvatars(author)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(event.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createChannelField("notification.message.delete", event.getChannel(), true));
        embed.addField(NotificationUtils.createUserField("notification.message.delete", author, true));
        embed.addField(Lang.t("notification.message.delete.message").getValue(), message, false);

        notificationManager.sendNotification(notificationChannel, new MessageCreateBuilder().addEmbeds(embed.build()).build());
    }
}