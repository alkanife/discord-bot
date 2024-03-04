package dev.alkanife.alkabot.configuration;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.configuration.json.AlkabotConfig;
import dev.alkanife.alkabot.configuration.json.AutoRoleConfig;
import dev.alkanife.alkabot.configuration.json.MusicConfig;
import dev.alkanife.alkabot.configuration.json.WelcomeMessageConfig;
import dev.alkanife.alkabot.configuration.json.commands.*;
import dev.alkanife.alkabot.configuration.json.guild.GuildConfig;
import dev.alkanife.alkabot.configuration.json.guild.GuildPresenceActivityConfig;
import dev.alkanife.alkabot.configuration.json.guild.GuildPresenceConfig;
import dev.alkanife.alkabot.configuration.json.notifications.*;
import dev.alkanife.alkabot.util.JsonDataFileManager;
import lombok.Getter;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager extends JsonDataFileManager {

    @Getter
    private AlkabotConfig config;

    public ConfigManager(Alkabot alkabot, File file) {
        super(alkabot, file, AlkabotConfig.class);

        readMessage = "Reading configuration from '" + file.getName() + "'";
        createMessage = "The configuration file was not found at the specified path. Creating a new one at '" + file.getAbsolutePath() + "'";
        updateMessage = "Updating configuration of '" + file.getName() + "'";
        loadMessage = "Loading configuration";
    }

    @Override
    public void cleanData() {
        AlkabotConfig fileConfig;

        if (getData() == null) {
            fileConfig = new AlkabotConfig();
        } else {
            fileConfig = (AlkabotConfig) getData();
        }

        // TODO rework configration

        // Lang
        if (fileConfig.getLangFile() == null)
            fileConfig.setLangFile("en_US");

        // Admin
        if (fileConfig.getAdminIds() == null) {
            fileConfig.setAdminIds(new ArrayList<>());
        }

        // Guild
        if (fileConfig.getGuildConfig() == null) {
            fileConfig.setGuildConfig(new GuildConfig());
        }

        if (fileConfig.getGuildConfig().getGuildPresenceConfig() == null) {
            fileConfig.getGuildConfig().setGuildPresenceConfig(new GuildPresenceConfig(OnlineStatus.ONLINE.name(), null));
        }

        if (fileConfig.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig() == null) {
            fileConfig.getGuildConfig().getGuildPresenceConfig().setGuildActivityConfig(new GuildPresenceActivityConfig(false, null, null));
        }

        if (fileConfig.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().isShowing()) {
            if (fileConfig.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getType() == null) {
                fileConfig.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().setShowing(false);
            }

            if (fileConfig.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getText() == null) {
                fileConfig.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().setShowing(false);
            }
        }

        // Welcome message
        if (fileConfig.getWelcomeMessageConfig() == null)
            fileConfig.setWelcomeMessageConfig(new WelcomeMessageConfig());

        if (fileConfig.getWelcomeMessageConfig().isEnable()) {
            if (fileConfig.getWelcomeMessageConfig().getChannelId() == null) {
                fileConfig.getWelcomeMessageConfig().setEnable(false);
            }
        }

        // Auto role
        if (fileConfig.getAutoRoleConfig() == null)
            fileConfig.setAutoRoleConfig(new AutoRoleConfig());

        if (fileConfig.getAutoRoleConfig().isEnable()) {
            if (fileConfig.getAutoRoleConfig().getRoleId() == null) {
                fileConfig.getAutoRoleConfig().setEnable(false);
            }
        }

        // Music settings
        if (fileConfig.getMusicConfig() == null) {
            fileConfig.setMusicConfig(new MusicConfig(true));
        }

        // Commands
        if (fileConfig.getCommandConfig() == null) {
            fileConfig.setCommandConfig(new CommandConfig(true, null, null));
        }

        if (fileConfig.getCommandConfig().getMusicCommandConfig() == null) {
            fileConfig.getCommandConfig().setMusicCommandConfig(new MusicCommandConfig(true, true, true, true, true, true, true, true, true, true, true, null));
        }

        if (fileConfig.getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig() == null) {
            fileConfig.getCommandConfig().getMusicCommandConfig().setShortcutCommandConfig(new ShortcutCommandConfig(true, true, true, true));
        }

        if (fileConfig.getCommandConfig().getUtilsCommandConfig() == null) {
            fileConfig.getCommandConfig().setUtilsCommandConfig(new UtilsCommandConfig(null, true));
        }

        if (fileConfig.getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig() == null) {
            fileConfig.getCommandConfig().getUtilsCommandConfig().setInfoUtilsCommandConfig(new InfoUtilsCommandConfig(true, true, true));
        }

        // Notifications
        if (fileConfig.getNotifConfig() == null)
            fileConfig.setNotifConfig(new NotifConfig(null, null, null, null, null));

        // Notifications -- SELF
        if (fileConfig.getNotifConfig().getSelfNotifConfig() == null)
            fileConfig.getNotifConfig().setSelfNotifConfig(new SelfNotifConfig(null, false, false));

        if (fileConfig.getNotifConfig().getSelfNotifConfig().getChannelId() == null) {
            fileConfig.getNotifConfig().setSelfNotifConfig(new SelfNotifConfig(null, false, false));
        }

        // Notifications -- MESSAGE
        if (fileConfig.getNotifConfig().getMessageNotifConfig() == null)
            fileConfig.getNotifConfig().setMessageNotifConfig(new MessageNotifConfig(null, 30, false, false));

        if (fileConfig.getNotifConfig().getMessageNotifConfig().getChannelId() == null || fileConfig.getNotifConfig().getMessageNotifConfig().getCache() == 0) {
            fileConfig.getNotifConfig().setMessageNotifConfig(new MessageNotifConfig(null, 30, false, false));
        }

        // Notifications -- MEMBER
        if (fileConfig.getNotifConfig().getMemberNotifConfig() == null)
            fileConfig.getNotifConfig().setMemberNotifConfig(new MemberNotifConfig(null, false, false));

        if (fileConfig.getNotifConfig().getMemberNotifConfig().getChannelId() == null) {
            fileConfig.getNotifConfig().setMemberNotifConfig(new MemberNotifConfig(null, false, false));
        }

        // Notifications -- MODERATOR
        if (fileConfig.getNotifConfig().getModNotifConfig() == null)
            fileConfig.getNotifConfig().setModNotifConfig(new ModNotifConfig(null, false, false, false, false, false, false, false, false, false));

        if (fileConfig.getNotifConfig().getModNotifConfig().getChannelId() == null) {
            fileConfig.getNotifConfig().setModNotifConfig(new ModNotifConfig(null, false, false, false, false, false, false, false, false, false));
        }

        // Notifications -- VOICE
        if (fileConfig.getNotifConfig().getVoiceNotifConfig() == null)
            fileConfig.getNotifConfig().setVoiceNotifConfig(new VoiceNotifConfig(null, false, false, false));

        if (fileConfig.getNotifConfig().getVoiceNotifConfig().getChannelId() == null) {
            fileConfig.getNotifConfig().setVoiceNotifConfig(new VoiceNotifConfig(null, false, false, false));
        }
        
        setData(fileConfig);
    }

    @Override
    public boolean onLoad(boolean reloading) {
        config = (AlkabotConfig) getData();

        getAlkabot().getLogger().debug("Configuration: " + config.toString());

        if (!reloading) {
            if (getAlkabot().getArgs().getOverrideLang() == null) {
                if (config.getLangFile() == null) {
                    getAlkabot().getLogger().error("No language was found in the configuration ('lang').");
                    return false;
                }
            }

            if (config.getGuildConfig() == null) {
                getAlkabot().getLogger().error("No Discord server ID was found in the configuration ('guild.guild_id').");
                return false;
            }

            if (config.getGuildConfig().getGuildId() == null) {
                getAlkabot().getLogger().error("No Discord server ID was found in the configuration ('guild.guild_id').");
                return false;
            }
        }

        return true;
    }
}
