package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.NotifUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent guildMemberJoinEvent) {
        // Welcome message
        boolean failedToWelcome = false;
        try {
            if (Alkabot.getConfig().getWelcome_message().isEnable()) {
                if (Alkabot.getWelcomeMessageChannel() == null) {
                    failedToWelcome = true;
                    Alkabot.getLogger().warn("Unable to find the role for the autorole");
                } else {
                    Alkabot.getWelcomeMessageChannel().sendMessage(Alkabot.tr("welcome.messages", guildMemberJoinEvent.getMember().getAsMention())).queue();
                }
            }
        } catch (Exception exception) {
            failedToWelcome = true;
            Alkabot.getLogger().error("Failed to send welcome message!");
            exception.printStackTrace();
        }

        // Autorole
        boolean failedAutorole = false;
        try {
            if (Alkabot.getConfig().getAuto_role().isEnable()) {
                if (Alkabot.getAutoRole() == null) {
                    failedAutorole = true;
                    Alkabot.getLogger().warn("Unable to find the text channel for the welcome message");
                } else {
                    guildMemberJoinEvent.getGuild().modifyMemberRoles(guildMemberJoinEvent.getMember(), Alkabot.getAutoRole()).queue();
                }
            }
        } catch (Exception exception) {
            failedAutorole = true;
            Alkabot.getLogger().error("Failed to auto-role a member!");
            exception.printStackTrace();
        }

        // Notif
        Alkabot.getNotificationManager().getMemberNotification().notifyJoin(guildMemberJoinEvent, failedToWelcome, failedAutorole);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent guildMemberRemoveEvent) {
        if (!Alkabot.getConfig().getNotifications().getMember().isLeave() && !Alkabot.getConfig().getNotifications().getModerator().isKick())
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

                if (kick && Alkabot.getConfig().getNotifications().getModerator().isKick()) {
                    embedBuilder.setTitle(Alkabot.t("notification.moderator.kick.title"));
                    embedBuilder.addField(Alkabot.t("notification.generic.member"), user.getAsTag() + " (" + user.getAsMention() + ")", true);
                    embedBuilder.addField(Alkabot.t("notification.generic.moderator"), NotifUtils.notifUser(latest.getUser()), true);
                    embedBuilder.addField(Alkabot.t("notification.generic.reason"), NotifUtils.notifValue(latest.getReason()), false);

                    Alkabot.getNotificationManager().getModeratorNotification().notifyKick(embedBuilder.build());
                } else {
                    embedBuilder.setTitle(Alkabot.t("notification.member.leave.title"));
                    embedBuilder.addField(Alkabot.t("notification.generic.member"), NotifUtils.notifUser(user), false);

                    Alkabot.getNotificationManager().getMemberNotification().notifyLeave(embedBuilder.build());
                }
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to notify leave");
            exception.printStackTrace();
        }
    }

}
