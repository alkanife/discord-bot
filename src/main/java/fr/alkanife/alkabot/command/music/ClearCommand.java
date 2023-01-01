package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.concurrent.LinkedBlockingQueue;

public class ClearCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.clear.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isClear();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());
        Alkabot.getMusicManager().getTrackScheduler().setQueue(new LinkedBlockingQueue<>());
        event.reply(Alkabot.t("command.music.clear.done")).queue();
    }
}
