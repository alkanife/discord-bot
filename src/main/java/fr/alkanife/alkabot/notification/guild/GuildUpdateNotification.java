package fr.alkanife.alkabot.notification.guild;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuildUpdateNotification extends AbstractGuildNotification {

    public GuildUpdateNotification(GuildNotification guildNotification) {
        super(guildNotification);
    }

    public void notifyUnknown(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isGuild_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyName(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isName_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyIcon(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isIcon_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyOwner(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isOwner_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
