package fr.alkanife.alkabot.notification.guild;

import fr.alkanife.alkabot.notification.AbstractNotification;
import fr.alkanife.alkabot.notification.NotificationChannel;
import fr.alkanife.alkabot.notification.NotificationManager;

public class GuildNotification extends AbstractNotification {

    private final GuildChannelNotification guildChannelNotification;
    private final GuildPermissionOverrideNotification guildPermissionOverrideNotification;
    private final GuildEmojiNotification guildEmojiNotification;
    private final GuildStickerNotification guildStickerNotification;
    private final GuildInviteNotification guildInviteNotification;
    private final GuildUpdateNotification guildUpdateNotification;
    private final GuildRoleNotification guildRoleNotification;

    public GuildNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.GUILD);
        guildChannelNotification = new GuildChannelNotification(this);
        guildPermissionOverrideNotification = new GuildPermissionOverrideNotification(this);
        guildEmojiNotification = new GuildEmojiNotification(this);
        guildStickerNotification = new GuildStickerNotification(this);
        guildInviteNotification = new GuildInviteNotification(this);
        guildUpdateNotification = new GuildUpdateNotification(this);
        guildRoleNotification = new GuildRoleNotification(this);
    }

    public GuildChannelNotification getGuildChannelNotification() {
        return guildChannelNotification;
    }

    public GuildPermissionOverrideNotification getGuildPermissionOverrideNotification() {
        return guildPermissionOverrideNotification;
    }

    public GuildEmojiNotification getGuildEmojiNotification() {
        return guildEmojiNotification;
    }

    public GuildStickerNotification getGuildStickerNotification() {
        return guildStickerNotification;
    }

    public GuildInviteNotification getGuildInviteNotification() {
        return guildInviteNotification;
    }

    public GuildUpdateNotification getGuildUpdateNotification() {
        return guildUpdateNotification;
    }

    public GuildRoleNotification getGuildRoleNotification() {
        return guildRoleNotification;
    }
}