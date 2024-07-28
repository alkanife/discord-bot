package dev.alkanife.alkabot.discord.event;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.command.SlashCommandHandler;
import dev.alkanife.alkabot.command.admin.AdminCommandHandler;
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
        if (!event.getChannelType().equals(ChannelType.PRIVATE))
            return;

        if (event.getAuthor().isBot())
            return;

        if (!alkabot.getConfig().getAdminIds().contains(event.getAuthor().getId()))
            return;

        new AdminCommandHandler(alkabot, event.getMessage().getContentRaw(), event);
    }

}
