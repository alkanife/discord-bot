package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ModeratorListener extends ListenerAdapter {

    private final Alkabot alkabot;

    @Override
    public void onGuildBan(@NotNull GuildBanEvent guildBanEvent) {
        alkabot.getNotificationManager().getModeratorNotification().notifyBan(guildBanEvent);
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent guildUnbanEvent) {
        alkabot.getNotificationManager().getModeratorNotification().notifyUnban(guildUnbanEvent);
    }

    @Override
    public void onGuildMemberUpdateTimeOut(@NotNull GuildMemberUpdateTimeOutEvent guildMemberUpdateTimeOutEvent) {
        alkabot.getNotificationManager().getModeratorNotification().notifyTimeout(guildMemberUpdateTimeOutEvent);
    }

    @Override
    public void onGuildVoiceGuildDeafen(@NotNull GuildVoiceGuildDeafenEvent guildVoiceGuildDeafenEvent) {
        if (guildVoiceGuildDeafenEvent.isGuildDeafened())
            alkabot.getNotificationManager().getModeratorNotification().notifyDeafenMember(guildVoiceGuildDeafenEvent);
        else
            alkabot.getNotificationManager().getModeratorNotification().notifyUndeafenMember(guildVoiceGuildDeafenEvent);
    }

    @Override
    public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent guildVoiceGuildMuteEvent) {
        if (guildVoiceGuildMuteEvent.isGuildMuted())
            alkabot.getNotificationManager().getModeratorNotification().notifyMuteMember(guildVoiceGuildMuteEvent);
        else
            alkabot.getNotificationManager().getModeratorNotification().notifyUnmuteMember(guildVoiceGuildMuteEvent);
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent guildMemberUpdateNicknameEvent) {
        alkabot.getNotificationManager().getModeratorNotification().notifyChangeMemberNickname(guildMemberUpdateNicknameEvent);
    }

}
