package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

public abstract class AbstractNotification {

    public Alkabot alkabot;
    public final NotificationManager notificationManager;
    public final NotificationChannel notificationChannel;

    public AbstractNotification(NotificationManager notificationManager, NotificationChannel notificationChannel) {
        this.alkabot = notificationManager.getAlkabot();
        this.notificationManager = notificationManager;
        this.notificationChannel = notificationChannel;
    }

    /*public String notifMember(Member member) {
        return notifUser(member.getUser());
    }

    public String notifUser(User user) {
        if (user == null)
            return notificationManager.getAlkabot().t("notification.generic.unknown");

        return user.getName() + " (" + user.getAsMention() + ")";
    }

    public String notifRole(Role role) {
        if (role == null)
            return notificationManager.getAlkabot().t("notification.generic.unknown");

        return role.getName() + " (" + role.getAsMention() + ")";
    }

    public String notifGuildChannel(GuildChannelUnion guildChannelUnion) {
        if (guildChannelUnion == null)
            return notificationManager.getAlkabot().t("notification.generic.unknown");

        return guildChannelUnion.getName() + " (" + guildChannelUnion.getAsMention() + ")";
    }

    public String notifAudioChannel(AudioChannelUnion audioChannelUnion) {
        if (audioChannelUnion == null)
            return notificationManager.getAlkabot().t("notification.generic.unknown");

        return audioChannelUnion.getName() + " (" + audioChannelUnion.getAsMention() + ")";
    }

    public String notifChannel(MessageChannelUnion messageChannelUnion) {
        if (messageChannelUnion == null)
            return notificationManager.getAlkabot().t("notification.generic.unknown");

        return messageChannelUnion.getName() + " (" + messageChannelUnion.getAsMention() + ")";
    }

    public EmbedBuilder addMemberAvatar(EmbedBuilder embedBuilder, Member member) {
        return addUserAvatar(embedBuilder, member.getUser());
    }

    public EmbedBuilder addUserAvatar(EmbedBuilder embedBuilder, User user) {
        if (user != null)
            embedBuilder.setThumbnail(user.getEffectiveAvatarUrl());

        return embedBuilder;
    }

    public String notifValue(String r) {
        if (r == null)
            return notificationManager.getAlkabot().t("notification.generic.none");

        return r;
    }

    public AuditLogEntry getFirst(List<AuditLogEntry> auditLogEntryList, ActionType actionType, String targetID) {
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

    public AuditLogEntry getFirst(List<AuditLogEntry> auditLogEntryList, ActionType actionType, String change, String targetID) {
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

    public EmbedBuilder genericVoiceEmbed(Color color, String title, Member member, AuditLogEntry auditLogEntry) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = addMemberAvatar(embedBuilder, member);
        embedBuilder.setColor(color);

        embedBuilder.setTitle(title);
        embedBuilder.addField(notificationManager.getAlkabot().t("notification.generic.member"), notifMember(member), true);

        String admin = notificationManager.getAlkabot().t("notification.generic.unknown");
        if (auditLogEntry != null)
            admin = notifUser(auditLogEntry.getUser());

        embedBuilder.addField(notificationManager.getAlkabot().t("notification.generic.moderator"), admin, true);

        GuildVoiceState guildVoiceState = member.getVoiceState();
        if (guildVoiceState != null)
            embedBuilder.addField(notificationManager.getAlkabot().t("notification.generic.channel"), notifAudioChannel(guildVoiceState.getChannel()), false);

        return embedBuilder;
    }

    public EmbedBuilder genericValueEmbed(Color color, String title, Member member, AuditLogEntry auditLogEntry, String oldValue, String newValue) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = addMemberAvatar(embedBuilder, member);
        embedBuilder.setColor(color);

        embedBuilder.setTitle(title);
        embedBuilder.addField(notificationManager.getAlkabot().t("notification.generic.member"), notifMember(member), true);

        String admin = notificationManager.getAlkabot().t("notification.generic.unknown");
        if (auditLogEntry != null)
            admin = notifUser(auditLogEntry.getUser());

        embedBuilder.addField(notificationManager.getAlkabot().t("notification.generic.moderator"), admin, true);
        embedBuilder.addField(notificationManager.getAlkabot().t("notification.generic.old_value"), notifValue(oldValue), false);
        embedBuilder.addField(notificationManager.getAlkabot().t("notification.generic.new_value"), notifValue(newValue), true);

        return embedBuilder;
    }*/
}
