package fr.alkanife.alkabot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.util.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

public class QueueCommand extends AbstractCommand {

    public QueueCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.queue.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isQueue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "input", Lang.get("command.music.queue.input_description"), false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = alkabot.getMusicManager();

        AudioTrack current = musicManager.getPlayer().getPlayingTrack();

        if (current == null) {
            event.reply(Lang.get("command.music.generic.not_playing")).queue();
            return;
        }

        event.deferReply().queue();

        List<AlkabotTrack> tracks = new ArrayList<>(musicManager.getTrackScheduler().getQueue());
        int tracksSize = tracks.size();
        int pages;
        if (!StringUtils.endsWithZero(tracksSize)) {
            for (int i = 0; i < 11; i++) {
                if (StringUtils.endsWithZero(tracksSize))
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
        if ((page - 1) > pages) {
            event.reply(Lang.get("command.music.queue.out_of_range")).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        String desc = "";
        if (tracks.size() == 0) {
            embedBuilder.setTitle(Lang.get("command.music.queue.one_now_playing"));
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + current.getIdentifier() + "/0.jpg");
            desc += "**[" + current.getInfo().title + "](" + current.getInfo().uri + ")** " + StringUtils.durationToString(current.getDuration(), true, false);
        } else {
            embedBuilder.setTitle(Lang.get("command.music.queue.title") + musicManager.getTrackScheduler().getQueue().size());
            embedBuilder.setThumbnail(Lang.t("command.music.generic.images").getImage());
            desc = "__" + Lang.get("command.music.queue.now_playing") + "__\n" +
                    "**[" + current.getInfo().title + "](" + current.getInfo().uri + ")** " + StringUtils.durationToString(current.getDuration(), true, false) + "\n" +
                    "\n" +
                    "__" + Lang.get("command.music.queue.incoming") + "__\n";

            for (int i = (page * 10); i < ((page * 10) + 10); i++) {
                try {
                    AlkabotTrack audioTrack = tracks.get(i);
                    //noinspection StringConcatenationInLoop
                    desc += "`" + (i + 1) + ".` [" + audioTrack.getTitle() + "](" + audioTrack.getUrl()+ ") " + StringUtils.durationToString(audioTrack.getDuration(), true, false) + "\n";
                } catch (Exception e) {
                    break;
                }
            }

            desc += "\n__" + Lang.get("command.music.queue.time_left") + "__ `" + StringUtils.durationToString(musicManager.getTrackScheduler().getQueueDuration(), false, true) + "`\n\n" +
                    "**PAGE " + (page + 1) + " / " + pages + "**\n\n";
        }
        embedBuilder.setDescription(desc);
        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}