package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsMessage;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MessageNotification extends AbstractNotification {

    private final JSONNotificationsMessage jsonNotificationsMessage;

    public MessageNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MESSAGE);
        jsonNotificationsMessage = Alkabot.getConfig().getNotifications().getMessage();
    }

    public void notifyEdit(MessageEmbed messageEmbed) {
        if (!jsonNotificationsMessage.isEdit())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyDelete(MessageEmbed messageEmbed) {
        if (!jsonNotificationsMessage.isDelete())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }
}