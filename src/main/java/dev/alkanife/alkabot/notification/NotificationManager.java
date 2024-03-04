package dev.alkanife.alkabot.notification;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.notification.notifier.*;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Getter
public class NotificationManager {

    private final Alkabot alkabot;

    private SelfNotifier selfNotification;
    private MessageNotifier messageNotification;
    private MemberNotifier memberNotification;
    private ModeratorNotifier moderatorNotification;
    private VoiceNotifier voiceNotification;

    public NotificationManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public void load() {
        selfNotification = new SelfNotifier(this);
        messageNotification = new MessageNotifier(this);
        memberNotification = new MemberNotifier(this);
        moderatorNotification = new ModeratorNotifier(this);
        voiceNotification = new VoiceNotifier(this);
    }

    public void sendNotification(NotificationChannel notificationChannel, MessageEmbed messageEmbed) {
        TextChannel textChannel = alkabot.getJda().getTextChannelById(notificationChannel.getChannelID(alkabot.getConfig()));

        if (textChannel == null) {
            alkabot.getLogger().warn("Failed to send a notification titled '" + messageEmbed.getTitle() + "' because the text channel was not found");
            return;
        }

        try {
            textChannel.sendMessageEmbeds(messageEmbed).queue();
            alkabot.getLogger().debug("Successfully sent '" + notificationChannel.name() + "' notification titled '" + messageEmbed.getTitle() + "'");
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to send a notification titled '" + messageEmbed.getTitle() + "':", exception);
        }
    }
}
