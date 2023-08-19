package fr.alkanife.alkabot.commands.music;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class DestroyCommand extends AbstractCommand {

    public DestroyCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "destroy";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.destroy.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isDestroy();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());
        alkabot.getMusicManager().reset();
        event.reply(Lang.get("command.music.destroy.message")).queue();
    }
}
