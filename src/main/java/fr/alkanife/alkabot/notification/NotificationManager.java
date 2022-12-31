package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.notification.guild.GuildNotification;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class NotificationManager {

    private final SelfNotification selfNotification;
    private final MessageNotification messageNotification;
    private final MemberNotification memberNotification;
    private final ModeratorNotification moderatorNotification;
    private final VoiceNotification voiceNotification;
    private final GuildNotification guildNotification;

    public NotificationManager() {
        selfNotification = new SelfNotification(this);
        messageNotification = new MessageNotification(this);
        memberNotification = new MemberNotification(this);
        moderatorNotification = new ModeratorNotification(this);
        voiceNotification = new VoiceNotification(this);
        guildNotification = new GuildNotification(this);
    }

    public SelfNotification getSelfNotification() {
        return selfNotification;
    }

    public MessageNotification getMessageNotification() {
        return messageNotification;
    }

    public MemberNotification getMemberNotification() {
        return memberNotification;
    }

    public ModeratorNotification getModeratorNotification() {
        return moderatorNotification;
    }

    public VoiceNotification getVoiceNotification() {
        return voiceNotification;
    }

    public GuildNotification getGuildNotification() {
        return guildNotification;
    }

    public void sendNotification(NotificationChannel notificationChannel, MessageEmbed messageEmbed) {
        TextChannel textChannel = Alkabot.getJda().getTextChannelById(notificationChannel.getChannelID());

        if (textChannel == null) {
            Alkabot.getLogger().warn("Failed to send a notification titled '" + messageEmbed.getTitle() + "' because the text channel was not found");
            return;
        }

        try {
            textChannel.sendMessageEmbeds(messageEmbed).queue();
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to send a notification titled '" + messageEmbed.getTitle() + "':");
            exception.printStackTrace();
        }
    }
}
