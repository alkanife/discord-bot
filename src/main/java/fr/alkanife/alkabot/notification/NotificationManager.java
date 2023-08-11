package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class NotificationManager {

    @Getter
    private final Alkabot alkabot;

    @Getter
    private final SelfNotification selfNotification;
    @Getter
    private final MessageNotification messageNotification;
    @Getter
    private final MemberNotification memberNotification;
    @Getter
    private final ModeratorNotification moderatorNotification;
    @Getter
    private final VoiceNotification voiceNotification;

    public NotificationManager(Alkabot alkabot) {
        this.alkabot = alkabot;

        selfNotification = new SelfNotification(this);
        messageNotification = new MessageNotification(this);
        memberNotification = new MemberNotification(this);
        moderatorNotification = new ModeratorNotification(this);
        voiceNotification = new VoiceNotification(this);
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
