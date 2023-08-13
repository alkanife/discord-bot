package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.configuration.json.notifications.ModNotifConfig;
import fr.alkanife.alkabot.util.Colors;
import fr.alkanife.alkabot.util.StringUtils;
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
        jsonNotificationsModerator = getNotificationManager().getAlkabot().getConfig().getNotifConfig().getModNotifConfig();
    }

    public void notifyBan(GuildBanEvent event) {
        if (!jsonNotificationsModerator.isBan())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.BAN, event.getUser().getId());

                User target = event.getUser();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder = addUserAvatar(embedBuilder, target);
                embedBuilder.setColor(Colors.RED);

                embedBuilder.setTitle(getNotificationManager().getAlkabot().t("notification.moderator.ban.title"));
                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.user"), notifUser(target), true);

                String admin = getNotificationManager().getAlkabot().t("notification.generic.unknown");
                String reason = getNotificationManager().getAlkabot().t("notification.generic.unknown");
                if (logEntry != null) {
                    admin = notifUser(logEntry.getUser());
                    reason = notifValue(logEntry.getReason());
                }

                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.moderator"), admin, true);
                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.reason"), reason, false);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });

        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify ban");
            exception.printStackTrace();
        }
    }

    public void notifyUnban(GuildUnbanEvent event) {
        if (!jsonNotificationsModerator.isUnban())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.UNBAN, event.getUser().getId());

                User target = event.getUser();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder = addUserAvatar(embedBuilder, target);
                embedBuilder.setColor(Colors.CYAN);

                embedBuilder.setTitle(getNotificationManager().getAlkabot().t("notification.moderator.unban.title"));
                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.user"), notifUser(target), true);

                String admin = getNotificationManager().getAlkabot().t("notification.generic.unknown");
                if (logEntry != null)
                    admin = notifUser(logEntry.getUser());


                embedBuilder.addField(getNotificationManager().getAlkabot().t("notification.generic.moderator"), admin, true);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify unban");
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
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "communication_disabled_until", event.getUser().getId());

                EmbedBuilder embedBuilder = genericValueEmbed(Colors.CYAN,
                        getNotificationManager().getAlkabot().t("notification.moderator.timeout.title"),
                        event.getMember(),
                        logEntry,
                        notifValue(StringUtils.offsetToString(event.getOldTimeOutEnd())),
                        notifValue(StringUtils.offsetToString(event.getNewTimeOutEnd())));

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify timeout");
            exception.printStackTrace();
        }
    }

    public void notifyDeafenMember(GuildVoiceGuildDeafenEvent event) { // Experimental
        if (!jsonNotificationsModerator.isDeafenMember())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "deaf", event.getMember().getId());

                EmbedBuilder embedBuilder = genericVoiceEmbed(Colors.RED, getNotificationManager().getAlkabot().t("notification.moderator.deafen.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify deafen");
            exception.printStackTrace();
        }
    }

    public void notifyUndeafenMember(GuildVoiceGuildDeafenEvent event) { // Experimental
        if (!jsonNotificationsModerator.isUndeafenMember())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "deaf", event.getMember().getId());

                EmbedBuilder embedBuilder = genericVoiceEmbed(Colors.CYAN, getNotificationManager().getAlkabot().t("notification.moderator.undeafen.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify undeafen");
            exception.printStackTrace();
        }
    }

    public void notifyMuteMember(GuildVoiceGuildMuteEvent event) { // Experimental
        if (!jsonNotificationsModerator.isMuteMember())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "mute", event.getMember().getId());

                EmbedBuilder embedBuilder = genericVoiceEmbed(Colors.RED, getNotificationManager().getAlkabot().t("notification.moderator.mute.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify mute");
            exception.printStackTrace();
        }
    }

    public void notifyUnmuteMember(GuildVoiceGuildMuteEvent event) { // Experimental
        if (!jsonNotificationsModerator.isUnmuteMember())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "mute", event.getMember().getId());

                EmbedBuilder embedBuilder = genericVoiceEmbed(Colors.CYAN, getNotificationManager().getAlkabot().t("notification.moderator.unmute.title"), event.getMember(), logEntry);

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify unmute");
            exception.printStackTrace();
        }
    }

    public void notifyChangeMemberNickname(GuildMemberUpdateNicknameEvent event) { // Experimental
        if (!jsonNotificationsModerator.isChangeMemberNickname())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry logEntry = getFirst(auditLogEntries, ActionType.MEMBER_UPDATE, "nick", event.getUser().getId());

                if (logEntry == null)
                    return;

                if (logEntry.getUser() == null)
                    return;

                if (logEntry.getUser().getId().equals(event.getMember().getId()))
                    return;

                EmbedBuilder embedBuilder = genericValueEmbed(Colors.CYAN,
                        getNotificationManager().getAlkabot().t("notification.moderator.nickname.title"),
                        event.getMember(),
                        logEntry,
                        notifValue(event.getOldNickname()),
                        notifValue(event.getNewNickname()));

                getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to notify timeout");
            exception.printStackTrace();
        }
    }

}
