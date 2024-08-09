package dev.alkanife.alkabot.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.music.AlkabotTrack;
import dev.alkanife.alkabot.music.MusicManager;
import dev.alkanife.alkabot.util.PagedList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
        return Lang.get("command.music.queue.command.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isQueue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "page", Lang.get("command.music.queue.command.page"), false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = alkabot.getMusicManager();

        AudioTrack current = musicManager.getPlayer().getPlayingTrack();

        if (current == null) {
            event.reply(Lang.get("command.music.queue.error.not_playing")).queue();
            return;
        }

        event.deferReply().queue();

        List<AlkabotTrack> tracks = new ArrayList<>(musicManager.getTrackScheduler().getQueue());
        int tracksSize = tracks.size();

        PagedList pagedList = new PagedList();

        if (!pagedList.parsePage(event, tracksSize, Lang.get("command.music.queue.error.out_of_range")))
            return;

        if (tracks.isEmpty()) {
            alkabot.getMusicManager().nowPlaying(event);
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(
                    Lang.t("command.music.queue.title")
                            .parseQueue(alkabot.getMusicManager())
                            .getValue()
            );
            embed.setColor(Lang.getColor("command.music.queue.color"));
            embed.setThumbnail(
                    Lang.t("command.music.queue.icon")
                            .parseBotAvatars(alkabot)
                            .parseMemberAvatars(event.getMember())
                            .parseGuildAvatar(event.getGuild())
                            .getImage()
            );

            String followingTracks = pagedList.toStringList(index -> Lang.t("command.music.queue.following_track")
                    .parse("index", String.valueOf(index + 1))
                    .parseTrack(tracks.get(index))
                    .getValue());

            embed.setDescription(
                    Lang.t("command.music.queue.description")
                            .parseTrack(alkabot.getMusicManager().getTrackScheduler().getNowPlaying())
                            .parse("following", followingTracks)
                            .parseQueue(alkabot.getMusicManager())
                            .parse("page", String.valueOf(pagedList.getPage() + 1))
                            .parse("page_count", String.valueOf(pagedList.getPages()))
                            .getValue()
            );

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}