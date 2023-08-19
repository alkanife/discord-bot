package fr.alkanife.alkabot.commands.music;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ForceplayCommand extends AbstractCommand {

    public ForceplayCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "forceplay";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.forceplay.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isForceplay();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "query", Lang.get("command.music.forceplay.input.query"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().playCommand(event, "forceplay", 0, true);
    }
}
