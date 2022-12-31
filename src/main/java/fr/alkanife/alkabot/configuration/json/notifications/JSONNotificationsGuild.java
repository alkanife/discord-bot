package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotificationsGuild {

    private String channel_id;
    private boolean channel_create, channel_delete, channel_update;
    private boolean permission_override_create, permission_override_delete, permission_override_update;
    private boolean emoji_added, emoji_delete, emoji_update;
    private boolean sticker_added, sticker_delete, sticker_update;
    private boolean invite_create, invite_delete;
    private boolean guild_update, name_update, icon_update, owner_update;
    private JSONNotificationsGuildRole role;

    public JSONNotificationsGuild() {}

    public JSONNotificationsGuild(String channel_id, boolean channel_create, boolean channel_delete, boolean channel_update, boolean permission_override_create, boolean permission_override_delete, boolean permission_override_update, boolean emoji_added, boolean emoji_delete, boolean emoji_update, boolean sticker_added, boolean sticker_delete, boolean sticker_update, boolean invite_create, boolean invite_delete, boolean guild_update, boolean name_update, boolean icon_update, boolean owner_update, JSONNotificationsGuildRole role) {
        this.channel_id = channel_id;
        this.channel_create = channel_create;
        this.channel_delete = channel_delete;
        this.channel_update = channel_update;
        this.permission_override_create = permission_override_create;
        this.permission_override_delete = permission_override_delete;
        this.permission_override_update = permission_override_update;
        this.emoji_added = emoji_added;
        this.emoji_delete = emoji_delete;
        this.emoji_update = emoji_update;
        this.sticker_added = sticker_added;
        this.sticker_delete = sticker_delete;
        this.sticker_update = sticker_update;
        this.invite_create = invite_create;
        this.invite_delete = invite_delete;
        this.guild_update = guild_update;
        this.name_update = name_update;
        this.icon_update = icon_update;
        this.owner_update = owner_update;
        this.role = role;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public boolean isChannel_create() {
        return channel_create;
    }

    public void setChannel_create(boolean channel_create) {
        this.channel_create = channel_create;
    }

    public boolean isChannel_delete() {
        return channel_delete;
    }

    public void setChannel_delete(boolean channel_delete) {
        this.channel_delete = channel_delete;
    }

    public boolean isChannel_update() {
        return channel_update;
    }

    public void setChannel_update(boolean channel_update) {
        this.channel_update = channel_update;
    }

    public boolean isPermission_override_create() {
        return permission_override_create;
    }

    public void setPermission_override_create(boolean permission_override_create) {
        this.permission_override_create = permission_override_create;
    }

    public boolean isPermission_override_delete() {
        return permission_override_delete;
    }

    public void setPermission_override_delete(boolean permission_override_delete) {
        this.permission_override_delete = permission_override_delete;
    }

    public boolean isPermission_override_update() {
        return permission_override_update;
    }

    public void setPermission_override_update(boolean permission_override_update) {
        this.permission_override_update = permission_override_update;
    }

    public boolean isEmoji_added() {
        return emoji_added;
    }

    public void setEmoji_added(boolean emoji_added) {
        this.emoji_added = emoji_added;
    }

    public boolean isEmoji_delete() {
        return emoji_delete;
    }

    public void setEmoji_delete(boolean emoji_delete) {
        this.emoji_delete = emoji_delete;
    }

    public boolean isEmoji_update() {
        return emoji_update;
    }

    public void setEmoji_update(boolean emoji_update) {
        this.emoji_update = emoji_update;
    }

    public boolean isSticker_added() {
        return sticker_added;
    }

    public void setSticker_added(boolean sticker_added) {
        this.sticker_added = sticker_added;
    }

    public boolean isSticker_delete() {
        return sticker_delete;
    }

    public void setSticker_delete(boolean sticker_delete) {
        this.sticker_delete = sticker_delete;
    }

    public boolean isSticker_update() {
        return sticker_update;
    }

    public void setSticker_update(boolean sticker_update) {
        this.sticker_update = sticker_update;
    }

    public boolean isInvite_create() {
        return invite_create;
    }

    public void setInvite_create(boolean invite_create) {
        this.invite_create = invite_create;
    }

    public boolean isInvite_delete() {
        return invite_delete;
    }

    public void setInvite_delete(boolean invite_delete) {
        this.invite_delete = invite_delete;
    }

    public boolean isGuild_update() {
        return guild_update;
    }

    public void setGuild_update(boolean guild_update) {
        this.guild_update = guild_update;
    }

    public boolean isName_update() {
        return name_update;
    }

    public void setName_update(boolean name_update) {
        this.name_update = name_update;
    }

    public boolean isIcon_update() {
        return icon_update;
    }

    public void setIcon_update(boolean icon_update) {
        this.icon_update = icon_update;
    }

    public boolean isOwner_update() {
        return owner_update;
    }

    public void setOwner_update(boolean owner_update) {
        this.owner_update = owner_update;
    }

    public JSONNotificationsGuildRole getRole() {
        return role;
    }

    public void setRole(JSONNotificationsGuildRole role) {
        this.role = role;
    }
}
