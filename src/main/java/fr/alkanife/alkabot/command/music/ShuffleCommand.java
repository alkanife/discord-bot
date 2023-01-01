package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ShuffleCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.shuffle.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isShuffle();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = Alkabot.getMusicManager();
        List<AlkabotTrack> audioTracks = new ArrayList<>(musicManager.getTrackScheduler().getQueue());

        Collections.shuffle(audioTracks);

        BlockingQueue<AlkabotTrack> blockingQueue = new LinkedBlockingQueue<>();

        for (AlkabotTrack audioTrack : audioTracks)
            blockingQueue.offer(audioTrack);

        musicManager.getTrackScheduler().setQueue(blockingQueue);

        event.reply(Alkabot.t("command.music.shuffle.done")).queue();
    }
}
