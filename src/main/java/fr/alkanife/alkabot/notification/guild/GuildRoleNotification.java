package fr.alkanife.alkabot.notification.guild;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuildRoleNotification extends AbstractGuildNotification {

    public GuildRoleNotification(GuildNotification guildNotification) {
        super(guildNotification);
    }

    public void notifyCreate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuildRole().isRole_create())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyDelete(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuildRole().isRole_delete())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyUpdate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuildRole().isRole_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyMemberRoleChange(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuildRole().isMember_role_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
