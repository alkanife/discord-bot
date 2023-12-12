package dev.alkanife.alkabot.notification.notifier;

import dev.alkanife.alkabot.configuration.json.notifications.VoiceNotifConfig;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.notification.NotificationChannel;
import dev.alkanife.alkabot.notification.NotificationManager;
import dev.alkanife.alkabot.notification.NotificationUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

public class VoiceNotifier extends Notifier {

    private final VoiceNotifConfig voiceNotifConfig;

    public VoiceNotifier(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.VOICE);
        voiceNotifConfig = notificationManager.getAlkabot().getConfig().getNotifConfig().getVoiceNotifConfig();
    }

    public void notifyJoin(Member member, AudioChannelUnion channel) {
        if (!voiceNotifConfig.isJoin())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.voice.join.color").getColor());
        embed.setTitle(
                Lang.t("notification.voice.join.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.voice.join.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.voice.join", member, true));
        embed.addField(NotificationUtils.createChannelField("notification.voice.join", channel, true));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyMove(Member member, AudioChannelUnion channelJoined, AudioChannelUnion channelLeft) {
        if (!voiceNotifConfig.isMove())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.voice.move.color").getColor());
        embed.setTitle(
                Lang.t("notification.voice.move.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.voice.move.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.voice.move", member, false));
        embed.addField(NotificationUtils.createChannelField("notification.voice.move", "channel_joined", channelJoined, true));
        embed.addField(NotificationUtils.createChannelField("notification.voice.move", "channel_left", channelLeft, true));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyLeave(Member member, AudioChannelUnion channel) {
        if (!voiceNotifConfig.isLeave())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.voice.left.color").getColor());
        embed.setTitle(
                Lang.t("notification.voice.left.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.voice.left.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.voice.left", member, true));
        embed.addField(NotificationUtils.createChannelField("notification.voice.left", channel, true));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }
}
