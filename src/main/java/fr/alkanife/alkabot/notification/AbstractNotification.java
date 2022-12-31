package fr.alkanife.alkabot.notification;

public abstract class AbstractNotification {

    private final NotificationManager notificationManager;
    private final NotificationChannel notificationChannel;

    public AbstractNotification(NotificationManager notificationManager, NotificationChannel notificationChannel) {
        this.notificationManager = notificationManager;
        this.notificationChannel = notificationChannel;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }
}
