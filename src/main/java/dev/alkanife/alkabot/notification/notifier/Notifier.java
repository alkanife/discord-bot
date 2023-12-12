package dev.alkanife.alkabot.notification.notifier;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.notification.NotificationChannel;
import dev.alkanife.alkabot.notification.NotificationManager;

public abstract class Notifier {

    public Alkabot alkabot;
    public final NotificationManager notificationManager;
    public final NotificationChannel notificationChannel;

    public Notifier(NotificationManager notificationManager, NotificationChannel notificationChannel) {
        this.alkabot = notificationManager.getAlkabot();
        this.notificationManager = notificationManager;
        this.notificationChannel = notificationChannel;
    }
}
