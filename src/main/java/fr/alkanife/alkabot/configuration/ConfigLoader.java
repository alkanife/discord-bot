package fr.alkanife.alkabot.configuration;

import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.Configuration;
import fr.alkanife.alkabot.configuration.json.MusicConfig;
import fr.alkanife.alkabot.configuration.json.commands.*;
import fr.alkanife.alkabot.configuration.json.guild.GuildPresenceActivityConfig;
import fr.alkanife.alkabot.configuration.json.guild.GuildPresenceConfig;
import fr.alkanife.alkabot.configuration.json.notifications.*;
import fr.alkanife.alkabot.log.Logs;
import fr.alkanife.alkabot.util.tool.JsonLoader;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ConfigLoader extends JsonLoader {

    public ConfigLoader(Alkabot alkabot) {
        super(alkabot, new File(alkabot.getParameters().getConfigurationPath()));
    }

    @Override
    public void processLoad() throws Exception {
        alkabot.getLogger().debug("Using config file at path '" + file.getPath() + "'");

        String content = Files.readString(file.toPath());
        Configuration config = new GsonBuilder().serializeNulls().create().fromJson(content, Configuration.class);

        //
        // CAN'T START WITHOUT IT
        //
        if (config.getLangFile() == null) {
            cantContinue("lang");
            return;
        } else {
            if (config.getLangFile().endsWith(".json"))
                config.setLangFile(config.getLangFile().replaceAll(".json", ""));
        }

        if (config.getGuildConfig() == null) {
            cantContinue("guild");
            return;
        }

        if (config.getGuildConfig().getGuildId() == null) {
            cantContinue("guild.guild_id");
            return;
        }

        //
        // SET TO A DEFAULT VALUE IF NULL
        //
        // Admin
        if (config.getAdminIds() == null) {
            config.setAdminIds(new ArrayList<>());
            changeNull("administrator_ids", "0");
        }

        // Guild
        if (config.getGuildConfig().getGuildPresenceConfig() == null) {
            config.getGuildConfig().setGuildPresenceConfig(new GuildPresenceConfig(OnlineStatus.ONLINE.name(), null));
            changeNull("guild.presence.status", OnlineStatus.ONLINE.name());
        }

        if (config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig() == null) {
            config.getGuildConfig().getGuildPresenceConfig().setGuildActivityConfig(new GuildPresenceActivityConfig(false, null, null));
            changeNull("guild.presence.activity.show", "false");
            changeNull("guild.presence.activity.type", "null");
            changeNull("guild.presence.activity.text", "null");
        }

        if (config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().isShowing()) {
            if (config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getType() == null) {
                config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().setShowing(false);
                changingBecauseNoValue("guild.presence.activity.show", "false", "guild.presence.activity.type");
            }

            if (config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getText() == null) {
                config.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().setShowing(false);
                changingBecauseNoValue("guild.presence.activity.show", "false", "guild.presence.activity.text");
            }
        }

        // Welcome message
        if (config.getWelcomeMessageConfig().isEnable()) {
            if (config.getWelcomeMessageConfig().getChannelId() == null) {
                config.getWelcomeMessageConfig().setEnable(false);
                changingBecauseNoValue("welcome_message.enable", "false", "welcome_message.channel_id");
            }
        }

        // Auto role
        if (config.getAutoRoleConfig().isEnable()) {
            if (config.getAutoRoleConfig().getRoleId() == null) {
                config.getAutoRoleConfig().setEnable(false);
                changingBecauseNoValue("auto_role.enable", "false", "auto_role.role_id");
            }
        }

        // Music settings
        if (config.getMusicConfig() == null) {
            config.setMusicConfig(new MusicConfig(true));
            changeNull("music.auto_stop", "true");
        }

        // Commands
        if (config.getCommandConfig() == null) {
            config.setCommandConfig(new CommandConfig(true, null, null));
            changingBecauseNoValue("commands.about", "true", "commands");
        }

        if (config.getCommandConfig().getMusicCommandConfig() == null) {
            config.getCommandConfig().setMusicCommandConfig(new MusicCommandConfig(true, true, true, true, true, true, true, true, true, true, true, null));
            changingBecauseNoValue("commands.music.*", "true", "commands.music");
        }

        if (config.getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig() == null) {
            config.getCommandConfig().getMusicCommandConfig().setShortcutCommandConfig(new ShortcutCommandConfig(true, true, true, true));
            changingBecauseNoValue("commands.music.shortcut.*", "true", "commands.music.shortcut");
        }

        if (config.getCommandConfig().getUtilsCommandConfig() == null) {
            config.getCommandConfig().setUtilsCommandConfig(new UtilsCommandConfig(null, true));
            changingBecauseNoValue("commands.music.utilities.copy", "true", "commands.music.utilities");
        }

        if (config.getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig() == null) {
            config.getCommandConfig().getUtilsCommandConfig().setInfoUtilsCommandConfig(new InfoUtilsCommandConfig(true, true, true));
            changingBecauseNoValue("commands.music.utilities.info.*", "true", "commands.music.utilities.info");
        }

        // Notifications
        if (config.getNotifConfig() == null)
            config.setNotifConfig(new NotifConfig(null, null, null, null, null));

        // Notifications -- SELF
        if (config.getNotifConfig().getSelfNotifConfig() == null)
            config.getNotifConfig().setSelfNotifConfig(new SelfNotifConfig(null, false, false));

        if (config.getNotifConfig().getSelfNotifConfig().getChannelId() == null) {
            config.getNotifConfig().setSelfNotifConfig(new SelfNotifConfig(null, false, false));
            changingBecauseNoValue("notifications.self.*", "false", "notifications.self.channel_id");
        }

        // Notifications -- MESSAGE
        if (config.getNotifConfig().getMessageNotifConfig() == null)
            config.getNotifConfig().setMessageNotifConfig(new MessageNotifConfig(null, 0, false, false));

        if (config.getNotifConfig().getMessageNotifConfig().getChannelId() == null || config.getNotifConfig().getMessageNotifConfig().getCache() == 0) {
            config.getNotifConfig().setMessageNotifConfig(new MessageNotifConfig(null, 0, false, false));
            changingBecauseNoValue("notifications.message.*", "false", "notifications.message.channel_id / notifications.message.cache");
        }

        // Notifications -- MEMBER
        if (config.getNotifConfig().getMemberNotifConfig() == null)
            config.getNotifConfig().setMemberNotifConfig(new MemberNotifConfig(null, false, false));

        if (config.getNotifConfig().getMemberNotifConfig().getChannelId() == null) {
            config.getNotifConfig().setMemberNotifConfig(new MemberNotifConfig(null, false, false));
            changingBecauseNoValue("notifications.member.*", "false", "notifications.member.channel_id");
        }

        // Notifications -- MODERATOR
        if (config.getNotifConfig().getModNotifConfig() == null)
            config.getNotifConfig().setModNotifConfig(new ModNotifConfig(null, false, false, false, false, false, false, false, false, false));

        if (config.getNotifConfig().getModNotifConfig().getChannelId() == null) {
            config.getNotifConfig().setModNotifConfig(new ModNotifConfig(null, false, false, false, false, false, false, false, false, false));
            changingBecauseNoValue("notifications.moderator.*", "false", "notifications.moderator.channel_id");
        }

        // Notifications -- VOICE
        if (config.getNotifConfig().getVoiceNotifConfig() == null)
            config.getNotifConfig().setVoiceNotifConfig(new VoiceNotifConfig(null, false, false, false));

        if (config.getNotifConfig().getVoiceNotifConfig().getChannelId() == null) {
            config.getNotifConfig().setVoiceNotifConfig(new VoiceNotifConfig(null, false, false, false));
            changingBecauseNoValue("notifications.voice.*", "false", "notifications.voice.channel_id");
        }

        alkabot.getLogger().debug("Parsed configuration: " + config.toString());

        alkabot.setConfig(config);
    }
}
