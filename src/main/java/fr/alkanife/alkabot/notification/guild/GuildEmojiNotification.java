package fr.alkanife.alkabot.notification.guild;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuildEmojiNotification extends AbstractGuildNotification {

    public GuildEmojiNotification(GuildNotification guildNotification) {
        super(guildNotification);
    }

    public void notifyCreate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isEmoji_added())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyDelete(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isEmoji_delete())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyUpdate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isEmoji_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
