package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.configuration.json.notifications.SelfNotifConfig;
import fr.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SelfNotification extends AbstractNotification {

    private final SelfNotifConfig selfNotifConfig;

    public SelfNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.SELF);
        selfNotifConfig = notificationManager.getAlkabot().getConfig().getNotifConfig().getSelfNotifConfig();
    }

    public void notifyAdmin(MessageEmbed messageEmbed) {
        if (!selfNotifConfig.isAdmin())
            return;

        notificationManager.sendNotification(notificationChannel, messageEmbed);
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

        notifyAdmin(embedBuilder.build());
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
                Lang.t("notification.command." + index + ".title")
                        .parseMemberNames(event.getMember())
                        .parseGuildName(alkabot.getGuild())
                        .parseCommand(event)
                        .getValue()
        );
        embed.setColor(Lang.getColor("notification.command." + index + ".color"));
        embed.setThumbnail(
                Lang.t("notification.command.success.icon")
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .parseMemberAvatars(event.getMember())
                        .getImage()
        );
        embed.addField(NotificationUtils.createMessageChannelField("notification.command." + index, event.getChannel(), true));
        embed.addField(NotificationUtils.createMemberField("notification.command." + index, event.getMember(), true));
        embed.addField(
                Lang.t("notification.command." + index + ".command.title")
                        .getValue(),
                Lang.t("notification.command." + index + ".command.field")
                        .parseCommand(event)
                        .getValue(),
                false);

        if (!success) {
            embed.addField(
                    Lang.t("notification.command.fail.error.title")
                            .getValue(),
                    Lang.t("notification.command.fail.error.field")
                            .parseError(exception)
                            .getValue(),
                    false);
        }

        notificationManager.sendNotification(notificationChannel, embed.build());
    }
}
