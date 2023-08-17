package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.lang.Lang;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class MemberListener extends ListenerAdapter {

    private final Alkabot alkabot;

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        // Welcome message
        boolean failedToWelcome = false;
        try {
            if (alkabot.getConfig().getWelcomeMessageConfig().isEnable()) {
                if (alkabot.getWelcomeMessageChannel() == null) {
                    failedToWelcome = true;
                    alkabot.getLogger().warn("Unable to find the role for the autorole");
                } else {
                    alkabot.getWelcomeMessageChannel().sendMessage(
                            Lang.t("welcome_messages")
                                    .parseMemberMention(event.getMember())
                                    .parseMemberNames(event.getMember())
                                    .getValue()
                    ).queue();
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
                    event.getGuild().modifyMemberRoles(event.getMember(), alkabot.getAutoRole()).queue();
                }
            }
        } catch (Exception exception) {
            failedAutorole = true;
            alkabot.getLogger().error("Failed to auto-role a member!");
            exception.printStackTrace();
        }

        // Notif
        alkabot.getNotificationManager().getMemberNotification().notifyJoin(event, failedToWelcome, failedAutorole);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (!alkabot.getConfig().getNotifConfig().getMemberNotifConfig().isLeave() && !alkabot.getConfig().getNotifConfig().getModNotifConfig().isKick())
            return;

        try {
            event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry latest = auditLogEntries.get(0);
                boolean kick = false;

                if (latest.getType().equals(ActionType.KICK))
                    if (latest.getTargetId().equalsIgnoreCase(event.getUser().getId()))
                        kick = true;

                if (kick && alkabot.getConfig().getNotifConfig().getModNotifConfig().isKick()) {
                    alkabot.getNotificationManager().getModeratorNotification().notifyKick(event.getUser(), latest.getUser(), latest.getReason());
                } else {
                    alkabot.getNotificationManager().getMemberNotification().notifyLeave(event);
                }
            });

        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify leave", exception);
        }
    }
}
