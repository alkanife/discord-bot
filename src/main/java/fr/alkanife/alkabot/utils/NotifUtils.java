package fr.alkanife.alkabot.utils;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;
import java.util.List;

public class NotifUtils {

    public static String notifMember(Member member) {
        return notifUser(member.getUser());
    }

    public static String notifUser(User user) {
        if (user == null)
            return Alkabot.t("notification.generic.unknown");

        return user.getName() + " (" + user.getAsMention() + ")";
    }

    public static String notifRole(Role role) {
        if (role == null)
            return Alkabot.t("notification.generic.unknown");

        return role.getName() + " (" + role.getAsMention() + ")";
    }

    public static String notifGuildChannel(GuildChannelUnion guildChannelUnion) {
        if (guildChannelUnion == null)
            return Alkabot.t("notification.generic.unknown");

        return guildChannelUnion.getName() + " (" + guildChannelUnion.getAsMention() + ")";
    }

    public static String notifAudioChannel(AudioChannelUnion audioChannelUnion) {
        if (audioChannelUnion == null)
            return Alkabot.t("notification.generic.unknown");

        return audioChannelUnion.getName() + " (" + audioChannelUnion.getAsMention() + ")";
    }

    public static String notifChannel(MessageChannelUnion messageChannelUnion) {
        if (messageChannelUnion == null)
            return Alkabot.t("notification.generic.unknown");

        return messageChannelUnion.getName() + " (" + messageChannelUnion.getAsMention() + ")";
    }

    public static EmbedBuilder addMemberAvatar(EmbedBuilder embedBuilder, Member member) {
        return addUserAvatar(embedBuilder, member.getUser());
    }

    public static EmbedBuilder addUserAvatar(EmbedBuilder embedBuilder, User user) {
        if (user != null)
            embedBuilder.setThumbnail(user.getEffectiveAvatarUrl());

        return embedBuilder;
    }

    public static String notifValue(String r) {
        if (StringUtils.isNull(r))
            return Alkabot.t("notification.generic.none");

        return r;
    }

    public static AuditLogEntry getFirst(List<AuditLogEntry> auditLogEntryList, ActionType actionType, String targetID) {
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

    public static AuditLogEntry getFirst(List<AuditLogEntry> auditLogEntryList, ActionType actionType, String change, String targetID) {
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

    public static EmbedBuilder genericVoiceEmbed(Color color, String title, Member member, AuditLogEntry auditLogEntry) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = NotifUtils.addMemberAvatar(embedBuilder, member);
        embedBuilder.setColor(color);

        embedBuilder.setTitle(title);
        embedBuilder.addField(Alkabot.t("notification.generic.member"), NotifUtils.notifMember(member), true);

        String admin = Alkabot.t("notification.generic.unknown");
        if (auditLogEntry != null)
            admin = NotifUtils.notifUser(auditLogEntry.getUser());

        embedBuilder.addField(Alkabot.t("notification.generic.moderator"), admin, true);

        GuildVoiceState guildVoiceState = member.getVoiceState();
        if (guildVoiceState != null)
            embedBuilder.addField(Alkabot.t("notification.generic.channel"), NotifUtils.notifAudioChannel(guildVoiceState.getChannel()), false);

        return embedBuilder;
    }

    public static EmbedBuilder genericValueEmbed(Color color, String title, Member member, AuditLogEntry auditLogEntry, String oldValue, String newValue) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = NotifUtils.addMemberAvatar(embedBuilder, member);
        embedBuilder.setColor(color);

        embedBuilder.setTitle(title);
        embedBuilder.addField(Alkabot.t("notification.generic.member"), NotifUtils.notifMember(member), true);

        String admin = Alkabot.t("notification.generic.unknown");
        if (auditLogEntry != null)
            admin = NotifUtils.notifUser(auditLogEntry.getUser());

        embedBuilder.addField(Alkabot.t("notification.generic.moderator"), admin, true);
        embedBuilder.addField(Alkabot.t("notification.generic.old_value"), NotifUtils.notifValue(oldValue), false);
        embedBuilder.addField(Alkabot.t("notification.generic.new_value"), NotifUtils.notifValue(newValue), true);

        return embedBuilder;
    }

}
