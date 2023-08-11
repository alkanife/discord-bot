package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.ModNotifConfig;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.NotifUtils;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;

public class ModeratorNotification extends AbstractNotification {

    private final ModNotifConfig jsonNotificationsModerator;

    public ModeratorNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MODERATOR);
        jsonNotificationsModerator = Alkabot.getConfig().getNotifications().getModerator();
    }

    public void notifyBan(GuildBanEvent event) {
        if (!jsonNotificationsModerator.isBan())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.BAN, event.getUser().getId());

                User target = event.getUser();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder = NotifUtils.addUserAvatar(embedBuilder, target);
                embedBuilder.setColor(Colors.RED);

                embedBuilder.setTitle(Alkabot.t("notification.moderator.ban.title"));
                embedBuilder.addField(Alkabot.t("notification.generic.user"), NotifUtils.notifUser(target), true);

                String admin = Alkabot.t("notification.generic.unknown");
                String reason = Alkabot.t("notification.generic.unknown");
                if (logEntry != null) {
                    admin = NotifUtils.notifUser(logEntry.getUser());
                    reason = NotifUtils.notifValue(logEntry.getReason());
                }

                embedBuilder.addField(Alkabot.t("notification.generic.moderator"), admin, true);
                embedBuilder.addField(Alkabot.t("notification.generic.reason"), reason, false);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });

        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify ban");
            exception.printStackTrace();
        }
    }

    public void notifyUnban(GuildUnbanEvent event) {
        if (!jsonNotificationsModerator.isUnban())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.UNBAN, event.getUser().getId());

                User target = event.getUser();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder = NotifUtils.addUserAvatar(embedBuilder, target);
                embedBuilder.setColor(Colors.CYAN);

                embedBuilder.setTitle(Alkabot.t("notification.moderator.unban.title"));
                embedBuilder.addField(Alkabot.t("notification.generic.user"), NotifUtils.notifUser(target), true);

                String admin = Alkabot.t("notification.generic.unknown");
                if (logEntry != null)
                    admin = NotifUtils.notifUser(logEntry.getUser());


                embedBuilder.addField(Alkabot.t("notification.generic.moderator"), admin, true);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify unban");
            exception.printStackTrace();
        }
    }

    public void notifyKick(MessageEmbed messageEmbed) {
        if (!jsonNotificationsModerator.isKick())
            return;

        // In MemberListener

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyTimeout(GuildMemberUpdateTimeOutEvent event) { // VERY experimental
        if (!jsonNotificationsModerator.isTimeout())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "communication_disabled_until", event.getUser().getId());

                EmbedBuilder embedBuilder = NotifUtils.genericValueEmbed(Colors.CYAN,
                        Alkabot.t("notification.moderator.timeout.title"),
                        event.getMember(),
                        logEntry,
                        NotifUtils.notifValue(StringUtils.offsetToString(event.getOldTimeOutEnd())),
                        NotifUtils.notifValue(StringUtils.offsetToString(event.getNewTimeOutEnd())));

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify timeout");
            exception.printStackTrace();
        }
    }

    public void notifyDeafenMember(GuildVoiceGuildDeafenEvent event) { // Experimental
        if (!jsonNotificationsModerator.isDeafen_member())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "deaf", event.getMember().getId());

                EmbedBuilder embedBuilder = NotifUtils.genericVoiceEmbed(Colors.RED, Alkabot.t("notification.moderator.deafen.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify deafen");
            exception.printStackTrace();
        }
    }

    public void notifyUndeafenMember(GuildVoiceGuildDeafenEvent event) { // Experimental
        if (!jsonNotificationsModerator.isUndeafen_member())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "deaf", event.getMember().getId());

                EmbedBuilder embedBuilder = NotifUtils.genericVoiceEmbed(Colors.CYAN, Alkabot.t("notification.moderator.undeafen.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify undeafen");
            exception.printStackTrace();
        }
    }

    public void notifyMuteMember(GuildVoiceGuildMuteEvent event) { // Experimental
        if (!jsonNotificationsModerator.isMute_member())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "mute", event.getMember().getId());

                EmbedBuilder embedBuilder = NotifUtils.genericVoiceEmbed(Colors.RED, Alkabot.t("notification.moderator.mute.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify mute");
            exception.printStackTrace();
        }
    }

    public void notifyUnmuteMember(GuildVoiceGuildMuteEvent event) { // Experimental
        if (!jsonNotificationsModerator.isUnmute_member())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "mute", event.getMember().getId());

                EmbedBuilder embedBuilder = NotifUtils.genericVoiceEmbed(Colors.CYAN, Alkabot.t("notification.moderator.unmute.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify unmute");
            exception.printStackTrace();
        }
    }

    public void notifyChangeMemberNickname(GuildMemberUpdateNicknameEvent event) { // Experimental
        if (!jsonNotificationsModerator.isChange_member_nickname())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotifUtils.getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "nick", event.getUser().getId());

                if (logEntry == null)
                    return;

                if (logEntry.getUser() == null)
                    return;

                if (logEntry.getUser().getId().equals(event.getMember().getId()))
                    return;

                EmbedBuilder embedBuilder = NotifUtils.genericValueEmbed(Colors.CYAN,
                        Alkabot.t("notification.moderator.nickname.title"),
                        event.getMember(),
                        logEntry,
                        NotifUtils.notifValue(event.getOldNickname()),
                        NotifUtils.notifValue(event.getNewNickname()));

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify timeout");
            exception.printStackTrace();
        }
    }

}
