package dev.alkanife.alkabot.commands.music;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class StopCommand extends AbstractCommand {

    public StopCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.stop.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isStop();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(Lang.get("command.music.stop.message")).queue();
        alkabot.getGuild().getAudioManager().closeAudioConnection();
        //Music.reset(); Disabled, it's not a bug it's a F E A T U R E
    }
}
