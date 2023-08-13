package fr.alkanife.alkabot.commands.admin;

import fr.alkanife.alkabot.command.admin.AbstractAdminCommand;
import fr.alkanife.alkabot.command.admin.AdminCommandExecution;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.util.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class StopCommand extends AbstractAdminCommand {

    public StopCommand(CommandManager commandManager) {
        super(commandManager);
    }

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
        return "Stops the bot";
    }

    @Override
    public boolean isDiscordOnly() {
        return false;
    }

    @Override
    public void execute(AdminCommandExecution execution) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(alkabot.t("notification.self.power_off.title"));
        embedBuilder.setThumbnail(alkabot.getJda().getSelfUser().getAvatarUrl());
        embedBuilder.setColor(Colors.ORANGE);

        if (execution.isFromDiscord()) {
            execution.messageReceivedEvent().getMessage().reply("Stopping (may take a moment!)").queue(message -> {
                embedBuilder.setDescription(alkabot.t("notification.self.power_off.description", execution.messageReceivedEvent().getAuthor().getAsMention()));
                alkabot.getNotificationManager().getSelfNotification().notifyShutdown(embedBuilder.build(), true);
            });
        } else {
            execution.reply("Stopping (may take a moment!)");
            embedBuilder.setDescription(alkabot.t("notification.self.power_off.description", "`Terminal`"));
            alkabot.getNotificationManager().getSelfNotification().notifyShutdown(embedBuilder.build(), true);
        }
    }
}
