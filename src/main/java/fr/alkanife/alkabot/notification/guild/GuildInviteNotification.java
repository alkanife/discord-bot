package fr.alkanife.alkabot.notification.guild;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuildInviteNotification extends AbstractGuildNotification {

    public GuildInviteNotification(GuildNotification guildNotification) {
        super(guildNotification);
    }

    public void notifyCreate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isInvite_create())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyDelete(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isInvite_delete())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
