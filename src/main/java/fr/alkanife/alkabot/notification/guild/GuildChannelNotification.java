package fr.alkanife.alkabot.notification.guild;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuildChannelNotification extends AbstractGuildNotification {

    public GuildChannelNotification(GuildNotification guildNotification) {
        super(guildNotification);
    }

    public void notifyCreate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isChannel_create())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyDelete(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isChannel_delete())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyUpdate(MessageEmbed messageEmbed) {
        if (!getJsonNotificationsGuild().isChannel_update())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
