package dev.alkanife.alkabot.discord.event;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.notification.NotificationUtils;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ModeratorListener extends ListenerAdapter {

    private final Alkabot alkabot;

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotificationUtils.getFirstLogEntry(auditLogEntries, ActionType.BAN, event.getUser().getId());

                if (logEntry == null)
                    alkabot.getNotificationManager().getModeratorNotification().notifyBan(event.getUser(), null, null);
                else
                    alkabot.getNotificationManager().getModeratorNotification().notifyBan(event.getUser(), logEntry.getUser(), logEntry.getReason());
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify ban", exception);
        }
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotificationUtils.getFirstLogEntry(auditLogEntries, ActionType.UNBAN, event.getUser().getId());

                if (logEntry == null)
                    alkabot.getNotificationManager().getModeratorNotification().notifyUnban(event.getUser(), null);
                else
                    alkabot.getNotificationManager().getModeratorNotification().notifyUnban(event.getUser(), logEntry.getUser());
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify unban", exception);
        }
    }

    @Override
    public void onGuildMemberUpdateTimeOut(@NotNull GuildMemberUpdateTimeOutEvent event) {
        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotificationUtils.getFirstLogEntry(auditLogEntries, ActionType.MEMBER_UPDATE, "communication_disabled_until", event.getUser().getId());

                if (logEntry == null)
                    return;

                if (event.getOldTimeOutEnd() != null && event.getNewTimeOutEnd() != null) {
                    alkabot.getNotificationManager().getModeratorNotification().notifyTimeoutChange(event.getMember(), logEntry.getUser(), event.getOldTimeOutEnd(), event.getNewTimeOutEnd());
                    return;
                }

                if (event.getOldTimeOutEnd() == null && event.getNewTimeOutEnd() != null) {
                    alkabot.getNotificationManager().getModeratorNotification().notifyTimeout(event.getMember(), logEntry.getUser(), logEntry.getReason(), event.getNewTimeOutEnd());
                    return;
                }

                if (event.getOldTimeOutEnd() != null && event.getNewTimeOutEnd() == null) {
                    alkabot.getNotificationManager().getModeratorNotification().notifyUntimeout(event.getMember(), logEntry.getUser());
                }
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify timeout update", exception);
        }
    }

    @Override
    public void onGuildVoiceGuildDeafen(@NotNull GuildVoiceGuildDeafenEvent event) {
        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotificationUtils.getFirstLogEntry(auditLogEntries, ActionType.MEMBER_UPDATE, "deaf", event.getMember().getId());

                User moderator = null;
                if (logEntry != null)
                    moderator = logEntry.getUser();

                if (event.isGuildDeafened())
                    alkabot.getNotificationManager().getModeratorNotification().notifyDeafenMember(event.getMember(), moderator);
                else
                    alkabot.getNotificationManager().getModeratorNotification().notifyUndeafenMember(event.getMember(), moderator);
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify deafen/undeafen", exception);
        }
    }

    @Override
    public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent event) {
        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotificationUtils.getFirstLogEntry(auditLogEntries, ActionType.MEMBER_UPDATE, "mute", event.getMember().getId());

                User moderator = null;
                if (logEntry != null)
                    moderator = logEntry.getUser();

                if (event.isGuildMuted())
                    alkabot.getNotificationManager().getModeratorNotification().notifyMuteMember(event.getMember(), moderator);
                else
                    alkabot.getNotificationManager().getModeratorNotification().notifyUnmuteMember(event.getMember(), moderator);
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify mute/unmute", exception);
        }
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) { // Experimental
        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = NotificationUtils.getFirstLogEntry(auditLogEntries, ActionType.MEMBER_UPDATE, "nick", event.getUser().getId());

                if (logEntry == null)
                    return;

                if (logEntry.getUser() == null)
                    return;

                if (logEntry.getUser().getId().equals(event.getMember().getId()))
                    return;

                alkabot.getNotificationManager().getModeratorNotification().notifyChangeMemberNickname(event.getMember(), logEntry.getUser(), event.getOldNickname(), event.getNewNickname());
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify nickname change", exception);
        }
    }

}
