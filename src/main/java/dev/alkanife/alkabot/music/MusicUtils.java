package dev.alkanife.alkabot.music;

import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MusicUtils {

    public static EmbedBuilder createTackAddedEmbed(String commandSource, SlashCommandInteractionEvent event, AlkabotTrack alkabotTrack, MusicManager musicManager, int position) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(
                Lang.t("command.music." + commandSource + ".added.title.track")
                        .parseTrack(alkabotTrack)
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("command.music." + commandSource + ".added.icon.track")
                        .parseMemberAvatars(event.getMember())
                        .parseGuildAvatar(event.getGuild())
                        .parseBotAvatars(musicManager.getAlkabot())
                        .parseTrackThumbnail(alkabotTrack)
                        .getImage()
        );
        embed.setColor(Lang.getColor("command.music." + commandSource + ".added.color"));
        embed.setDescription(
                Lang.t("command.music." + commandSource + ".added.description.track")
                        .parseTrack(alkabotTrack)
                        .parseQueue(musicManager)
                        .parse("position", position+"")
                        .getValue()
        );

        return embed;
    }

    public static EmbedBuilder createPlaylistAddedEmbed(String commandSource, SlashCommandInteractionEvent event, AlkabotTrackPlaylist playlist, MusicManager musicManager, int position) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(
                Lang.t("command.music." + commandSource + ".added.title.playlist")
                        .parsePlaylist(playlist)
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("command.music." + commandSource + ".added.icon.playlist")
                        .parseMemberAvatars(event.getMember())
                        .parseGuildAvatar(event.getGuild())
                        .parseBotAvatars(musicManager.getAlkabot())
                        .parsePlaylistThumbnail(playlist)
                        .getImage()
        );
        embed.setColor(Lang.getColor("command.music." + commandSource + ".added.color"));
        embed.setDescription(
                Lang.t("command.music." + commandSource + ".added.description.playlist")
                        .parsePlaylist(playlist)
                        .parseQueue(musicManager)
                        .parse("position", position+"-"+(position+playlist.getTracks().size()-1))
                        .getValue()
        );

        return embed;
    }

    public static EmbedBuilder createGenericMusicFailEmbed(AlkabotTrack alkabotTrack, MusicManager musicManager) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(
                Lang.t("music.play_failed.title")
                        .parseGuildName(musicManager.getAlkabot().getGuild())
                        .getValue()
        );
        embed.setColor(Lang.getColor("music.play_failed.color"));
        embed.setThumbnail(
                Lang.t("music.play_failed.icon")
                        .parseGuildAvatar(musicManager.getAlkabot().getGuild())
                        .parseBotAvatars(musicManager.getAlkabot())
                        .parseTrackThumbnail(alkabotTrack)
                        .getValue()
        );
        embed.setDescription(
                Lang.t("music.play_failed.description")
                        .parseGuildName(musicManager.getAlkabot().getGuild())
                        .parseQueue(musicManager)
                        .parseTrack(alkabotTrack)
                        .getValue()
        );

        return embed;
    }

    /**
     * Convert milliseconds music durations in human-readable time (00:00:00)
     *
     * @param duration the duration
     * @param noLimit  if the method should return nothing when the duration is above 20 hours
     * @return the string
     */
    public static @NotNull String durationToString(long duration, boolean noLimit) {
        if (!noLimit)
            if (duration >= 72000000) // 20 hours
                return Lang.get("music.stream");

        StringBuilder stringBuilder = new StringBuilder();

        if (duration >= 3600000) // 1 hour
            stringBuilder.append(String.format("%02d:%02d:%02d",  TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)));
        else
            stringBuilder.append(String.format("%02d:%02d",  TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)));

        return stringBuilder.toString();
    }

    public static @NotNull String getPlaylistDuration(AlkabotTrackPlaylist playlist) {
        long duration = 0;

        for (AlkabotTrack alkabotTrack : playlist.getTracks())
            duration += alkabotTrack.getDuration();

        return durationToString(duration, true);
    }

}
