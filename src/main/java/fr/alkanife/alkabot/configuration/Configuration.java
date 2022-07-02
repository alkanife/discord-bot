package fr.alkanife.alkabot.configuration;

import java.util.List;

public class Configuration {

    private String token;
    private List<String> administrators_id;
    private String guild_id;
    private boolean admin_only;
    private boolean debug;
    private Presence presence;
    private WelcomeMessage welcome_message;
    private AutoRole auto_role;
    private Logs logs;
    private Commands commands;

    public Configuration() {}

    public Configuration(String token, List<String> administrators_id, String guild_id, boolean admin_only, boolean debug, Presence presence, WelcomeMessage welcome_message, AutoRole auto_role, Logs logs, Commands commands) {
        this.token = token;
        this.administrators_id = administrators_id;
        this.guild_id = guild_id;
        this.admin_only = admin_only;
        this.debug = debug;
        this.presence = presence;
        this.welcome_message = welcome_message;
        this.auto_role = auto_role;
        this.logs = logs;
        this.commands = commands;
    }

    public String getToken() {
        return token;
    }

    public List<String> getAdministrators_id() {
        return administrators_id;
    }

    public String getGuild_id() {
        return guild_id;
    }

    public boolean isAdmin_only() {
        return admin_only;
    }

    public boolean isDebug() {
        return debug;
    }

    public Presence getPresence() {
        return presence;
    }

    public WelcomeMessage getWelcome_message() {
        return welcome_message;
    }

    public AutoRole getAuto_role() {
        return auto_role;
    }

    public Logs getLogs() {
        return logs;
    }

    public Commands getCommands() {
        return commands;
    }

    public static class Presence {
        private String status;
        private Activity activity;

        public Presence() {}

        public Presence(String status, Activity activity) {
            this.status = status;
            this.activity = activity;
        }

        public String getStatus() {
            return status;
        }

        public Activity getActivity() {
            return activity;
        }

        public static class Activity {
            private boolean show;
            private String type;
            private String text;

            public Activity() {}

            public Activity(boolean show, String type, String text) {
                this.show = show;
                this.type = type;
                this.text = text;
            }

            public boolean isShow() {
                return show;
            }

            public String getType() {
                return type;
            }

            public String getText() {
                return text;
            }
        }
    }

    public static class AutoRole {
        private boolean enable;
        private String role_id;

        public AutoRole() {}

        public AutoRole(boolean enable, String role_id) {
            this.enable = enable;
            this.role_id = role_id;
        }

        public boolean isEnable() {
            return enable;
        }

        public String getRole_id() {
            return role_id;
        }
    }

    public static class WelcomeMessage {
        private boolean enable;
        private String channel_id;

        public WelcomeMessage() {}

        public WelcomeMessage(boolean enable, String channel_id) {
            this.enable = enable;
            this.channel_id = channel_id;
        }

        public boolean isEnable() {
            return enable;
        }

        public String getChannel_id() {
            return channel_id;
        }
    }

    public static class Logs {
        private String channel_id;
        private int message_cache;
        private boolean admin, join, left, join_voice, left_voice, move_voice, voice_deafen, voice_undeafen, voice_mute, voice_unmute, ban, unban, kick, edit, delete;

        public Logs() {}

        public Logs(String channel_id, int message_cache, boolean admin, boolean join, boolean left, boolean join_voice, boolean left_voice, boolean move_voice, boolean voice_deafen, boolean voice_undeafen, boolean voice_mute, boolean voice_unmute, boolean ban, boolean unban, boolean kick, boolean edit, boolean delete) {
            this.channel_id = channel_id;
            this.message_cache = message_cache;
            this.admin = admin;
            this.join = join;
            this.left = left;
            this.join_voice = join_voice;
            this.left_voice = left_voice;
            this.move_voice = move_voice;
            this.voice_deafen = voice_deafen;
            this.voice_undeafen = voice_undeafen;
            this.voice_mute = voice_mute;
            this.voice_unmute = voice_unmute;
            this.ban = ban;
            this.unban = unban;
            this.kick = kick;
            this.edit = edit;
            this.delete = delete;
        }

        public String getChannel_id() {
            return channel_id;
        }

        public int getMessage_cache() {
            return message_cache;
        }

        public boolean isAdmin() {
            return admin;
        }

        public boolean isJoin() {
            return join;
        }

        public boolean isLeft() {
            return left;
        }

        public boolean isJoin_voice() {
            return join_voice;
        }

        public boolean isLeft_voice() {
            return left_voice;
        }

        public boolean isMove_voice() {
            return move_voice;
        }

        public boolean isVoice_deafen() {
            return voice_deafen;
        }

        public boolean isVoice_undeafen() {
            return voice_undeafen;
        }

        public boolean isVoice_mute() {
            return voice_mute;
        }

        public boolean isVoice_unmute() {
            return voice_unmute;
        }

        public boolean isBan() {
            return ban;
        }

        public boolean isUnban() {
            return unban;
        }

        public boolean isKick() {
            return kick;
        }

        public boolean isEdit() {
            return edit;
        }

        public boolean isDelete() {
            return delete;
        }
    }

    public static class Commands {
        private boolean music;
        private boolean info;
        private boolean utilities;

        public Commands() {}

        public Commands(boolean music, boolean info, boolean utilities) {
            this.music = music;
            this.info = info;
            this.utilities = utilities;
        }

        public boolean isMusic() {
            return music;
        }

        public boolean isInfo() {
            return info;
        }

        public boolean isUtilities() {
            return utilities;
        }
    }

}
