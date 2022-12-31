package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;

public enum NotificationChannel {

    SELF, MESSAGE, MEMBER, MODERATOR, VOICE, GUILD;

    public String getChannelID() {
        String channelID = "";
        switch (this) {
            case SELF -> channelID = Alkabot.getConfig().getNotifications().getSelf().getChannel_id();
            case MESSAGE -> channelID = Alkabot.getConfig().getNotifications().getMessage().getChannel_id();
            case MEMBER -> channelID = Alkabot.getConfig().getNotifications().getMember().getChannel_id();
            case MODERATOR -> channelID = Alkabot.getConfig().getNotifications().getModerator().getChannel_id();
            case VOICE -> channelID = Alkabot.getConfig().getNotifications().getVoice().getChannel_id();
            case GUILD -> channelID = Alkabot.getConfig().getNotifications().getGuild().getChannel_id();
        }
        return channelID;
    }
    
}
