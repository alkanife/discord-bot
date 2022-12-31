package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsMember;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MemberNotification extends AbstractNotification {

    private final JSONNotificationsMember jsonNotificationsMember;

    public MemberNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MEMBER);
        jsonNotificationsMember = Alkabot.getConfig().getNotifications().getMember();
    }

    public void notifyJoin(MessageEmbed messageEmbed) {
        if (!jsonNotificationsMember.isJoin())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyLeave(MessageEmbed messageEmbed) {
        if (!jsonNotificationsMember.isLeave())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}

