package fr.alkanife.alkabot.commands.admin;

import fr.alkanife.alkabot.command.admin.AbstractAdminCommand;
import fr.alkanife.alkabot.command.admin.AdminCommandExecution;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.EmbedBuilder;

public class StopbotCommand extends AbstractAdminCommand {

    public StopbotCommand(CommandManager commandManager) {
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
        embedBuilder.setTitle(
                Lang.t("notification.self.power_off.title")
                        .parseBotClientNames(alkabot)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );
        embedBuilder.setColor(Lang.getColor("notification.self.power_off.color"));
        embedBuilder.setThumbnail(
                Lang.t("notification.self.power_off.icon")
                        .parseBotAvatars(alkabot)
                        .parseGuildAvatar(alkabot.getGuild())
                        .getImage()
        );
        embedBuilder.setDescription(
                Lang.t("notification.self.power_off.description")
                        .parseAdmin(execution, "notification.self.power_off.admin")
                        .parseBot(alkabot)
                        .parseGuildName(alkabot.getGuild())
                        .getValue()
        );

        if (execution.isFromDiscord()) {
            execution.messageReceivedEvent().getMessage().reply("Stopping (may take a moment!)").queue(message -> alkabot.getNotificationManager().getSelfNotification().notifyShutdown(embedBuilder.build(), true));
        } else {
            execution.reply("Stopping (may take a moment!)");
            alkabot.getNotificationManager().getSelfNotification().notifyShutdown(embedBuilder.build(), true);
        }
    }
}
