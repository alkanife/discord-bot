package fr.alkanife.alkabot.notification.notifier;

import fr.alkanife.alkabot.configuration.json.notifications.ModNotifConfig;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.notification.NotificationChannel;
import fr.alkanife.alkabot.notification.NotificationManager;
import fr.alkanife.alkabot.notification.NotificationUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class ModeratorNotifier extends Notifier {

    private final ModNotifConfig jsonNotificationsModerator;

    public ModeratorNotifier(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MODERATOR);
        jsonNotificationsModerator = alkabot.getConfig().getNotifConfig().getModNotifConfig();
    }

    public void notifyKick(User user, User moderator, String reason) {
        if (!jsonNotificationsModerator.isKick())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.kick.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.kick.title")
                        .parseUserNames(user)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.kick.icon")
                        .parseUserAvatars(user)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createUserField("notification.moderator.kick", user, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.kick", moderator, true));
        embed.addField(NotificationUtils.createReasonField("notification.moderator.kick", reason));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyBan(User user, User moderator, String reason) {
        if (!jsonNotificationsModerator.isBan())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.ban.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.ban.title")
                        .parseUserNames(user)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.ban.icon")
                        .parseUserAvatars(user)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createUserField("notification.moderator.ban", user, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.ban", moderator, true));
        embed.addField(NotificationUtils.createReasonField("notification.moderator.ban", reason));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyUnban(User user, User moderator) {
        if (!jsonNotificationsModerator.isUnban())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.unban.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.unban.title")
                        .parseUserNames(user)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.unban.icon")
                        .parseUserAvatars(user)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createUserField("notification.moderator.unban", user, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.unban", moderator, true));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyTimeout(User user, User moderator, String reason, OffsetDateTime timeout) { // Experimental
        if (!jsonNotificationsModerator.isTimeout())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.timeout.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.timeout.title")
                        .parseUserNames(user)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.timeout.icon")
                        .parseUserAvatars(user)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createUserField("notification.moderator.timeout", user, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.timeout", moderator, true));
        embed.addField(
                Lang.t("notification.moderator.timeout.until")
                        .getValue(),
                new SimpleDateFormat(Lang.getDateFormat(), Lang.getDateLocale()).format(new Date(timeout.toInstant().toEpochMilli())), false);
        embed.addField(NotificationUtils.createReasonField("notification.moderator.timeout", reason));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyUntimeout(User user, User moderator) { // Experimental
        if (!jsonNotificationsModerator.isTimeout()) // todo: untimout in config
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.untimeout.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.untimeout.title")
                        .parseUserNames(user)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.untimeout.icon")
                        .parseUserAvatars(user)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createUserField("notification.moderator.untimeout", user, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.untimeout", moderator, true));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyTimeoutChange(User user, User moderator, OffsetDateTime oldTimeout, OffsetDateTime newTimeout) { // Experimental
        if (!jsonNotificationsModerator.isTimeout()) // todo: timeout_change in config
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.timeout_change.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.timeout_change.title")
                        .parseUserNames(user)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.timeout_change.icon")
                        .parseUserAvatars(user)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createUserField("notification.moderator.timeout_change", user, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.timeout_change", moderator, true));
        embed.addField(
                Lang.t("notification.moderator.timeout_change.old")
                        .getValue(),
                new SimpleDateFormat(Lang.getDateFormat(), Lang.getDateLocale()).format(new Date(oldTimeout.toInstant().toEpochMilli())), false);
        embed.addField(
                Lang.t("notification.moderator.timeout_change.new")
                        .getValue(),
                new SimpleDateFormat(Lang.getDateFormat(), Lang.getDateLocale()).format(new Date(newTimeout.toInstant().toEpochMilli())), false);

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyDeafenMember(Member member, User moderator) { // Experimental
        if (!jsonNotificationsModerator.isDeafenMember())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.deafen.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.deafen.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.deafen.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.moderator.deafen", member, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.deafen", moderator, true));

        GuildVoiceState guildVoiceState = member.getVoiceState();
        if (guildVoiceState != null)
            embed.addField(NotificationUtils.createAudioChannelField("notification.moderator.deafen", guildVoiceState.getChannel(), false));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyUndeafenMember(Member member, User moderator) { // Experimental
        if (!jsonNotificationsModerator.isUndeafenMember())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.undeafen.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.undeafen.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.undeafen.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.moderator.undeafen", member, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.undeafen", moderator, true));

        GuildVoiceState guildVoiceState = member.getVoiceState();
        if (guildVoiceState != null)
            embed.addField(NotificationUtils.createAudioChannelField("notification.moderator.undeafen", guildVoiceState.getChannel(), false));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyMuteMember(Member member, User moderator) { // Experimental
        if (!jsonNotificationsModerator.isMuteMember())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.mute.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.mute.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.mute.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.moderator.mute", member, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.mute", moderator, true));

        GuildVoiceState guildVoiceState = member.getVoiceState();
        if (guildVoiceState != null)
            embed.addField(NotificationUtils.createAudioChannelField("notification.moderator.mute", guildVoiceState.getChannel(), false));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyUnmuteMember(Member member, User moderator) { // Experimental
        if (!jsonNotificationsModerator.isUnmuteMember())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.unmute.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.unmute.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.unmute.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.moderator.unmute", member, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.unmute", moderator, true));

        GuildVoiceState guildVoiceState = member.getVoiceState();
        if (guildVoiceState != null)
            embed.addField(NotificationUtils.createAudioChannelField("notification.moderator.unmute", guildVoiceState.getChannel(), false));

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

    public void notifyChangeMemberNickname(Member member, User moderator, String oldNickname, String newNickname) { // Experimental
        if (!jsonNotificationsModerator.isChangeMemberNickname())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Lang.t("notification.moderator.nickname.color").getColor());
        embed.setTitle(
                Lang.t("notification.moderator.nickname.title")
                        .parseMemberNames(member)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("notification.moderator.nickname.icon")
                        .parseMemberAvatars(member)
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMemberField("notification.moderator.nickname", member, true));
        embed.addField(NotificationUtils.createModeratorField("notification.moderator.nickname", moderator, true));

        if (oldNickname != null)
            embed.addField(Lang.t("notification.moderator.nickname.old").getValue(), oldNickname, false);

        embed.addField(Lang.t("notification.moderator.nickname.new").getValue(), newNickname, false);

        notificationManager.sendNotification(notificationChannel, embed.build());
    }

}
