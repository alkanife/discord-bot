package fr.alkanife.alkabot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.music.MusicUtils;
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
        int pages;
        if (!StringUtils.endsWithZero(tracksSize)) {
            for (int i = 0; i < 11; i++) {
                if (StringUtils.endsWithZero(tracksSize))
                    break;

                tracksSize++;
            }
        }
        pages = tracksSize / 10;
        OptionMapping pageOption = event.getOption("page");
        int page = 0;
        if (pageOption != null)
            page = ((int) pageOption.getAsLong()) - 1;
        if (page < 0)
            page = 0;
        if ((page - 1) > pages) {
            event.getHook().sendMessage(Lang.get("command.music.queue.error.out_of_range")).queue();
            return;
        }


        if (tracks.size() == 0) {
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

            StringBuilder followingTracks = new StringBuilder();
            for (int i = (page * 10); i < ((page * 10) + 10); i++) {
                try {
                    followingTracks.append(
                            Lang.t("command.music.queue.following_track")
                                    .parse("index", String.valueOf(i + 1))
                                    .parseTrack(tracks.get(i))
                                    .getValue()
                    ).append("\n");
                } catch (Exception ignored) {
                    break;
                }
            }

            embed.setDescription(
                    Lang.t("command.music.queue.description")
                            .parseTrack(alkabot.getMusicManager().getTrackScheduler().getNowPlaying())
                            .parse("following", followingTracks.toString())
                            .parseQueue(alkabot.getMusicManager())
                            .parse("page", String.valueOf(page + 1))
                            .parse("page_count", String.valueOf(pages))
                            .getValue()
            );

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}