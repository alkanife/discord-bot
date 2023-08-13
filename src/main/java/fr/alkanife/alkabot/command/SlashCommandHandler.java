package fr.alkanife.alkabot.command;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Locale;

public class SlashCommandHandler extends AbstractCommandHandler {

    public SlashCommandHandler(Alkabot alkabot, SlashCommandInteractionEvent event) {
        super(alkabot);

        boolean success = true;

        try {
            AbstractCommand abstractCommand = alkabot.getCommandManager().getCommand(event.getName().toLowerCase(Locale.ROOT));

            if (abstractCommand == null)
                return;

            alkabot.verbose("Invoking command '" + event.getFullCommandName() + "'");
            abstractCommand.execute(event);
        } catch (Exception exception) {
            event.reply(alkabot.t("command.generic.error")).queue();
            alkabot.getLogger().error("Failed to handle a command:\n" + buildTrace(event));
            exception.printStackTrace();
            success = false;
        }

        alkabot.getNotificationManager().getSelfNotification().notifyCommand(event, success);
    }

    private String buildTrace(SlashCommandInteractionEvent event) {
        StringBuilder stringBuilder = new StringBuilder("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv").append("\n");

        stringBuilder.append("* getId() / getCommandId() -> ").append(event.getId()).append(" / ").append(event.getCommandId()).append("\n");
        stringBuilder.append("* getName() -> ").append(event.getName()).append("\n");
        stringBuilder.append("* getFullCommandName() -> ").append(event.getFullCommandName()).append("\n");
        stringBuilder.append("* getCommandString() -> ").append(event.getCommandString()).append("\n");
        stringBuilder.append("* getSubcommandGroup() -> ").append(event.getSubcommandGroup()).append("\n");
        stringBuilder.append("* getSubcommandName() -> ").append(event.getSubcommandName()).append("\n");
        stringBuilder.append("* getChannel().getName() -> ").append(event.getChannel().getName()).append("\n");
        stringBuilder.append("* getChannelType() -> ").append(event.getChannelType().name()).append("\n");

        stringBuilder.append("* getMember().getEffectiveName() -> ");
        if (event.getMember() == null)
            stringBuilder.append("null");
        else
            stringBuilder.append(event.getMember().getEffectiveName());

        stringBuilder.append("\n* getOptions() -> ").append(event.getOptions().size()).append("\n");
        int i = 0;
        for (OptionMapping optionMapping : event.getOptions()) {
            if (i != 0)
                stringBuilder.append("- ").append(i).append(" ---------").append("\n");

            stringBuilder.append(" * getName() ").append(optionMapping.getName()).append("\n");
            stringBuilder.append(" * getType() -> ").append(optionMapping.getType()).append("\n");
            stringBuilder.append(" * getChannelType() -> ").append(optionMapping.getChannelType().name()).append("\n");
            stringBuilder.append(" * optionMapping -> ").append(optionMapping).append("\n");

            i++;
        }

        stringBuilder.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        return stringBuilder.toString();
    }
}
