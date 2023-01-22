package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.configuration.AbstractWorker;
import fr.alkanife.alkabot.configuration.ConfigurationInitializer;
import fr.alkanife.alkabot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        Alkabot.getLogger().info("Connected and ready!");

        try {
            ConfigurationInitializer configurationInitializer = new ConfigurationInitializer(false);
            if (configurationInitializer.getStatus() == AbstractWorker.Status.FAIL) {
                Alkabot.shutdown();
                return;
            }

            updateCommands(false);
            Alkabot.getMusicManager().initialize(false);

            Alkabot.getCommandManager().getTerminalCommandHandlerThread().start();

            Alkabot.getLogger().info("Ready!");

            //
            // Send notification
            //
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("notification.self.power_on.title"));
            embedBuilder.setColor(Colors.BIG_GREEN);
            embedBuilder.setThumbnail(Alkabot.tri("notification.self.power_on.ok_memes"));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Alkabot v")
                    .append(Alkabot.VERSION)
                    .append("\n\n");

            // Listing admins (5 maximum)
            List<String> admins = Alkabot.getConfig().getAdmin().getAdministrators_id();

            if (admins.size() > 0) {
                stringBuilder.append(Alkabot.t("notification.self.power_on.admins", "" + admins.size()));

                int i = 0;

                for (String admin : admins) {
                    if (i != 0)
                        stringBuilder.append(",");

                    stringBuilder.append(" <@").append(admin).append(">");

                    if (i == 4) {
                        stringBuilder.append("...");
                        break;
                    }

                    i++;
                }

                stringBuilder.append("\n\n");
            }

            stringBuilder.append(Alkabot.t("notification.self.power_on.help"));
            embedBuilder.setDescription(stringBuilder.toString());

            Alkabot.getNotificationManager().getSelfNotification().notifyAdmin(embedBuilder.build());
        } catch (Exception exception) {
            exception.printStackTrace();
            Alkabot.shutdown();
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
