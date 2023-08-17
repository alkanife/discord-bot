package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.test.Test;
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
            if (!alkabot.setupGuild()) {
                alkabot.shutdown();
                return;
            }

            alkabot.setupAutoRole();
            alkabot.setupWelComeChannel();
            alkabot.updateCommands();

            alkabot.getMusicManager().initialize();

            alkabot.getCommandManager().getTerminalCommandHandlerThread().start();

            alkabot.getLogger().info("Loading complete! Guild: " + alkabot.getGuild().getName());
            alkabot.getLogger().info("To see a list of admin commands, type 'help'...");

            alkabot.getNotificationManager().getSelfNotification().notifyStart();

            new Test(alkabot);
        } catch (Exception exception) {
            exception.printStackTrace();
            alkabot.shutdown();
        }
    }
}
