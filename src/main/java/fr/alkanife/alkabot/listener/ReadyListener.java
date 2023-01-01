package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.configuration.AbstractWorker;
import fr.alkanife.alkabot.configuration.ConfigurationInitializer;
import fr.alkanife.alkabot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        Alkabot.getLogger().info("Connected!");

        try {
            ConfigurationInitializer configurationInitializer = new ConfigurationInitializer(false);
            if (configurationInitializer.getStatus() == AbstractWorker.Status.FAIL) {
                readyEvent.getJDA().shutdownNow();
                System.exit(0);
                return;
            }

            updateCommands(false);
            Alkabot.getMusicManager().initialize();

            Alkabot.getCommandManager().getTerminalCommandHandlerThread().start();

            Alkabot.getLogger().info("Ready!");

            //
            // LOG SUCCESSFUL CONNECTION
            //
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("notification.self.power_on.title"));
            embedBuilder.setColor(Colors.BIG_GREEN);

            embedBuilder.setThumbnail(Alkabot.tr("notification.self.power_on.ok_memes"));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Alkabot v")
                    .append(Alkabot.VERSION)
                    .append("\n\n");

            if (Alkabot.getConfig().getAdmin().getAdministrators_id().size() > 0) {
                stringBuilder.append(Alkabot.t("notification.self.power_on.admins"));
                for (String admin : Alkabot.getConfig().getAdmin().getAdministrators_id())
                    stringBuilder.append(" <@").append(admin).append(">");
                stringBuilder.append("\n\n");
            }

            stringBuilder.append(Alkabot.t("notification.self.power_on.help"));
            embedBuilder.setDescription(stringBuilder.toString());

            Alkabot.getNotificationManager().getSelfNotification().notifyAdmin(embedBuilder.build());
        } catch (Exception exception) {
            exception.printStackTrace();
            readyEvent.getJDA().shutdownNow();
            System.exit(0);
        }
    }

    public void updateCommands(boolean reload) {
        Alkabot.getLogger().info((reload ? "(reload)" : "") + "Updating commands...");

        List<SlashCommandData> commands = new ArrayList<>();

        for (AbstractCommand abstractCommand : Alkabot.getCommandManager().getCommands())
            if (abstractCommand.isEnabled())
                commands.add(abstractCommand.getCommandData());

        Alkabot.getGuild().updateCommands().addCommands(commands).queue();
    }

}
