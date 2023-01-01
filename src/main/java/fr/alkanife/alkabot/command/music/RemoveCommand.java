package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RemoveCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.remove.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isRemove();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "input", Alkabot.t("command.music.remove.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = Alkabot.getMusicManager();

        if (musicManager.getPlayer().getPlayingTrack() == null) {
            event.reply(Alkabot.t("command.music.generic.not_playing")).queue();
            return;
        }

        OptionMapping removeOption = event.getOption("input");
        long remove = 1;
        if (removeOption != null) {
            remove = removeOption.getAsLong();

            if (remove > musicManager.getTrackScheduler().getQueue().size()) {
                event.reply(Alkabot.t("command.music.generic.not_enough")).queue();
                return;
            }
        }

        List<AlkabotTrack> aTracks = new ArrayList<>(musicManager.getTrackScheduler().getQueue());

        try {
            AlkabotTrack t = aTracks.get(((int) remove) - 1);

            aTracks.remove(t);

            BlockingQueue<AlkabotTrack> newBlockingQueue = new LinkedBlockingQueue<>();

            for (AlkabotTrack audioTrack : aTracks)
                newBlockingQueue.offer(audioTrack);

            musicManager.getTrackScheduler().setQueue(newBlockingQueue);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("command.music.remove.title"));
            embedBuilder.setDescription("[" + t.getTitle() + "](" + t.getUrl() + ")"
                    + " " + Alkabot.t("command.music.generic.by") + " [" + t.getArtists() + "](" + t.getUrl() + ")");
            embedBuilder.setThumbnail(t.getThumbUrl());

            event.replyEmbeds(embedBuilder.build()).queue();
        } catch (Exception e) {
            Alkabot.getLogger().error("Failed to remove a music from the queue:");
            e.printStackTrace();
            event.reply(Alkabot.t("command.music.remove.error")).queue();
        }
    }
}
