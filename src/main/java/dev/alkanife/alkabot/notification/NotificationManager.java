package dev.alkanife.alkabot.notification;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.notification.notifier.*;
import dev.alkanife.alkabot.util.StringUtils;
import dev.alkanife.alkabot.util.timetracker.TimeTracker;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.UUID;

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

    public void sendNotification(NotificationChannel notificationChannel, MessageCreateData messageCreateData) {
        TextChannel textChannel = alkabot.getJda().getTextChannelById(notificationChannel.getChannelID(alkabot.getConfig()));

        String notificationName;

        if (messageCreateData.getEmbeds().isEmpty()) {
            notificationName = StringUtils.limitString(messageCreateData.getContent(), 30);
        } else {
            MessageEmbed embed = messageCreateData.getEmbeds().get(0);

            if (embed.getTitle() == null) {
                if (embed.getDescription() == null) {
                    notificationName = "Unknown notification";
                } else {
                    notificationName = StringUtils.limitString(embed.getDescription(), 30);
                }
            } else {
                notificationName = StringUtils.limitString(embed.getTitle(), 30);
            }
        }

        if (textChannel == null) {
            alkabot.getLogger().warn("Failed to send a notification titled '{}' because the text channel was not found", notificationName);
            return;
        }

        String tracking = TimeTracker.startUnique("send-notification");

        try {
            textChannel.sendMessage(messageCreateData).queue();
            TimeTracker.end(tracking);
            alkabot.getLogger().debug("Successfully sent '{}' notification titled '{}'", notificationChannel.name(), notificationName);
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to send a notification titled '{}':", notificationName, exception);
            TimeTracker.kill(tracking);
        }
    }
}
