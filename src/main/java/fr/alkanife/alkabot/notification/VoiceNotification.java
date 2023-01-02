package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsVoice;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.NotifUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

public class VoiceNotification extends AbstractNotification {

    private final JSONNotificationsVoice jsonNotificationsVoice;

    public VoiceNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.VOICE);
        jsonNotificationsVoice = Alkabot.getConfig().getNotifications().getVoice();
    }

    public void notifyJoin(GuildVoiceUpdateEvent event) {
        if (!jsonNotificationsVoice.isJoin())
            return;

        EmbedBuilder embedBuilder = genericEmbed(Alkabot.t("notification.voice.join.title"), event.getMember());
        embedBuilder.addField(Alkabot.t("notification.generic.channel"), NotifUtils.notifAudioChannel(event.getChannelJoined()), true);

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    public void notifyMove(GuildVoiceUpdateEvent event) {
        if (!jsonNotificationsVoice.isMove())
            return;

        EmbedBuilder embedBuilder = genericEmbed(Alkabot.t("notification.voice.move.title"), event.getMember());
        embedBuilder.addField(Alkabot.t("notification.voice.move.new_channel"), NotifUtils.notifAudioChannel(event.getChannelJoined()), true);
        embedBuilder.addField(Alkabot.t("notification.voice.move.old_channel"), NotifUtils.notifAudioChannel(event.getChannelLeft()), true);

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    public void notifyLeave(GuildVoiceUpdateEvent event) {
        if (!jsonNotificationsVoice.isLeave())
            return;

        EmbedBuilder embedBuilder = genericEmbed(Alkabot.t("notification.voice.left.title"), event.getMember());
        embedBuilder.addField(Alkabot.t("notification.generic.channel"), NotifUtils.notifAudioChannel(event.getChannelLeft()), true);

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    private EmbedBuilder genericEmbed(String title, Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = NotifUtils.addMemberAvatar(embedBuilder, member);
        embedBuilder.setColor(Colors.CYAN);

        embedBuilder.setTitle(title);
        embedBuilder.addField(Alkabot.t("notification.generic.member"), NotifUtils.notifMember(member), true);

        return embedBuilder;
    }

}
