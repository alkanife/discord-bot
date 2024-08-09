package dev.alkanife.alkabot.discord.event;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.lang.Lang;
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
        boolean welcome = alkabot.getWelcomeMessageManager().welcomeMember(event.getMember());
        boolean autorole = alkabot.getAutoroleManager().applyRole(event.getMember());

        alkabot.getNotificationManager().getMemberNotification().notifyJoin(event, welcome, autorole);
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
