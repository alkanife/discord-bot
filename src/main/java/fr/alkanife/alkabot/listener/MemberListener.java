package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class MemberListener extends ListenerAdapter {

    private final Alkabot alkabot;

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent guildMemberJoinEvent) {
        // Welcome message
        boolean failedToWelcome = false;
        try {
            if (alkabot.getConfig().getWelcomeMessageConfig().isEnable()) {
                if (alkabot.getWelcomeMessageChannel() == null) {
                    failedToWelcome = true;
                    alkabot.getLogger().warn("Unable to find the role for the autorole");
                } else {
                    alkabot.getWelcomeMessageChannel().sendMessage(alkabot.tr("welcome.messages", guildMemberJoinEvent.getMember().getAsMention())).queue();
                }
            }
        } catch (Exception exception) {
            failedToWelcome = true;
            alkabot.getLogger().error("Failed to send welcome message!");
            exception.printStackTrace();
        }

        // Autorole
        boolean failedAutorole = false;
        try {
            if (alkabot.getConfig().getAutoRoleConfig().isEnable()) {
                if (alkabot.getAutoRole() == null) {
                    failedAutorole = true;
                    alkabot.getLogger().warn("Unable to find the text channel for the welcome message");
                } else {
                    guildMemberJoinEvent.getGuild().modifyMemberRoles(guildMemberJoinEvent.getMember(), alkabot.getAutoRole()).queue();
                }
            }
        } catch (Exception exception) {
            failedAutorole = true;
            alkabot.getLogger().error("Failed to auto-role a member!");
            exception.printStackTrace();
        }

        // Notif
        alkabot.getNotificationManager().getMemberNotification().notifyJoin(guildMemberJoinEvent, failedToWelcome, failedAutorole);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent guildMemberRemoveEvent) {
        if (!alkabot.getConfig().getNotifConfig().getMemberNotifConfig().isLeave() && !alkabot.getConfig().getNotifConfig().getModNotifConfig().isKick())
            return;

        try {
            alkabot.getNotificationManager().getMemberNotification().notifyLeaveOrKick(guildMemberRemoveEvent);
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify leave");
            exception.printStackTrace();
        }
    }

}
