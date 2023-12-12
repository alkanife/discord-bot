package dev.alkanife.alkabot.commands.music;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.music.AlkabotTrack;
import dev.alkanife.alkabot.music.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ShuffleCommand extends AbstractCommand {

    public ShuffleCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.shuffle.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isShuffle();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = alkabot.getMusicManager();
        List<AlkabotTrack> audioTracks = new ArrayList<>(musicManager.getTrackScheduler().getQueue());

        Collections.shuffle(audioTracks);

        BlockingQueue<AlkabotTrack> blockingQueue = new LinkedBlockingQueue<>();

        for (AlkabotTrack audioTrack : audioTracks)
            blockingQueue.offer(audioTrack);

        musicManager.getTrackScheduler().setQueue(blockingQueue);

        event.reply(Lang.t("command.music.shuffle.message").parseQueue(alkabot.getMusicManager()).getValue()).queue();
    }
}
