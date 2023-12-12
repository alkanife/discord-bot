package dev.alkanife.alkabot.notification;

import dev.alkanife.alkabot.configuration.json.Configuration;

public enum NotificationChannel {

    SELF, MESSAGE, MEMBER, MODERATOR, VOICE;

    public String getChannelID(Configuration configuration) {
        String channelID = "";
        switch (this) {
            case SELF -> channelID = configuration.getNotifConfig().getSelfNotifConfig().getChannelId();
            case MESSAGE -> channelID = configuration.getNotifConfig().getMessageNotifConfig().getChannelId();
            case MEMBER -> channelID = configuration.getNotifConfig().getMemberNotifConfig().getChannelId();
            case MODERATOR -> channelID = configuration.getNotifConfig().getModNotifConfig().getChannelId();
            case VOICE -> channelID = configuration.getNotifConfig().getVoiceNotifConfig().getChannelId();
        }
        return channelID;
    }
    
}
