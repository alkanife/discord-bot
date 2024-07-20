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
import dev.alkanife.alkabot.file.FileManipulation;
import dev.alkanife.alkabot.file.JsonFileManipulation;
import dev.alkanife.alkabot.secrets.json.Secrets;
import lombok.Getter;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager extends JsonFileManipulation {

    @Getter
    private AlkabotConfig config;

    public ConfigManager(@NotNull Alkabot alkabot) {
        super(alkabot, new File(alkabot.getArgs().getConfigFilePath()), AlkabotConfig.class);
    }

    @Override
    public boolean setup() {
        config = (AlkabotConfig) cleanData(new AlkabotConfig());
        save();
        return true;
    }

    @Override
    public boolean validateLoad(@NotNull Object data, boolean reload) {
        AlkabotConfig loadedConfig = (AlkabotConfig) data;

        getAlkabot().getLogger().debug("Loaded configuration: {}", loadedConfig);

        if (getAlkabot().getArgs().getOverrideLangFilePath() != null) {
            loadedConfig.setLangFilePath(getAlkabot().getArgs().getOverrideLangFilePath());
            getAlkabot().getLogger().info("Overriding language pack to '{}'", getAlkabot().getArgs().getOverrideLangFilePath());
        }

        if (!reload) {
            if (getAlkabot().getArgs().getOverrideLangFilePath() == null) {
                if (loadedConfig.getLangFilePath() == null) {
                    getAlkabot().getLogger().error("No language was found in the configuration ('lang_file_path').");
                    return false;
                }
            }

            if (loadedConfig.getGuildConfig() == null) {
                getAlkabot().getLogger().error("No Discord server ID was found in the configuration ('guild.guild_id').");
                return false;
            }

            if (loadedConfig.getGuildConfig().getGuildId() == null) {
                getAlkabot().getLogger().error("No Discord server ID was found in the configuration ('guild.guild_id').");
                return false;
            }
        }

        config = loadedConfig;
        return true;
    }

    @NotNull
    @Override
    public Object cleanData(@Nullable Object object) {
        AlkabotConfig configObject = (AlkabotConfig) object;

        // TODO rework configration

        if (configObject == null)
            configObject = new AlkabotConfig();

        // Lang
        if (configObject.getLangFilePath() == null)
            configObject.setLangFilePath("lang/en_US.json");

        // Admin
        if (configObject.getAdminIds() == null) {
            configObject.setAdminIds(new ArrayList<>());
        }

        // Guild
        if (configObject.getGuildConfig() == null) {
            configObject.setGuildConfig(new GuildConfig());
        }

        if (configObject.getGuildConfig().getGuildPresenceConfig() == null) {
            configObject.getGuildConfig().setGuildPresenceConfig(new GuildPresenceConfig(OnlineStatus.ONLINE.name(), null));
        }

        if (configObject.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig() == null) {
            configObject.getGuildConfig().getGuildPresenceConfig().setGuildActivityConfig(new GuildPresenceActivityConfig(false, null, null));
        }

        if (configObject.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().isShowing()) {
            if (configObject.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getType() == null) {
                configObject.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().setShowing(false);
            }

            if (configObject.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().getText() == null) {
                configObject.getGuildConfig().getGuildPresenceConfig().getGuildActivityConfig().setShowing(false);
            }
        }

        // Welcome message
        if (configObject.getWelcomeMessageConfig() == null)
            configObject.setWelcomeMessageConfig(new WelcomeMessageConfig());

        if (configObject.getWelcomeMessageConfig().isEnable()) {
            if (configObject.getWelcomeMessageConfig().getChannelId() == null) {
                configObject.getWelcomeMessageConfig().setEnable(false);
            }
        }

        // Auto role
        if (configObject.getAutoRoleConfig() == null)
            configObject.setAutoRoleConfig(new AutoRoleConfig());

        if (configObject.getAutoRoleConfig().isEnable()) {
            if (configObject.getAutoRoleConfig().getRoleId() == null) {
                configObject.getAutoRoleConfig().setEnable(false);
            }
        }

        // Music settings
        if (configObject.getMusicConfig() == null) {
            configObject.setMusicConfig(new MusicConfig(true));
        }

        // Commands
        if (configObject.getCommandConfig() == null) {
            configObject.setCommandConfig(new CommandConfig(true, null, null));
        }

        if (configObject.getCommandConfig().getMusicCommandConfig() == null) {
            configObject.getCommandConfig().setMusicCommandConfig(new MusicCommandConfig(true, true, true, true, true, true, true, true, true, true, true, null));
        }

        if (configObject.getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig() == null) {
            configObject.getCommandConfig().getMusicCommandConfig().setShortcutCommandConfig(new ShortcutCommandConfig(true, true, true, true));
        }

        if (configObject.getCommandConfig().getUtilsCommandConfig() == null) {
            configObject.getCommandConfig().setUtilsCommandConfig(new UtilsCommandConfig(null, true));
        }

        if (configObject.getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig() == null) {
            configObject.getCommandConfig().getUtilsCommandConfig().setInfoUtilsCommandConfig(new InfoUtilsCommandConfig(true, true, true));
        }

        // Notifications
        if (configObject.getNotifConfig() == null)
            configObject.setNotifConfig(new NotifConfig(null, null, null, null, null));

        // Notifications -- SELF
        if (configObject.getNotifConfig().getSelfNotifConfig() == null)
            configObject.getNotifConfig().setSelfNotifConfig(new SelfNotifConfig(null, false, false));

        if (configObject.getNotifConfig().getSelfNotifConfig().getChannelId() == null) {
            configObject.getNotifConfig().setSelfNotifConfig(new SelfNotifConfig(null, false, false));
        }

        // Notifications -- MESSAGE
        if (configObject.getNotifConfig().getMessageNotifConfig() == null)
            configObject.getNotifConfig().setMessageNotifConfig(new MessageNotifConfig(null, 30, false, false));

        if (configObject.getNotifConfig().getMessageNotifConfig().getChannelId() == null || configObject.getNotifConfig().getMessageNotifConfig().getCache() == 0) {
            configObject.getNotifConfig().setMessageNotifConfig(new MessageNotifConfig(null, 30, false, false));
        }

        // Notifications -- MEMBER
        if (configObject.getNotifConfig().getMemberNotifConfig() == null)
            configObject.getNotifConfig().setMemberNotifConfig(new MemberNotifConfig(null, false, false));

        if (configObject.getNotifConfig().getMemberNotifConfig().getChannelId() == null) {
            configObject.getNotifConfig().setMemberNotifConfig(new MemberNotifConfig(null, false, false));
        }

        // Notifications -- MODERATOR
        if (configObject.getNotifConfig().getModNotifConfig() == null)
            configObject.getNotifConfig().setModNotifConfig(new ModNotifConfig(null, false, false, false, false, false, false, false, false, false));

        if (configObject.getNotifConfig().getModNotifConfig().getChannelId() == null) {
            configObject.getNotifConfig().setModNotifConfig(new ModNotifConfig(null, false, false, false, false, false, false, false, false, false));
        }

        // Notifications -- VOICE
        if (configObject.getNotifConfig().getVoiceNotifConfig() == null)
            configObject.getNotifConfig().setVoiceNotifConfig(new VoiceNotifConfig(null, false, false, false));

        if (configObject.getNotifConfig().getVoiceNotifConfig().getChannelId() == null) {
            configObject.getNotifConfig().setVoiceNotifConfig(new VoiceNotifConfig(null, false, false, false));
        }

        return configObject;
    }

    @Nullable
    @Override
    public Object getDataObject() {
        return config;
    }
}
