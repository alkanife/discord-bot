package fr.alkanife.alkabot.notification.notifier;

import fr.alkanife.alkabot.configuration.json.notifications.MemberNotifConfig;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.notification.NotificationChannel;
import fr.alkanife.alkabot.notification.NotificationManager;
import fr.alkanife.alkabot.notification.NotificationUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public class MemberNotifier extends Notifier {

    private final MemberNotifConfig jsonNotificationsMember;

    public MemberNotifier(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MEMBER);
        jsonNotificationsMember = alkabot.getConfig().getNotifConfig().getMemberNotifConfig();
    }

    public void notifyJoin(GuildMemberJoinEvent event, boolean failWelcome, boolean failAutorole) {
        if (!jsonNotificationsMember.isJoin())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.member.join.color").getColor());
        embed.setTitle(
                Lang.t("notification.member.join.title")
                        .parseUserNames(event.getUser())
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.member.join.icon")
                        .parseUserAvatars(event.getUser())
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(event.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createUserField("notification.member.join", event.getUser(), true));

        if (failAutorole || failWelcome) {
            StringBuilder stringBuilder = new StringBuilder();
            if (failWelcome)
                stringBuilder.append(Lang.t("notification.member.join.fail.welcome").getValue());
            if (failAutorole && failWelcome)
                stringBuilder.append("\n");
            if (failAutorole)
                stringBuilder.append(Lang.t("notification.member.join.fail.auto_role").getValue());
            embed.setDescription(stringBuilder.toString());
        }

        notificationManager.sendNotification(notificationChannel, embed.build());
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
        embed.addField(NotificationUtils.createUserField("notification.member.leave", event.getUser(), true));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }
}

