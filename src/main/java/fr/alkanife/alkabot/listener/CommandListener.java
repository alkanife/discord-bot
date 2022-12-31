package fr.alkanife.alkabot.listener;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (Alkabot.getConfig().getAdmin().isAdmin_only()) {
            Member member = event.getMember();

            if (member != null)
                if (!Alkabot.getConfig().getAdmin().getAdministrators_id().contains(member.getId()))
                    return;
        }

        Alkabot.getCommandManager().handleSlash(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // If in DM, handle admin commands
        if (!event.getChannelType().equals(ChannelType.PRIVATE))
            return;

        // Deny if not administrator
        if (!Alkabot.getConfig().getAdmin().getAdministrators_id().contains(event.getAuthor().getId()))
            return;

        Alkabot.getCommandManager().handleAdmin(event);
    }

}
