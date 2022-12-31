package fr.alkanife.alkabot.command.utilities;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CopyCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "copy";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.copy.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getUtilities().isCopy();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("copy", Alkabot.t("copy-command-description"))
                .addOption(OptionType.STRING, "input", Alkabot.t("command.copy.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}
