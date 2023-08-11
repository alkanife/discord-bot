package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.NotifUtils;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
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
            User user = guildMemberRemoveEvent.getUser();

            guildMemberRemoveEvent.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
                AuditLogEntry latest = auditLogEntries.get(0);
                boolean kick = false;

                if (latest.getType().equals(ActionType.KICK))
                    if (latest.getTargetId().equalsIgnoreCase(user.getId()))
                        kick = true;

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder = NotifUtils.addUserAvatar(embedBuilder, user);
                embedBuilder.setColor(Colors.RED);

                if (kick && alkabot.getConfig().getNotifConfig().getModNotifConfig().isKick()) {
                    embedBuilder.setTitle(alkabot.t("notification.moderator.kick.title"));
                    embedBuilder.addField(alkabot.t("notification.generic.member"), user.getName() + " (" + user.getAsMention() + ")", true);
                    embedBuilder.addField(alkabot.t("notification.generic.moderator"), NotifUtils.notifUser(latest.getUser()), true);
                    embedBuilder.addField(alkabot.t("notification.generic.reason"), NotifUtils.notifValue(latest.getReason()), false);

                    alkabot.getNotificationManager().getModeratorNotification().notifyKick(embedBuilder.build());
                } else {
                    embedBuilder.setTitle(alkabot.t("notification.member.leave.title"));
                    embedBuilder.addField(alkabot.t("notification.generic.member"), NotifUtils.notifUser(user), false);

                    alkabot.getNotificationManager().getMemberNotification().notifyLeave(embedBuilder.build());
                }
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to notify leave");
            exception.printStackTrace();
        }
    }

}
