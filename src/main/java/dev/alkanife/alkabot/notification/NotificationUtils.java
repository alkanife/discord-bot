package dev.alkanife.alkabot.notification;

import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.List;

public class NotificationUtils {

    public static MessageEmbed.Field createChannelField(String root, Channel channel, boolean inline) {
        return new MessageEmbed.Field(
                Lang.t(root + ".channel.title").getValue(),
                Lang.t(root + ".channel.field")
                        .parseChannel(channel)
                        .getValue(),
                inline);
    }

    public static MessageEmbed.Field createChannelField(String root, String field, Channel channel, boolean inline) {
        return new MessageEmbed.Field(
                Lang.t(root + "." + field + ".title").getValue(),
                Lang.t(root + "." + field + ".field")
                        .parseChannel(channel)
                        .getValue(),
                inline);
    }

    public static MessageEmbed.Field createMemberField(String root, Member member, boolean inline) {
        return new MessageEmbed.Field(
                Lang.t(root + ".member.title").getValue(),
                Lang.t(root + ".member.field")
                        .parseMemberId(member)
                        .parseMemberNames(member)
                        .parseMemberMention(member)
                        .getValue(),
                inline);
    }

    public static MessageEmbed.Field createUserField(String root, User user, boolean inline) {
        return new MessageEmbed.Field(
                Lang.t(root + ".user.title").getValue(),
                Lang.t(root + ".user.field")
                        .parseUserId(user)
                        .parseUserNames(user)
                        .parseUserMention(user)
                        .getValue(),
                inline);
    }

    public static MessageEmbed.Field createModeratorField(String root, User user, boolean inline) {
        return new MessageEmbed.Field(
                Lang.t(root + ".moderator.title").getValue(),
                Lang.t(root + ".moderator.field")
                        .parseModId(user)
                        .parseModNames(user)
                        .parseModMention(user)
                        .getValue(),
                inline);
    }

    public static MessageEmbed.Field createReasonField(String root, String reason) {
        if (reason == null)
            reason = Lang.t("notification.generic.none").getValue();

        return new MessageEmbed.Field(
                Lang.t(root + ".reason").getValue(),
                reason,
                false);
    }

    public static AuditLogEntry getFirstLogEntry(List<AuditLogEntry> auditLogEntryList, ActionType actionType, String change, String targetID) {
        for (int i = 0; i < 5; i++) {
            AuditLogEntry auditLogEntry = auditLogEntryList.get(i);

            if (!auditLogEntry.getType().equals(actionType))
                continue;

            if (!auditLogEntry.getTargetId().equalsIgnoreCase(targetID))
                continue;

            for (AuditLogChange auditLogChange : auditLogEntry.getChanges().values())
                if (auditLogChange.getKey().equalsIgnoreCase(change))
                    return auditLogEntry;
        }

        return null;
    }

    public static AuditLogEntry getFirstLogEntry(List<AuditLogEntry> auditLogEntryList, ActionType actionType, String targetID) {
        for (int i = 0; i < 5; i++) {
            AuditLogEntry auditLogEntry = auditLogEntryList.get(i);

            if (!auditLogEntry.getType().equals(actionType))
                continue;

            if (!auditLogEntry.getTargetId().equalsIgnoreCase(targetID))
                continue;

            return auditLogEntry;
        }

        return null;
    }

}
