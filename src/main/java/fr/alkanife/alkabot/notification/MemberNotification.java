package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.configuration.json.notifications.MemberNotifConfig;
import fr.alkanife.alkabot.util.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public class MemberNotification extends AbstractNotification {

    private final MemberNotifConfig jsonNotificationsMember;

    public MemberNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MEMBER);
        jsonNotificationsMember = getNotificationManager().getAlkabot().getConfig().getNotifConfig().getMemberNotifConfig();
    }

    public void notifyJoin(GuildMemberJoinEvent event, boolean failWelcome, boolean failAutorole) {
        if (!jsonNotificationsMember.isJoin())
            return;

        User user = event.getMember().getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = addUserAvatar(embedBuilder, user);
        embedBuilder.setTitle(getNotificationManager().getAlkabot().t("notification.member.join.title"));
        embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.member"), notifUser(user), true);

        if (failAutorole || failWelcome) {
            embedBuilder.setColor(Colors.ORANGE);

            StringBuilder stringBuilder = new StringBuilder();
            if (failWelcome)
                stringBuilder.append(getNotificationManager().getAlkabot().t("notification.member.join.fail.welcome"));
            if (failAutorole)
                stringBuilder.append("\n").append(getNotificationManager().getAlkabot().t("notification.member.join.fail.autorole"));
            embedBuilder.setDescription(stringBuilder.toString());
        } else {
            embedBuilder.setColor(Colors.GREEN);
        }

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    public void notifyLeaveOrKick(GuildMemberRemoveEvent guildMemberRemoveEvent) {
        User user = guildMemberRemoveEvent.getUser();
        guildMemberRemoveEvent.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
            AuditLogEntry latest = auditLogEntries.get(0);
            boolean kick = false;

            if (latest.getType().equals(ActionType.KICK))
                if (latest.getTargetId().equalsIgnoreCase(user.getId()))
                    kick = true;

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder = addUserAvatar(embedBuilder, user);
            embedBuilder.setColor(Colors.RED);

            if (kick && getNotificationManager().getAlkabot().getConfig().getNotifConfig().getModNotifConfig().isKick()) {
                embedBuilder.setTitle(getNotificationManager().getAlkabot().t("notification.moderator.kick.title"));
                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.member"), user.getName() + " (" + user.getAsMention() + ")", true);
                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.moderator"), notifUser(latest.getUser()), true);
                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.reason"), notifValue(latest.getReason()), false);

                getNotificationManager().getAlkabot().getNotificationManager().getModeratorNotification().notifyKick(embedBuilder.build());
            } else {
                embedBuilder.setTitle(getNotificationManager().getAlkabot().t("notification.member.leave.title"));
                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.member"), notifUser(user), false);

                notifyLeave(embedBuilder.build());
            }
        });
    }

    public void notifyLeave(MessageEmbed messageEmbed) {
        if (!jsonNotificationsMember.isLeave())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}

