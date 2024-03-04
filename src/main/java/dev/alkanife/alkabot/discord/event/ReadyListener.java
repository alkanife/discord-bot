package dev.alkanife.alkabot.discord.event;

import dev.alkanife.alkabot.Alkabot;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ReadyListener extends ListenerAdapter {

    private final Alkabot alkabot;

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        try {
            if (!alkabot.getGuildManager().loadGuild()) {
                alkabot.shutdown();
                return;
            }

            alkabot.getAutoroleManager().loadRole();
            alkabot.getWelcomeMessageManager().loadChannel();
            alkabot.getCommandManager().updateCommandsToDiscord();

            alkabot.getMusicManager().initialize();

            alkabot.getCommandManager().getTerminalCommandHandlerThread().start();

            alkabot.getLogger().info("------------------");
            alkabot.getLogger().info("Guild: " + alkabot.getGuild().getName());
            alkabot.getLogger().info("To see a list of admin commands, type 'help'!");
            alkabot.getLogger().info("------------------");

            alkabot.getNotificationManager().getSelfNotification().notifyStart();
        } catch (Exception exception) {
            alkabot.getLogger().error("Fatal: an unexpected error prevented the bot to start", exception);
            alkabot.shutdown();
        }
    }
}
