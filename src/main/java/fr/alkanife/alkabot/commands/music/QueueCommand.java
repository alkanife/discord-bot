package fr.alkanife.alkabot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.commands.utils.Command;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.List;

public class QueueCommand {

    @Command(name = "queue")
    public void queue(SlashCommandInteractionEvent event) {
        AudioTrack current = Alkabot.getAudioPlayer().getPlayingTrack();
        if (current == null) {
            event.reply(Alkabot.t("jukebox-command-no-current")).queue();
            return;
        }
        List<AlkabotTrack> tracks = new ArrayList<>(Alkabot.getTrackScheduler().getQueue());
        int tracksSize = tracks.size();
        int pages = 0;
        if (!endsWithZero(tracksSize)) {
            for (int i = 0; i < 11; i++) {
                if (endsWithZero(tracksSize))
                    break;

                tracksSize++;
            }
        }
        pages = tracksSize / 10;
        OptionMapping pageOption = event.getOption("input");
        int page = 0;
        if (pageOption != null)
            page = ((int) pageOption.getAsLong()) - 1;
        if (page < 0)
            page = 0;
        if ((page - 1) > pages) { //todo bug
            event.reply(Alkabot.t("jukebox-command-queue-outofrange")).queue();
            return;
        }
        event.deferReply().queue();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String desc = "";
        if (tracks.size() == 0) {
            embedBuilder.setTitle(Alkabot.t("jukebox-command-queue-now-playing"));
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + current.getIdentifier() + "/0.jpg");
            desc += "**[" + current.getInfo().title + "](" + current.getInfo().uri + ")** " + StringUtils.durationToString(current.getDuration(), true, false);
        } else {                                                                           // '~' because String.valueOf don't work?
            embedBuilder.setTitle(Alkabot.t("jukebox-command-queue-queued-title", "~" + Alkabot.getTrackScheduler().getQueue().size()));
            embedBuilder.setThumbnail(Alkabot.t("jukebox-command-plgif"));
            desc = "__" + Alkabot.t("jukebox-command-queue-queued-now-playing") + "__\n" +
                    "**[" + current.getInfo().title + "](" + current.getInfo().uri + ")** " + StringUtils.durationToString(current.getDuration(), true, false) + "\n" +
                    "\n" +
                    "__" + Alkabot.t("jukebox-command-queue-queued-incoming") + "__\n";

            for (int i = (page * 10); i < ((page * 10) + 10); i++) {
                try {
                    AlkabotTrack audioTrack = tracks.get(i);
                    desc += "`" + (i + 1) + ".` [" + audioTrack.getTitle() + "](" + audioTrack.getUrl()+ ") " + StringUtils.durationToString(audioTrack.getDuration(), true, false) + "\n";
                } catch (Exception e) {
                    break;
                }
            }

            desc += "\n__" + Alkabot.t("jukebox-command-queue-queued-time") + "__ `" + StringUtils.durationToString(Alkabot.getTrackScheduler().getQueueDuration(), false, true) + "`\n\n" +
                    "**PAGE " + (page + 1) + " / " + pages + "**\n\n";
        }
        embedBuilder.setDescription(desc);
        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private boolean endsWithZero(int i) { //what an ugly way
        return Integer.toString(i).endsWith("0");
    }

}
