package fr.alkanife.alkabot.configuration;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.JSONAdmin;
import fr.alkanife.alkabot.configuration.json.JSONConfiguration;
import fr.alkanife.alkabot.configuration.json.commands.*;
import fr.alkanife.alkabot.configuration.json.guild.JSONGuildPresence;
import fr.alkanife.alkabot.configuration.json.guild.JSONGuildPresenceActivity;
import fr.alkanife.alkabot.configuration.json.notifications.*;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.OnlineStatus;
import org.slf4j.event.Level;

import java.util.ArrayList;

public class ConfigurationParser extends AbstractWorker {

    public ConfigurationParser(boolean reload) {
        super(reload);

        Alkabot.debug("Parsing configuration");

        JSONConfiguration conf = Alkabot.getConfig();

        //
        // CAN'T START WITHOUT IT
        //
        if (StringUtils.isNull(conf.getLang_file())) {
            log(Level.ERROR, "Can't continue because the 'lang_file' field is empty");
            setStatus(Status.FAIL);
            return;
        }

        if (conf.getGuild() == null) {
            log(Level.ERROR, "Can't continue because the 'guild' field is empty");
            setStatus(Status.FAIL);
            return;
        }

        if (StringUtils.isNull(conf.getGuild().getGuild_id())) {
            log(Level.ERROR, "Can't continue because the 'guild.guild_id' field is empty");
            setStatus(Status.FAIL);
            return;
        }

        //
        // SET TO A DEFAULT VALUE IF NULL
        //

        if (conf.getShortcut_file() == null) {
            conf.setShortcut_file("shortcuts.json");
            changeNull("shortcut_file", "shortcuts.json");
        }

        // Admin
        if (conf.getAdmin() == null) {
            conf.setAdmin(new JSONAdmin(false, false, null));
            changeNull("admin.metrics_for_nerds", "false");
            changeNull("admin.admin_only", "false");
        }

        if (conf.getAdmin().getAdministrators_id() == null) {
            conf.getAdmin().setAdministrators_id(new ArrayList<>());
            changeNull("admin.administrators_id", "0");
        }

        // Guild
        if (conf.getGuild().getPresence() == null) {
            conf.getGuild().setPresence(new JSONGuildPresence(OnlineStatus.ONLINE.name(), null));
            changeNull("guild.presence.status", OnlineStatus.ONLINE.name());
        }

        if (conf.getGuild().getPresence().getActivity() == null) {
            conf.getGuild().getPresence().setActivity(new JSONGuildPresenceActivity(false, null, null));
            changeNull("guild.presence.activity.show", "false");
            changeNull("guild.presence.activity.type", "null");
            changeNull("guild.presence.activity.text", "null");
        }

        if (conf.getGuild().getPresence().getActivity().isShow()) {
            if (StringUtils.isNull(conf.getGuild().getPresence().getActivity().getType())) {
                conf.getGuild().getPresence().getActivity().setShow(false);
                changingBecauseNoValue("guild.presence.activity.show", "false", "guild.presence.activity.type");
            }

            if (StringUtils.isNull(conf.getGuild().getPresence().getActivity().getType())) {
                conf.getGuild().getPresence().getActivity().setShow(false);
                changingBecauseNoValue("guild.presence.activity.show", "false", "guild.presence.activity.text");
            }
        }

        // Welcome message
        if (conf.getWelcome_message().isEnable()) {
            if (StringUtils.isNull(conf.getWelcome_message().getChannel_id())) {
                conf.getWelcome_message().setEnable(false);
                changingBecauseNoValue("welcome_message.enable", "false", "welcome_message.channel_id");
            }
        }

        // Auto role
        if (conf.getAuto_role().isEnable()) {
            if (StringUtils.isNull(conf.getAuto_role().getRole_id())) {
                conf.getAuto_role().setEnable(false);
                changingBecauseNoValue("auto_role.enable", "false", "auto_role.role_id");
            }
        }

        // Commands
        if (conf.getCommands() == null) {
            conf.setCommands(new JSONCommands(true, null, null));
            changingBecauseNoValue("commands.about", "true", "commands");
        }

        if (conf.getCommands().getMusic() == null) {
            conf.getCommands().setMusic(new JSONCommandsMusic(true, true, true, true, true, true, true, true, true, true, null));
            changingBecauseNoValue("commands.music.*", "true", "commands.music");
        }

        if (conf.getCommands().getMusic().getShortcut() == null) {
            conf.getCommands().getMusic().setShortcut(new JSONCommandsMusicShortcut(true, true, true, true));
            changingBecauseNoValue("commands.music.shortcut.*", "true", "commands.music.shortcut");
        }

        if (conf.getCommands().getUtilities() == null) {
            conf.getCommands().setUtilities(new JSONCommandsUtilities(null, true));
            changingBecauseNoValue("commands.music.utilities.copy", "true", "commands.music.utilities");
        }

        if (conf.getCommands().getUtilities().getInfo() == null) {
            conf.getCommands().getUtilities().setInfo(new JSONCommandsUtilitiesInfo(true, true, true));
            changingBecauseNoValue("commands.music.utilities.info.*", "true", "commands.music.utilities.info");
        }

        // Notifications
        if (conf.getNotifications() == null)
            conf.setNotifications(new JSONNotifications(null, null, null, null, null, null));

        // Notifications -- SELF
        if (conf.getNotifications().getSelf() == null)
            conf.getNotifications().setSelf(new JSONNotificationsSelf(null, false, false));

        if (StringUtils.isNull(conf.getNotifications().getSelf().getChannel_id())) {
            conf.getNotifications().setSelf(new JSONNotificationsSelf(null, false, false));
            changingBecauseNoValue("notifications.self.*", "false", "notifications.self.channel_id");
        }

        // Notifications -- MESSAGE
        if (conf.getNotifications().getMessage() == null)
            conf.getNotifications().setMessage(new JSONNotificationsMessage(null, 0, false, false));

        if (StringUtils.isNull(conf.getNotifications().getMessage().getChannel_id())
                || conf.getNotifications().getMessage().getCache() == 0) {
            conf.getNotifications().setMessage(new JSONNotificationsMessage(null, 0, false, false));
            changingBecauseNoValue("notifications.message.*", "false", "notifications.message.channel_id / notifications.message.cache");
        }

        // Notifications -- MEMBER
        if (conf.getNotifications().getMember() == null)
            conf.getNotifications().setMember(new JSONNotificationsMember(null, false, false));

        if (StringUtils.isNull(conf.getNotifications().getMember().getChannel_id())) {
            conf.getNotifications().setMember(new JSONNotificationsMember(null, false, false));
            changingBecauseNoValue("notifications.member.*", "false", "notifications.member.channel_id");
        }

        // Notifications -- MODERATOR
        if (conf.getNotifications().getModerator() == null)
            conf.getNotifications().setModerator(new JSONNotificationsModerator(null, false, false, false, false, false, false, false, false, false));

        if (StringUtils.isNull(conf.getNotifications().getModerator().getChannel_id())) {
            conf.getNotifications().setModerator(new JSONNotificationsModerator(null, false, false, false, false, false, false, false, false, false));
            changingBecauseNoValue("notifications.moderator.*", "false", "notifications.moderator.channel_id");
        }

        // Notifications -- VOICE
        if (conf.getNotifications().getVoice() == null)
            conf.getNotifications().setVoice(new JSONNotificationsVoice(null, false, false, false));

        if (StringUtils.isNull(conf.getNotifications().getVoice().getChannel_id())) {
            conf.getNotifications().setVoice(new JSONNotificationsVoice(null, false, false, false));
            changingBecauseNoValue("notifications.voice.*", "false", "notifications.voice.channel_id");
        }

        // Notifications -- GUILD
        if (conf.getNotifications().getGuild() == null)
            conf.getNotifications().setGuild(new JSONNotificationsGuild(null, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, null));

        if (StringUtils.isNull(conf.getNotifications().getGuild().getChannel_id())) {
            conf.getNotifications().setGuild(new JSONNotificationsGuild(null, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, null));
            changingBecauseNoValue("notifications.guild.*", "false", "notifications.guild.channel_id");
        }

        if (conf.getNotifications().getGuild().getRole() == null) {
            conf.getNotifications().getGuild().setRole(new JSONNotificationsGuildRole(false, false, false, false));
            changingBecauseNoValue("notifications.guild.role.*", "false", "notifications.guild.role");
        }
    }

    private void changingBecauseNoValue(String field, String newValue, String noValueField) {
        log(Level.WARN, "Changing '" + field + "' to '" + newValue + "', there is no value in '" + noValueField + "'");
        setStatus(Status.NOT_PERFECT);
    }

    private void changeNull(String field, String newValue) {
        log(Level.WARN, "'" + field + "' is null, changing it to '" + newValue + "'");
        setStatus(Status.NOT_PERFECT);
    }
}
