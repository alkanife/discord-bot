package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.notification.notifier.*;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class NotificationManager {

    @Getter
    private final Alkabot alkabot;

    @Getter
    private final SelfNotifier selfNotification;
    @Getter
    private final MessageNotifier messageNotification;
    @Getter
    private final MemberNotifier memberNotification;
    @Getter
    private final ModeratorNotifier moderatorNotification;
    @Getter
    private final VoiceNotifier voiceNotification;

    public NotificationManager(Alkabot alkabot) {
        this.alkabot = alkabot;

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
            alkabot.verbose("Successfully sent '" + notificationChannel.name() + "' notification titled '" + messageEmbed.getTitle() + "'");
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to send a notification titled '" + messageEmbed.getTitle() + "':");
            exception.printStackTrace();
        }
    }
}
