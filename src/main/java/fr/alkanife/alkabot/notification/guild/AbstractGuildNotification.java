package fr.alkanife.alkabot.notification.guild;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsGuild;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsGuildRole;
import fr.alkanife.alkabot.notification.NotificationChannel;
import fr.alkanife.alkabot.notification.NotificationManager;

public abstract class AbstractGuildNotification {

    private final NotificationManager notificationManager;
    private final GuildNotification guildNotification;

    public AbstractGuildNotification(GuildNotification guildNotification) {
        this.notificationManager = guildNotification.getNotificationManager();
        this.guildNotification = guildNotification;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public GuildNotification getGuildNotification() {
        return guildNotification;
    }

    public NotificationChannel getNotificationChannel() {
        return NotificationChannel.GUILD;
    }

    public JSONNotificationsGuild getJsonNotificationsGuild() {
        return Alkabot.getConfig().getNotifications().getGuild();
    }

    public JSONNotificationsGuildRole getJsonNotificationsGuildRole() {
        return Alkabot.getConfig().getNotifications().getGuild().getRole();
    }
}
