package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.utils.Colors;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;

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

            updateCommands(false);
            alkabot.getMusicManager().initialize(false);

            alkabot.getCommandManager().getTerminalCommandHandlerThread().start();

            alkabot.getLogger().info("Loading complete! Guild: " + alkabot.getGuild().getName());
            alkabot.getLogger().info("To see a list of admin commands, type 'help'...");

            //
            // Send notification
            //
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(alkabot.t("notification.self.power_on.title"));
            embedBuilder.setColor(Colors.BIG_GREEN);
            embedBuilder.setThumbnail(alkabot.tri("notification.self.power_on.ok_memes"));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Alkabot v")
                    .append(alkabot.getVersion())
                    .append("\n\n");

            // Listing admins (5 maximum)
            List<String> admins = alkabot.getConfig().getAdminIds();

            if (admins.size() > 0) {
                stringBuilder.append(alkabot.t("notification.self.power_on.admins", "" + admins.size()));

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

            stringBuilder.append(alkabot.t("notification.self.power_on.help"));
            embedBuilder.setDescription(stringBuilder.toString());

            alkabot.getNotificationManager().getSelfNotification().notifyAdmin(embedBuilder.build());
        } catch (Exception exception) {
            exception.printStackTrace();
            alkabot.shutdown();
        }
    }

    public void updateCommands(boolean reload) {
        alkabot.getLogger().info((reload ? "(reload)" : "") + "Updating commands...");

        List<SlashCommandData> commands = new ArrayList<>();

        for (AbstractCommand abstractCommand : alkabot.getCommandManager().getCommands().values())
            if (abstractCommand.isEnabled())
                commands.add(abstractCommand.getCommandData());

        alkabot.getGuild().updateCommands().addCommands(commands).queue();
    }
}
