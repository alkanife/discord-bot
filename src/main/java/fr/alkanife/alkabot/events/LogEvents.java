package fr.alkanife.alkabot.events;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.LoggedMessage;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogEvents extends ListenerAdapter {

    public static List<LoggedMessage> sentMessages = new ArrayList<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent messageReceivedEvent) {
        // Do nothing if message logs disabled
        if (!Alkabot.getConfig().getLogs().isEdit() || !Alkabot.getConfig().getLogs().isDelete())
            return;

        // Do nothing if in a DM
        if (messageReceivedEvent.getChannelType().equals(ChannelType.PRIVATE))
            return;

        // Do nothing if bot
        if (messageReceivedEvent.getAuthor().isBot())
            return;

        // Cache system, delete the oldest message if the size is > than allowed
        if (sentMessages.size() >= Alkabot.getConfig().getLogs().getMessage_cache())
            sentMessages.remove(0);

        sentMessages.add(new LoggedMessage(messageReceivedEvent.getMessageIdLong(), messageReceivedEvent.getMessage().getContentDisplay(), messageReceivedEvent.getAuthor().getIdLong()));

    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent messageUpdateEvent) {
        if (!Alkabot.getConfig().getLogs().isEdit())
            return;

        if (messageUpdateEvent.getAuthor().isBot())
            return;

        String beforeMessage = null;

        for (LoggedMessage sentMessage : sentMessages)
            if (sentMessage.getId() == messageUpdateEvent.getMessageIdLong())
                beforeMessage = StringUtils.limitString(sentMessage.getContent(), 1000);

        if (beforeMessage == null)
            beforeMessage = Alkabot.t("logs-unknown");

        User user = messageUpdateEvent.getAuthor();
        MessageChannelUnion messageChannelUnion = messageUpdateEvent.getChannel();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Alkabot.t("logs-message-edited"));
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setColor(Colors.CYAN);
        embedBuilder.setDescription("[" + Alkabot.t("logs-message") + "](" + messageUpdateEvent.getMessage().getJumpUrl() + ")");
        embedBuilder.addField(Alkabot.t("logs-member"), user.getAsTag() + " (" + user.getAsMention() + ")", true);
        embedBuilder.addField(Alkabot.t("logs-channel"), messageChannelUnion.getName() +  " (" + messageChannelUnion.getAsMention() + ")", true);
        embedBuilder.addField(Alkabot.t("logs-message-edited-before"), beforeMessage, false);

        String after = StringUtils.limitString(messageUpdateEvent.getMessage().getContentDisplay(), 1000);

        if (after.equals(""))
            after = Alkabot.t("logs-message-edited-after-nomessage");

        embedBuilder.addField(Alkabot.t("logs-message-edited-after"), after, false);

        Alkabot.discordLog(embedBuilder.build());
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent messageDeleteEvent) {
        if (!Alkabot.getConfig().getLogs().isDelete())
            return;

        MessageChannelUnion messageChannelUnion = messageDeleteEvent.getChannel();

        LoggedMessage loggedMessage = null;

        for (LoggedMessage sentMessage : sentMessages)
            if (sentMessage.getId() == messageDeleteEvent.getMessageIdLong())
                loggedMessage = sentMessage;


        if (loggedMessage == null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-message-deleted"));
            embedBuilder.setColor(Colors.RED);
            embedBuilder.addField(Alkabot.t("logs-message-deleted-author"), Alkabot.t("logs-unknown"), true);
            embedBuilder.addField(Alkabot.t("logs-channel"), messageChannelUnion.getName() +  " (" + messageChannelUnion.getAsMention() + ")", true);
            embedBuilder.addField(Alkabot.t("logs-message"), Alkabot.t("logs-unknown"), false);
            Alkabot.discordLog(embedBuilder.build());
        } else {
            Member member = Alkabot.getGuild().getMemberById(loggedMessage.getAuthor());

            String author = loggedMessage.getAuthor() + " (" + Alkabot.t("logs-message-deleted-author-notamember") + ")";

            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (member != null) {
                author = member.getUser().getAsTag() + " (" + member.getAsMention() + ")";
                embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
            }

            embedBuilder.setTitle(Alkabot.t("logs-message-deleted"));
            embedBuilder.setColor(Colors.RED);
            embedBuilder.addField(Alkabot.t("logs-message-deleted-author"), author, true);
            embedBuilder.addField(Alkabot.t("logs-channel"), messageChannelUnion.getName() +  " (" + messageChannelUnion.getAsMention() + ")", true);
            embedBuilder.addField(Alkabot.t("logs-message"), StringUtils.limitString(loggedMessage.getContent().equals("") ? Alkabot.t("logs-message-edited-after-nomessage") : loggedMessage.getContent(), 1000), false);
            Alkabot.discordLog(embedBuilder.build());
        }
    }

    @Override
    public void onGuildVoiceGuildDeafen(@NotNull GuildVoiceGuildDeafenEvent guildVoiceGuildDeafenEvent) {
        if (guildVoiceGuildDeafenEvent.isGuildDeafened()) {
            if (!Alkabot.getConfig().getLogs().isVoice_deafen())
                return;

            User target = guildVoiceGuildDeafenEvent.getMember().getUser();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-voice-deafen"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.RED);
            embedBuilder.addField(Alkabot.t("logs-member"), target.getAsTag() + " (" + target.getAsMention() + ")", true);

            GuildVoiceState guildVoiceState = guildVoiceGuildDeafenEvent.getMember().getVoiceState();
            if (guildVoiceState != null)
                if (guildVoiceState.getChannel() != null)
                    embedBuilder.addField(Alkabot.t("logs-channel"), guildVoiceState.getChannel().getName() + " (" + guildVoiceState.getChannel().getAsMention() + ")", true);

            Alkabot.discordLog(embedBuilder.build());
        } else {
            if (!Alkabot.getConfig().getLogs().isVoice_undeafen())
                return;

            User target = guildVoiceGuildDeafenEvent.getMember().getUser();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-voice-undeafen"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.CYAN);
            embedBuilder.addField(Alkabot.t("logs-member"), target.getAsTag() + " (" + target.getAsMention() + ")", true);

            GuildVoiceState guildVoiceState = guildVoiceGuildDeafenEvent.getMember().getVoiceState();
            if (guildVoiceState != null)
                if (guildVoiceState.getChannel() != null)
                    embedBuilder.addField(Alkabot.t("logs-channel"), guildVoiceState.getChannel().getName() + " (" + guildVoiceState.getChannel().getAsMention() + ")", true);

            Alkabot.discordLog(embedBuilder.build());
        }
    }

    @Override
    public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent guildVoiceGuildMuteEvent) {
        if (guildVoiceGuildMuteEvent.isGuildMuted()) {
            if (!Alkabot.getConfig().getLogs().isVoice_mute())
                return;

            User target = guildVoiceGuildMuteEvent.getMember().getUser();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-voice-muted"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.RED);
            embedBuilder.addField(Alkabot.t("logs-member"), target.getAsTag() + " (" + target.getAsMention() + ")", true);

            GuildVoiceState guildVoiceState = guildVoiceGuildMuteEvent.getMember().getVoiceState();
            if (guildVoiceState != null)
                if (guildVoiceState.getChannel() != null)
                    embedBuilder.addField(Alkabot.t("logs-channel"), guildVoiceState.getChannel().getName() + " (" + guildVoiceState.getChannel().getAsMention() + ")", true);

            Alkabot.discordLog(embedBuilder.build());
        } else {
            if (!Alkabot.getConfig().getLogs().isVoice_unmute())
                return;

            User target = guildVoiceGuildMuteEvent.getMember().getUser();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-voice-unmuted"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.CYAN);
            embedBuilder.addField(Alkabot.t("logs-member"), target.getAsTag() + " (" + target.getAsMention() + ")", true);

            GuildVoiceState guildVoiceState = guildVoiceGuildMuteEvent.getMember().getVoiceState();
            if (guildVoiceState != null)
                if (guildVoiceState.getChannel() != null)
                    embedBuilder.addField(Alkabot.t("logs-channel"), guildVoiceState.getChannel().getName() + " (" + guildVoiceState.getChannel().getAsMention() + ")", true);

            Alkabot.discordLog(embedBuilder.build());
        }
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent guildVoiceUpdateEvent) {
        if (guildVoiceUpdateEvent.getMember().getUser().isBot())
            return;

        AudioChannelUnion joinChannelUnion = guildVoiceUpdateEvent.getChannelJoined();
        AudioChannelUnion leftChannelUnion = guildVoiceUpdateEvent.getChannelLeft();

        if (joinChannelUnion == null && leftChannelUnion == null)
            return;

        User target = guildVoiceUpdateEvent.getMember().getUser();

        if (joinChannelUnion == null) { // Left
            if (!Alkabot.getConfig().getLogs().isLeft_voice())
                return;

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-voice-left"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.CYAN);
            embedBuilder.addField(Alkabot.t("logs-member"), target.getAsTag() + " (" + target.getAsMention() + ")", true);
            embedBuilder.addField(Alkabot.t("logs-channel"), leftChannelUnion.getName() + " (" + leftChannelUnion.getAsMention() + ")", true);
            Alkabot.discordLog(embedBuilder.build());
            return;
        }

        if (leftChannelUnion == null) { // Join
            if (!Alkabot.getConfig().getLogs().isJoin_voice())
                return;

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-voice-join"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.CYAN);
            embedBuilder.addField(Alkabot.t("logs-member"), target.getAsTag() + " (" + target.getAsMention() + ")", true);
            embedBuilder.addField(Alkabot.t("logs-channel"), joinChannelUnion.getName() + " (" + joinChannelUnion.getAsMention() + ")", true);
            Alkabot.discordLog(embedBuilder.build());
            return;
        }

        // Move
        if (!Alkabot.getConfig().getLogs().isMove_voice())
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Alkabot.t("logs-voice-move"));
        embedBuilder.setThumbnail(target.getAvatarUrl());
        embedBuilder.setColor(Colors.CYAN);
        embedBuilder.addField(Alkabot.t("logs-member"), target.getAsTag() + " (" + target.getAsMention() + ")", false);
        embedBuilder.addField(Alkabot.t("logs-old-channel"), leftChannelUnion.getName() + " (" + leftChannelUnion.getAsMention() + ")", true);
        embedBuilder.addField(Alkabot.t("logs-new-channel"), joinChannelUnion.getName() + " (" + joinChannelUnion.getAsMention() + ")", true);
        Alkabot.discordLog(embedBuilder.build());
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent guildBanEvent) {
        if (!Alkabot.getConfig().getLogs().isBan())
            return;

        guildBanEvent.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
            AuditLogEntry logEntry = null;

            for (int i = 0; i < 5; i++) {
                AuditLogEntry auditLogEntry = auditLogEntries.get(i);

                if (!auditLogEntry.getType().equals(ActionType.BAN))
                    continue;

                if (!auditLogEntry.getTargetId().equalsIgnoreCase(guildBanEvent.getUser().getId()))
                    continue;

                logEntry = auditLogEntry;
            }

            User target = guildBanEvent.getUser();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-user-banned"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.RED);
            embedBuilder.addField(Alkabot.t("logs-user"), target.getAsTag() + " (" + target.getAsMention() + ")", true);

            String admin = Alkabot.t("logs-unknown");
            String reason = admin;
            if (logEntry != null) {
                User user = logEntry.getUser();
                admin = user == null ? Alkabot.t("logs-unknown") : (user.getAsTag() + " (" + user.getAsMention() + ")");
                reason = logEntry.getReason() == null ? Alkabot.t("logs-none") : logEntry.getReason();
            }

            embedBuilder.addField(Alkabot.t("logs-admin"), admin, true);
            embedBuilder.addField(Alkabot.t("logs-reason"), reason, false);

            Alkabot.discordLog(embedBuilder.build());
        });
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent guildUnbanEvent) {
        if (!Alkabot.getConfig().getLogs().isUnban())
            return;

        guildUnbanEvent.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
            AuditLogEntry logEntry = null;

            for (int i = 0; i < 5; i++) {
                AuditLogEntry auditLogEntry = auditLogEntries.get(i);

                if (!auditLogEntry.getType().equals(ActionType.UNBAN))
                    continue;

                if (!auditLogEntry.getTargetId().equalsIgnoreCase(guildUnbanEvent.getUser().getId()))
                    continue;

                logEntry = auditLogEntry;
            }

            User target = guildUnbanEvent.getUser();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-user-unbanned"));
            embedBuilder.setThumbnail(target.getAvatarUrl());
            embedBuilder.setColor(Colors.CYAN);
            embedBuilder.addField(Alkabot.t("logs-user"), target.getAsTag() + " (" + target.getAsMention() + ")", true);

            String admin = Alkabot.t("logs-unknown");
            if (logEntry != null) {
                User user = logEntry.getUser();
                admin = user == null ? Alkabot.t("logs-unknown") : (user.getAsTag() + " (" + user.getAsMention() + ")");
            }

            embedBuilder.addField(Alkabot.t("logs-admin"), admin, true);

            Alkabot.discordLog(embedBuilder.build());
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent guildMemberJoinEvent) {
        // Welcome message
        boolean failedToWelcome = false;
        try {
            if (Alkabot.getConfig().getWelcome_message().isEnable()) {
                String[] welcomeMessages = Alkabot.t("welcome-messages", guildMemberJoinEvent.getMember().getAsMention()).split("\n");
                int random = new Random().nextInt(welcomeMessages.length);
                String message = welcomeMessages[random];

                TextChannel textChannel = Alkabot.getGuild().getTextChannelById(Alkabot.getConfig().getWelcome_message().getChannel_id());
                if (textChannel == null)
                    failedToWelcome = true;
                else
                    textChannel.sendMessage(message).queue();
            }
        } catch (Exception exception) {
            failedToWelcome = true;
            exception.printStackTrace();
        }

        // Auto-role
        boolean failedAutorole = false;
        try {
            if (Alkabot.getConfig().getAuto_role().isEnable()) {
                Role role = Alkabot.getGuild().getRoleById(Alkabot.getConfig().getAuto_role().getRole_id());
                if (role == null)
                    failedAutorole = true;
                else
                    guildMemberJoinEvent.getGuild().modifyMemberRoles(guildMemberJoinEvent.getMember(), role).queue();
            }
        } catch (Exception exception) {
            failedAutorole = true;
            exception.printStackTrace();
        }

        // Log
        if (Alkabot.getConfig().getLogs().isJoin()) {
            User user = guildMemberJoinEvent.getMember().getUser();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-member-join"));
            embedBuilder.setThumbnail(user.getAvatarUrl());
            embedBuilder.setColor(Colors.GREEN);
            embedBuilder.addField(Alkabot.t("logs-member"), user.getAsTag() + " (" + user.getAsMention() + ")", true);

            if (failedAutorole || failedToWelcome) {
                StringBuilder stringBuilder = new StringBuilder();
                if (failedToWelcome)
                    stringBuilder.append(Alkabot.t("logs-member-join-failed-welcome"));
                if (failedAutorole)
                    stringBuilder.append("\n").append(Alkabot.t("logs-member-join-failed-autorole"));
                embedBuilder.setDescription(stringBuilder.toString());
            }

            Alkabot.discordLog(embedBuilder.build());
        } else {
            if (failedToWelcome)
                Alkabot.getLogger().warn("Unable to find the text channel for the welcome message");
            if (failedAutorole)
                Alkabot.getLogger().warn("Unable to find the role for the autorole");
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent guildMemberRemoveEvent) {
        try {
            User user = guildMemberRemoveEvent.getUser();

            guildMemberRemoveEvent.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry latest = auditLogEntries.get(0);
                boolean kick = false;

                if (latest.getType().equals(ActionType.KICK))
                    if (latest.getTargetId().equalsIgnoreCase(user.getId()))
                        kick = true;

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setThumbnail(user.getAvatarUrl());
                embedBuilder.setColor(Colors.RED);
                if (kick && Alkabot.getConfig().getLogs().isKick()) {
                    embedBuilder.setTitle(Alkabot.t("logs-member-kicked"));
                    embedBuilder.addField(Alkabot.t("logs-member"), user.getAsTag() + " (" + user.getAsMention() + ")", true);
                    User admin = latest.getUser();
                    embedBuilder.addField(Alkabot.t("logs-admin"), admin == null ? Alkabot.t("logs-unknown") : (admin.getAsTag() + " (" + admin.getAsMention() + ")"), true);
                    embedBuilder.addField(Alkabot.t("logs-reason"), latest.getReason() == null ? Alkabot.t("logs-none") : latest.getReason(), false);
                } else {
                    embedBuilder.setTitle(Alkabot.t("logs-member-left"));
                    embedBuilder.addField(Alkabot.t("logs-member"), user.getAsTag() + " (" + user.getAsMention() + ")", false);
                }

                if (Alkabot.getConfig().getLogs().isLeft())
                    Alkabot.discordLog(embedBuilder.build());
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
