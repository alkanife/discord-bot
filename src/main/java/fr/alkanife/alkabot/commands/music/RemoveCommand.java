package fr.alkanife.alkabot.commands.music;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
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

    public RemoveCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.remove.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isRemove();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "track", Lang.get("command.music.remove.track"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        MusicManager musicManager = alkabot.getMusicManager();

        if (musicManager.getPlayer().getPlayingTrack() == null) {
            event.reply(Lang.get("command.music.remove.error.no_track")).queue();
            return;
        }

        OptionMapping removeOption = event.getOption("track");
        long remove = 1;
        if (removeOption != null) {
            remove = removeOption.getAsLong();

            if (remove > musicManager.getTrackScheduler().getQueue().size() || remove <= 0) {
                event.reply(Lang.get("command.music.remove.error.no_track")).queue();
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

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(
                    Lang.t("command.music.remove.success.title")
                            .parseTrack(t)
                            .getValue()
            );
            embed.setThumbnail(
                    Lang.t("command.music.remove.success.icon")
                            .parseTrackThumbnail(t)
                            .parseMemberAvatars(event.getMember())
                            .parseBotAvatars(alkabot)
                            .parseGuildAvatar(event.getGuild())
                            .getImage()
            );
            embed.setColor(Lang.getColor("command.music.remove.success.color"));
            embed.setDescription(
                    Lang.t("command.music.remove.success.description")
                            .parseTrack(t)
                            .getValue()
            );

            event.replyEmbeds(embed.build()).queue();
        } catch (Exception e) {
            event.reply(Lang.get("command.music.remove.error.generic")).queue();
        }
    }
}
