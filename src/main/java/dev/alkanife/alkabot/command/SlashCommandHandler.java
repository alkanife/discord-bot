package dev.alkanife.alkabot.command;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Locale;

public class SlashCommandHandler extends AbstractCommandHandler {

    public SlashCommandHandler(Alkabot alkabot, SlashCommandInteractionEvent event) {
        super(alkabot);

        try {
            AbstractCommand abstractCommand = alkabot.getCommandManager().getCommand(event.getName().toLowerCase(Locale.ROOT));

            if (abstractCommand == null)
                return;

            alkabot.getLogger().debug("Invoking command '" + event.getFullCommandName() + "'");
            abstractCommand.execute(event);
            alkabot.getNotificationManager().getSelfNotification().notifyCommand(event, null);
        } catch (Exception exception) {
            event.reply(Lang.t("command.error").getValue()).queue();
            alkabot.getLogger().error("Failed to handle command '" + event.getFullCommandName() + "'", exception);
            alkabot.getLogger().debug(buildTrace(event));
            alkabot.getNotificationManager().getSelfNotification().notifyCommand(event, exception);
        }

    }

    private String buildTrace(SlashCommandInteractionEvent event) {
        StringBuilder stringBuilder = new StringBuilder("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv").append("\n");

        stringBuilder.append("* Event ID / Command ID -> ").append(event.getId()).append(" / ").append(event.getCommandId()).append("\n");
        stringBuilder.append("* Name -> ").append(event.getName()).append("\n");
        stringBuilder.append("* Full command name -> ").append(event.getFullCommandName()).append("\n");
        stringBuilder.append("* Command string -> ").append(event.getCommandString()).append("\n");
        stringBuilder.append("* Subcommand group -> ").append(event.getSubcommandGroup()).append("\n");
        stringBuilder.append("* Subcommand name -> ").append(event.getSubcommandName()).append("\n");
        stringBuilder.append("* Channel name -> ").append(event.getChannel().getName()).append("\n");
        stringBuilder.append("* Channel type -> ").append(event.getChannelType().name()).append("\n");

        stringBuilder.append("* Member -> ");
        if (event.getMember() == null)
            stringBuilder.append("not a member");
        else
            stringBuilder.append(event.getMember().getEffectiveName());

        stringBuilder.append("\n* Options -> ").append(event.getOptions().size()).append("\n");
        int i = 0;
        for (OptionMapping optionMapping : event.getOptions()) {
            if (i != 0)
                stringBuilder.append("- ").append(i).append(" ---------").append("\n");

            stringBuilder.append(" * Name ").append(optionMapping.getName()).append("\n");
            stringBuilder.append(" * Type -> ").append(optionMapping.getType()).append("\n");
            stringBuilder.append(" * Channel type -> ").append(optionMapping.getChannelType().name()).append("\n");
            stringBuilder.append(" * Option mapping -> ").append(optionMapping).append("\n");

            i++;
        }

        stringBuilder.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        return stringBuilder.toString();
    }
}
