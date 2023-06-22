package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.JSONNotificationsSelf;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.NotifUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SelfNotification extends AbstractNotification {

    private final JSONNotificationsSelf jsonNotificationsSelf;

    public SelfNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.SELF);
        jsonNotificationsSelf = Alkabot.getConfig().getNotifications().getSelf();
    }

    public void notifyAdmin(MessageEmbed messageEmbed) {
        if (!jsonNotificationsSelf.isAdmin())
            return;

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

    public void notifyShutdown(MessageEmbed messageEmbed, boolean shutdownAfter) {
        if (!jsonNotificationsSelf.isAdmin() && shutdownAfter) {
            Alkabot.shutdown();
            return;
        }

        if (!jsonNotificationsSelf.isAdmin())
            return;

        TextChannel textChannel = Alkabot.getJda().getTextChannelById(getNotificationChannel().getChannelID());

        if (textChannel == null) {
            if (shutdownAfter)
                Alkabot.shutdown();
            return;
        }

        try {
            textChannel.sendMessageEmbeds(messageEmbed).queue(message -> {
                if (shutdownAfter)
                    Alkabot.shutdown();
            });
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to send the shutdown notification '" + messageEmbed.getTitle() + "':");
            exception.printStackTrace();
            if (shutdownAfter)
                Alkabot.shutdown();
        }
    }

    public void notifyCommand(SlashCommandInteractionEvent event, boolean success) {
        if (!jsonNotificationsSelf.isCommands())
            return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(Alkabot.t("notification.self.command.title") + (success ? "" : (" " + Alkabot.t("notification.self.command.fail_suffix"))));

        if (success)
            embed.setColor(Colors.CYAN);
        else
            embed.setColor(Colors.RED);

        if (event.getMember() != null)
            embed = NotifUtils.addMemberAvatar(embed, event.getMember());

        embed.addField(Alkabot.t("notification.generic.channel"), NotifUtils.notifChannel(event.getChannel()), true);
        embed.addField(Alkabot.t("notification.generic.member"), NotifUtils.notifMember(event.getMember()), true);

        StringBuilder command = new StringBuilder();
        command.append(event.getFullCommandName());

        if (event.getOptions().size() > 0)
            command.append("\n\n");

        for (OptionMapping option : event.getOptions()) {
            command.append("`").append(option.getName()).append("`: ");

            OptionType optionType = option.getType();

            switch (optionType) {
                case USER -> command.append(NotifUtils.notifUser(option.getAsUser()));
                case ROLE -> command.append(NotifUtils.notifRole(option.getAsRole()));
                case CHANNEL -> command.append(NotifUtils.notifGuildChannel(option.getAsChannel()));
                default -> command.append(option.getAsString());
            }

            command.append("\n");
        }

        embed.addField(Alkabot.t("notification.self.command.command"), command.toString(), false);

        getNotificationManager().sendNotification(getNotificationChannel(), embed.build());
    }

}
