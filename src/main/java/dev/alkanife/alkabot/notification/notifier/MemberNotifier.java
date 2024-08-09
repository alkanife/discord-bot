package dev.alkanife.alkabot.notification.notifier;

import dev.alkanife.alkabot.configuration.json.notifications.MemberNotifConfig;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.notification.NotificationChannel;
import dev.alkanife.alkabot.notification.NotificationManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class MemberNotifier extends Notifier {

    private final MemberNotifConfig jsonNotificationsMember;

    public MemberNotifier(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MEMBER);
        jsonNotificationsMember = alkabot.getConfig().getNotifConfig().getMemberNotifConfig();
    }

    public void notifyJoin(GuildMemberJoinEvent event, boolean welcome, boolean autorole) {
        if (!jsonNotificationsMember.isJoin())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.member.join.color").getColor());
        embed.setTitle(
                Lang.t("notification.member.join.title")
                        .parseMemberAvatars(event.getMember())
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.member.join.icon")
                        .parseMemberAvatars(event.getMember())
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(event.getGuild())
                        .getImage()
        );

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(
                Lang.t("notification.member.join.member")
                        .parseMemberId(event.getMember())
                        .parseMemberNames(event.getMember())
                        .parseMemberMention(event.getMember())
                        .getValue()
        );

        if (!autorole || !welcome) {
            stringBuilder.append("\n");
            if (!welcome)
                stringBuilder.append(Lang.t("notification.member.join.fail.welcome").getValue());
            if (!autorole && !welcome)
                stringBuilder.append("\n");
            if (!autorole)
                stringBuilder.append(Lang.t("notification.member.join.fail.auto_role").getValue());
        }

        embed.setDescription(stringBuilder.toString());

        notificationManager.sendNotification(notificationChannel, new MessageCreateBuilder().addEmbeds(embed.build()).build());
    }

    public void notifyLeave(GuildMemberRemoveEvent event) {
        if (!jsonNotificationsMember.isLeave())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.member.leave.color").getColor());
        embed.setTitle(
                Lang.t("notification.member.leave.title")
                        .parseUserNames(event.getUser())
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.member.leave.icon")
                        .parseUserAvatars(event.getUser())
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(event.getGuild())
                        .getImage()
        );
        embed.setDescription(
                Lang.t("notification.member.leave.user")
                        .parseUserId(event.getUser())
                        .parseUserNames(event.getUser())
                        .parseUserMention(event.getUser())
                        .getValue()
        );

        notificationManager.sendNotification(notificationChannel, new MessageCreateBuilder().addEmbeds(embed.build()).build());
    }
}

