package fr.alkanife.alkabot.command.admin;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractAdminCommand;
import fr.alkanife.alkabot.command.AdminCommandExecution;
import fr.alkanife.alkabot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class StopCommand extends AbstractAdminCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getUsage() {
        return getName();
    }

    @Override
    public String getDescription() {
        return "Shutdown the bot";
    }

    @Override
    public boolean isDiscordOnly() {
        return false;
    }

    @Override
    public void execute(AdminCommandExecution execution) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Alkabot.t("notification.self.power_off.title"));
        embedBuilder.setThumbnail(Alkabot.getJda().getSelfUser().getAvatarUrl());
        embedBuilder.setColor(Colors.ORANGE);

        if (execution.isFromDiscord()) {
            execution.getMessageReceivedEvent().getMessage().reply("Stopping (may take a moment!)").queue(message -> {
                embedBuilder.setDescription(Alkabot.t("notification.self.power_off.description", execution.getMessageReceivedEvent().getAuthor().getAsMention()));
                Alkabot.getNotificationManager().getSelfNotification().notifyShutdown(embedBuilder.build(), true);
            });
        } else {
            execution.reply("Stopping");
            embedBuilder.setDescription(Alkabot.t("notification.self.power_off.description", "`ADMIN`"));
            Alkabot.getNotificationManager().getSelfNotification().notifyShutdown(embedBuilder.build(), true);
        }
    }
}
