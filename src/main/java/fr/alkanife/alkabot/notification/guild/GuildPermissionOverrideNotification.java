package fr.alkanife.alkabot.notification.guild;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuildPermissionOverrideNotification extends AbstractGuildNotification {

    public GuildPermissionOverrideNotification(GuildNotification guildNotification) {
        super(guildNotification);
    }

    public void notifyCreate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isPermission_override_create())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyDelete(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isPermission_override_delete())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyUpdate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isPermission_override_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
