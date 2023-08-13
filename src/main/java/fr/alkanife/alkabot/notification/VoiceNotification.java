package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.configuration.json.notifications.VoiceNotifConfig;
import fr.alkanife.alkabot.util.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

public class VoiceNotification extends AbstractNotification {

    private final VoiceNotifConfig voiceNotifConfig;

    public VoiceNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.VOICE);
        voiceNotifConfig = notificationManager.getAlkabot().getConfig().getNotifConfig().getVoiceNotifConfig();
    }

    public void notifyJoin(GuildVoiceUpdateEvent event) {
        if (!voiceNotifConfig.isJoin())
            return;

        EmbedBuilder embedBuilder = genericEmbed(getNotificationManager().getAlkabot().t("notification.voice.join.title"), event.getMember());
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.channel"), notifAudioChannel(event.getChannelJoined()), true);

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    public void notifyMove(GuildVoiceUpdateEvent event) {
        if (!voiceNotifConfig.isMove())
            return;

        EmbedBuilder embedBuilder = genericEmbed(getNotificationManager().getAlkabot().t("notification.voice.move.title"), event.getMember());
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.voice.move.new_channel"), notifAudioChannel(event.getChannelJoined()), true);
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.voice.move.old_channel"), notifAudioChannel(event.getChannelLeft()), true);

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    public void notifyLeave(GuildVoiceUpdateEvent event) {
        if (!voiceNotifConfig.isLeave())
            return;

        EmbedBuilder embedBuilder = genericEmbed(getNotificationManager().getAlkabot().t("notification.voice.left.title"), event.getMember());
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.channel"), notifAudioChannel(event.getChannelLeft()), true);

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    private EmbedBuilder genericEmbed(String title, Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = addMemberAvatar(embedBuilder, member);
        embedBuilder.setColor(Colors.CYAN);

        embedBuilder.setTitle(title);
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.member"), notifMember(member), true);

        return embedBuilder;
    }

}
