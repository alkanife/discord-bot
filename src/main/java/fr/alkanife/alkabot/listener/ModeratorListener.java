package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ModeratorListener extends ListenerAdapter {

    @Override
    public void onGuildBan(@NotNull GuildBanEvent guildBanEvent) {
        Alkabot.getNotificationManager().getModeratorNotification().notifyBan(guildBanEvent);
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent guildUnbanEvent) {
        Alkabot.getNotificationManager().getModeratorNotification().notifyUnban(guildUnbanEvent);
    }

    @Override
    public void onGuildMemberUpdateTimeOut(@NotNull GuildMemberUpdateTimeOutEvent guildMemberUpdateTimeOutEvent) {
        Alkabot.getNotificationManager().getModeratorNotification().notifyTimeout(guildMemberUpdateTimeOutEvent);
    }

    @Override
    public void onGuildVoiceGuildDeafen(@NotNull GuildVoiceGuildDeafenEvent guildVoiceGuildDeafenEvent) {
        if (guildVoiceGuildDeafenEvent.isGuildDeafened())
            Alkabot.getNotificationManager().getModeratorNotification().notifyDeafenMember(guildVoiceGuildDeafenEvent);
        else
            Alkabot.getNotificationManager().getModeratorNotification().notifyUndeafenMember(guildVoiceGuildDeafenEvent);
    }

    @Override
    public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent guildVoiceGuildMuteEvent) {
        if (guildVoiceGuildMuteEvent.isGuildMuted())
            Alkabot.getNotificationManager().getModeratorNotification().notifyMuteMember(guildVoiceGuildMuteEvent);
        else
            Alkabot.getNotificationManager().getModeratorNotification().notifyUnmuteMember(guildVoiceGuildMuteEvent);
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent guildMemberUpdateNicknameEvent) {
        Alkabot.getNotificationManager().getModeratorNotification().notifyChangeMemberNickname(guildMemberUpdateNicknameEvent);
    }

}
