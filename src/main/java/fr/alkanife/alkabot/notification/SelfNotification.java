package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsSelf;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SelfNotification extends AbstractNotification {

    private final JSONNotificationsSelf jsonNotificationsSelf;

    public SelfNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.SELF);
        jsonNotificationsSelf = Alkabot.getConfig().getNotifications().getSelf();
    }

    public void notifyAdmin(MessageEmbed messageEmbed) {
        if (!jsonNotificationsSelf.isAdmin())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyCommand(MessageEmbed messageEmbed) {
        if (!jsonNotificationsSelf.isCommands())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
