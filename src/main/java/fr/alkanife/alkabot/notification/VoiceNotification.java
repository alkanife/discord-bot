package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsVoice;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class VoiceNotification extends AbstractNotification {

    private final JSONNotificationsVoice jsonNotificationsVoice;

    public VoiceNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MEMBER);
        jsonNotificationsVoice = Alkabot.getConfig().getNotifications().getVoice();
    }

    public void notifyJoin(MessageEmbed messageEmbed) {
        if (!jsonNotificationsVoice.isJoin())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyMove(MessageEmbed messageEmbed) {
        if (!jsonNotificationsVoice.isMove())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyLeave(MessageEmbed messageEmbed) {
        if (!jsonNotificationsVoice.isLeave())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
