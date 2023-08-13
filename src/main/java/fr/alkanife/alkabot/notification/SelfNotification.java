package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.configuration.json.notifications.SelfNotifConfig;
import fr.alkanife.alkabot.util.Colors;
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

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyShutdown(MessageEmbed messageEmbed, boolean shutdownAfter) {
        if (!selfNotifConfig.isAdmin() && shutdownAfter) {
            getNotificationManager().getAlkabot().shutdown();
            return;
        }

        if (!selfNotifConfig.isAdmin())
            return;

        TextChannel textChannel = getNotificationManager().getAlkabot().getJda().getTextChannelById(getNotificationChannel().getChannelID(getNotificationManager().getAlkabot().getConfig()));

        if (textChannel == null) {
            if (shutdownAfter)
                getNotificationManager().getAlkabot().shutdown();
            return;
        }

        try {
            textChannel.sendMessageEmbeds(messageEmbed).queue(message -> {
                if (shutdownAfter)
                    getNotificationManager().getAlkabot().shutdown();
            });
        } catch (Exception exception) {
            getNotificationManager().getAlkabot().getLogger().error("Failed to send the shutdown notification '" + messageEmbed.getTitle() + "':");
            exception.printStackTrace();
            if (shutdownAfter)
                getNotificationManager().getAlkabot().shutdown();
        }
    }

    public void notifyCommand(SlashCommandInteractionEvent event, boolean success) {
        if (!selfNotifConfig.isCommands())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getNotificationManager().getAlkabot().t("notification.self.command.title") + (success ? "" : (" " + getNotificationManager().getAlkabot().t("notification.self.command.fail_suffix"))));

        if (success)
            embed.setColor(Colors.CYAN);
        else
            embed.setColor(Colors.RED);

        if (event.getMember() != null)
            embed = addMemberAvatar(embed, event.getMember());

        embed.addField(getNotificationManager().getAlkabot().t("notification.generic.channel"), notifChannel(event.getChannel()), true);
        embed.addField(getNotificationManager().getAlkabot().t("notification.generic.member"), notifMember(event.getMember()), true);

        StringBuilder command = new StringBuilder();
        command.append(event.getFullCommandName());

        if (event.getOptions().size() > 0)
            command.append("\n\n");

        for (OptionMapping option : event.getOptions()) {
            command.append("`").append(option.getName()).append("`: ");

            OptionType optionType = option.getType();

            switch (optionType) {
                case USER -> command.append(notifUser(option.getAsUser()));
                case ROLE -> command.append(notifRole(option.getAsRole()));
                case CHANNEL -> command.append(notifGuildChannel(option.getAsChannel()));
                default -> command.append(option.getAsString());
            }

            command.append("\n");
        }

        embed.addField(getNotificationManager().getAlkabot().t("notification.self.command.command"), command.toString(), false);

        getNotificationManager().sendNotification(getNotificationChannel(), embed.build());
    }

}
