package dev.alkanife.alkabot.notification.notifier;

import dev.alkanife.alkabot.configuration.json.notifications.SelfNotifConfig;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.notification.NotificationChannel;
import dev.alkanife.alkabot.notification.NotificationManager;
import dev.alkanife.alkabot.notification.NotificationUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

public class SelfNotifier extends Notifier {

    private final SelfNotifConfig selfNotifConfig;

    public SelfNotifier(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.SELF);
        selfNotifConfig = notificationManager.getAlkabot().getConfig().getNotifConfig().getSelfNotifConfig();
    }

    public void notifyAdmin(MessageCreateData messageCreateData) {
        if (!selfNotifConfig.isAdmin())
            return;

        notificationManager.sendNotification(notificationChannel, messageCreateData);
    }

    public void notifyStart() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                Lang.t("notification.self.power_on.title")
                        .parseBotClientNames(alkabot)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embedBuilder.setColor(Lang.getColor("notification.self.power_on.color"));
        embedBuilder.setThumbnail(
                Lang.t("notification.self.power_on.icon")
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embedBuilder.setDescription(
                Lang.t("notification.self.power_on.description")
                        .parseBot(alkabot)
                        .parseAdmins(alkabot)
                        .getValue()
        );

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();

        if (alkabot.isSnapshotBuild()) {
            messageCreateBuilder.setEmbeds(embedBuilder.build(), new EmbedBuilder()
                    .setDescription("### This version of Alkabot is an experiment and some features are not finished, take extra care!\n\nAlkabot" + alkabot.getVersion() + ", build @ " + alkabot.getBuild())
                    .setColor(Color.decode("#a1400b"))
                    .build());
        } else {
            messageCreateBuilder.setEmbeds(embedBuilder.build());
        }

        notifyAdmin(messageCreateBuilder.build());
    }

    public void notifyShutdown(MessageEmbed messageEmbed, boolean shutdownAfter) {
        if (!selfNotifConfig.isAdmin() && shutdownAfter) {
            alkabot.shutdown();
            return;
        }

        if (!selfNotifConfig.isAdmin())
            return;

        TextChannel textChannel = alkabot.getJda().getTextChannelById(notificationChannel.getChannelID(alkabot.getConfig()));

        if (textChannel == null) {
            if (shutdownAfter)
                alkabot.shutdown();
            return;
        }

        try {
            textChannel.sendMessageEmbeds(messageEmbed).queue(message -> {
                if (shutdownAfter)
                    alkabot.shutdown();
            });
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to send the shutdown notification '" + messageEmbed.getTitle() + "':");
            exception.printStackTrace();
            if (shutdownAfter)
                alkabot.shutdown();
        }
    }

    public void notifyCommand(SlashCommandInteractionEvent event, Exception exception) {
        if (!selfNotifConfig.isCommands())
            return;

        boolean success = exception == null;
        String index = success ? "success" : "fail";

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(
                Lang.t("notification.self.command." + index + ".title")
                        .parseMemberNames(event.getMember())
                        .parseGuildName(alkabot.getGuild())
                        .parseCommand(event)
                        .getValue()
        );
        embed.setColor(Lang.getColor("notification.self.command." + index + ".color"));
        embed.setThumbnail(
                Lang.t("notification.self.command.success.icon")
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .parseMemberAvatars(event.getMember())
                        .getImage()
        );
        embed.addField(NotificationUtils.createChannelField("notification.self.command." + index, event.getChannel(), true));
        embed.addField(NotificationUtils.createMemberField("notification.self.command." + index, event.getMember(), true));
        embed.addField(
                Lang.t("notification.self.command." + index + ".command.title")
                        .getValue(),
                Lang.t("notification.self.command." + index + ".command.field")
                        .parseCommand(event)
                        .getValue(),
                false);

        if (!success) {
            embed.addField(
                    Lang.t("notification.self.command.fail.error.title")
                            .getValue(),
                    Lang.t("notification.self.command.fail.error.field")
                            .parseError(exception)
                            .getValue(),
                    false);
        }

        notificationManager.sendNotification(notificationChannel, new MessageCreateBuilder().addEmbeds(embed.build()).build());
    }
}
