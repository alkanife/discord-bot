package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsModerator;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ModeratorNotification extends AbstractNotification {

    private final JSONNotificationsModerator jsonNotificationsModerator;

    public ModeratorNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MODERATOR);
        jsonNotificationsModerator = Alkabot.getConfig().getNotifications().getModerator();
    }

    public void notifyBan(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isBan())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyUnban(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isUnban())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyKick(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isKick())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyTimeout(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isTimeout())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyDeafenMember(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isDeafen_member())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyUndeafenMember(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isUndeafen_member())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyMuteMember(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isMute_member())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyUnmuteMember(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isUnmute_member())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyChangeMemberNickname(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isUnmute_member())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}
