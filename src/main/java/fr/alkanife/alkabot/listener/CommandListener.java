package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.SlashCommandHandler;
import fr.alkanife.alkabot.command.admin.AdminCommandExecution;
import fr.alkanife.alkabot.command.admin.AdminCommandHandler;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@AllArgsConstructor
public class CommandListener extends ListenerAdapter {

    private final Alkabot alkabot;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        new SlashCommandHandler(alkabot, event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // If in DM, handle admin commands
        if (!event.getChannelType().equals(ChannelType.PRIVATE))
            return;

        // Deny if not administrator
        if (!alkabot.getConfig().getAdminIds().contains(event.getAuthor().getId()))
            return;

        new AdminCommandHandler(alkabot, new AdminCommandExecution(alkabot, event.getMessage().getContentRaw().toLowerCase(Locale.ROOT), event));
    }

}
