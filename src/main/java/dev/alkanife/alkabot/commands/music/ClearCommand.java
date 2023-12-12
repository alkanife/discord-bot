package dev.alkanife.alkabot.commands.music;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.concurrent.LinkedBlockingQueue;

public class ClearCommand extends AbstractCommand {

    public ClearCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.clear.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isClear();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());
        alkabot.getMusicManager().getTrackScheduler().setQueue(new LinkedBlockingQueue<>());
        event.reply(Lang.get("command.music.clear.message")).queue();
    }
}
