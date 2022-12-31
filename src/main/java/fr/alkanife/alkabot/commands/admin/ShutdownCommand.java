package fr.alkanife.alkabot.commands.admin;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.commands.AbstractAdminCommand;
import fr.alkanife.alkabot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownCommand extends AbstractAdminCommand {

    @Override
    public String getName() {
        return "shutdown";
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
    public void execute(MessageReceivedEvent event) {
        event.getMessage().reply("Stopping (may take a moment!)").queue(message -> {
            // Log
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-power-off-title"));
            embedBuilder.setThumbnail(message.getJDA().getSelfUser().getAvatarUrl());
            embedBuilder.setColor(Colors.ORANGE);
            embedBuilder.setDescription(event.getAuthor().getAsMention() + " " + Alkabot.t("logs-power-off-description"));
            Alkabot.getNotificationManager().getSelfNotification().notifyAdmin(embedBuilder.build());

            // Shutdown
            event.getJDA().shutdown();
        });
    }
}
