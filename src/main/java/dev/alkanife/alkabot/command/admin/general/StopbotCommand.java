package dev.alkanife.alkabot.command.admin.general;

import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.command.admin.AdminCommand;
import dev.alkanife.alkabot.command.admin.AdminCommandTarget;
import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopbotCommand extends AdminCommand {

    public StopbotCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getUsage() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stop the bot";
    }

    @Override
    public AdminCommandTarget getCommandTarget() {
        return AdminCommandTarget.TERMINAL_AND_DISCORD;
    }

    @Override
    public void handleDiscord(String query, MessageReceivedEvent event) {
        event.getMessage().reply("OK bye :wave:").queue(message -> alkabot.getNotificationManager().getSelfNotification().notifyShutdown(getEmbed(event.getAuthor().getName(), event.getAuthor().getAsMention()).build(), true));
    }

    @Override
    public void handleTerminal(String query) {
        replyTerminal("Bye!");
        alkabot.getNotificationManager().getSelfNotification().notifyShutdown(getEmbed(Lang.get("notification.generic.cli.name"), Lang.get("notification.generic.cli.mention")).build(), true);
    }

    private EmbedBuilder getEmbed(String name, String mention) {
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
                        .parseBot(alkabot)
                        .parseGuildName(alkabot.getGuild())
                        .parseAdminNames(name, mention)
                        .getValue()
        );

        return embedBuilder;
    }
}
