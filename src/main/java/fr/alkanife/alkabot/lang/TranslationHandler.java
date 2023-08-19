package fr.alkanife.alkabot.lang;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.admin.AdminCommandExecution;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.AlkabotTrackPlaylist;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.music.MusicUtils;
import fr.alkanife.alkabot.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationHandler {

    @Getter
    private String value;

    private final String NULL_VALUE = "[null]";

    public TranslationHandler(String key) {
        // Get object
        Object obj = Lang.getTranslations().get(key);

        // If not found, return a missing translation
        if (obj == null) {
            value = "{" + key + "}";
            System.out.println("Missing translation: " + key); // todo: logging
            return;
        }

        value = "";

        // If it's a list, call a random value
        if (obj instanceof List list) {
            value = list.get(new Random().nextInt(list.size())).toString();
        } else {
            value = obj.toString();
        }

        // Replace {variables}
        Pattern pattern = Pattern.compile("\\{([^}]*)}");
        Matcher matcher = pattern.matcher(value);

        while (matcher.find()) {
            String variableKey = matcher.group(1);
            Object variableObject = Lang.getTranslations().get(variableKey);

            if (variableObject == null) {
                System.out.println("Missing translation for '" + key + "': '" + variableKey + "'"); // todo: logging
            } else {
                value = value.replaceAll("\\{"+variableKey+"}", variableObject.toString());
            }
        }
    }

    private void replaceTag(@NotNull String tag, @Nullable String tagValue) {
        if (tagValue == null)
            tagValue = NULL_VALUE;

        value = value.replaceAll("<" + tag.toLowerCase() + ">", Matcher.quoteReplacement(tagValue));
    }

    public TranslationHandler parse(@NotNull String tag, @Nullable String value) {
        replaceTag(tag, value);
        return this;
    }

    // -------------
    // GENERIC
    // -------------
    public TranslationHandler parseAvatars(@Nullable User user, @NotNull String field) {
        if (user == null)
            return parse(field + "_avatar_url", null)
                    .parse(field + "_effective_avatar_url", null)
                    .parse(field + "_default_avatar_url", null);
        else
            return parse(field + "_avatar_url", user.getAvatarUrl())
                    .parse(field + "_effective_avatar_url", user.getEffectiveAvatarUrl())
                    .parse(field + "_default_avatar_url", user.getDefaultAvatarUrl());
    }

    public TranslationHandler parseNames(@Nullable User user, @NotNull String field) {
        if (user == null)
            return parse(field + "_name", null)
                    .parse(field + "_effective_name", null)
                    .parse(field + "_global_name", null);
        else
            return parse(field + "_name", user.getName())
                    .parse(field + "_effective_name", user.getEffectiveName())
                    .parse(field + "_global_name", user.getGlobalName());
    }

    public TranslationHandler parseMention(@Nullable IMentionable mentionable, @NotNull String field) {
        if (mentionable == null)
            return parse(field + "_mention", null);
        else
            return parse(field + "_mention", mentionable.getAsMention());
    }

    public TranslationHandler parseId(@Nullable ISnowflake snowflake, @NotNull String field) {
        if (snowflake == null)
            return parse(field + "_id", null);
        else
            return parse(field + "_id", snowflake.getId());
    }

    public TranslationHandler parseError(@Nullable Exception exception) {
        String error = NULL_VALUE;
        if (exception != null)
            error = exception.toString();

        return parse("error", error);
    }

    // -------------
    // BOT
    // -------------
    public TranslationHandler parseBot(@NotNull Alkabot alkabot) {
        return parseBotClientNames(alkabot)
                .parseBotMention(alkabot)
                .parseBotAvatars(alkabot)
                .parseBotId(alkabot)
                .parseBotVersion(alkabot)
                .parseBotBuildDate(alkabot)
                .parseBotFullVersion(alkabot)
                .parseBotGithub(alkabot);
    }

    public TranslationHandler parseBotClientNames(@NotNull Alkabot alkabot) {
        return parseNames(alkabot.getJda().getSelfUser(), "bot");
    }

    public TranslationHandler parseBotMention(@NotNull Alkabot alkabot) {
        return parseMention(alkabot.getJda().getSelfUser(), "bot");
    }

    public TranslationHandler parseBotAvatars(@NotNull Alkabot alkabot) {
        return parseAvatars(alkabot.getJda().getSelfUser(), "bot");
    }

    public TranslationHandler parseBotId(@NotNull Alkabot alkabot) {
        return parseId(alkabot.getJda().getSelfUser(), "bot");
    }

    public TranslationHandler parseBotVersion(@NotNull Alkabot alkabot) {
        return parse("bot_version", alkabot.getVersion());
    }

    public TranslationHandler parseBotBuildDate(@NotNull Alkabot alkabot) {
        return parse("bot_build_date", alkabot.getBuild());
    }

    public TranslationHandler parseBotFullVersion(@NotNull Alkabot alkabot) {
        return parse("bot_full_version", alkabot.getFullVersion());
    }

    public TranslationHandler parseBotGithub(@NotNull Alkabot alkabot) {
        return parse("bot_github", alkabot.getGithub());
    }

    public TranslationHandler parseAdmins(@NotNull Alkabot alkabot) {
        String admins = "";
        if (alkabot.getConfig().getAdminIds().size() > 0) {
            int i = 0;

            for (String admin : alkabot.getConfig().getAdminIds()) {
                if (i != 0)
                    admins += ",";

                admins += "<@" + admin + ">";

                if (i == 4) {
                    admins += "...";
                    break;
                }

                i++;
            }
        } else {
            admins = Lang.get("notification.generic.none");
        }

        return parse("admins", admins).parse("admin_count", alkabot.getConfig().getAdminIds().size()+"");
    }

    public TranslationHandler parseAdmin(@NotNull AdminCommandExecution execution, @NotNull String adminPath) {
        if (execution.messageReceivedEvent() == null)
            return parse("admin", Lang.get(adminPath + ".system"));
        else
            return parse("admin",
                    Lang.t(adminPath + ".admin")
                            .parseNames(execution.messageReceivedEvent().getAuthor(), "admin")
                            .parseMention(execution.messageReceivedEvent().getAuthor(), "admin")
                            .getValue()
            );
    }

    public TranslationHandler parseCommand(@NotNull SlashCommandInteractionEvent event) {
        return parse("command", event.getCommandString())
                .parse("full_command", event.getFullCommandName())
                .parse("command_name", event.getName());
    }

    // -------------
    // GUILD
    // -------------
    public TranslationHandler parseGuildName(@Nullable Guild guild) {
        if (guild == null)
            return parse("guild_name", null);
        else
            return parse("guild_name", guild.getName());
    }

    public TranslationHandler parseGuildAvatar(@Nullable Guild guild) {
        if (guild == null)
            return parse("guild_icon", null);
        else
            return parse("guild_icon", guild.getIconUrl());
    }

    // -------------
    // CHANNEL
    // -------------
    public TranslationHandler parseChannel(@Nullable Channel channel) {
        return parseChannelName(channel)
                .parseChannelMention(channel)
                .parseChannelId(channel);
    }

    public TranslationHandler parseChannelName(@Nullable Channel channel) {
        if (channel == null)
            return parse("channel_name", null);
        else
            return parse("channel_name", channel.getName());
    }

    public TranslationHandler parseChannelMention(@Nullable Channel channel) {
        return parseMention(channel, "channel");
    }

    public TranslationHandler parseChannelId(@Nullable Channel channel) {
        return parseId(channel, "channel");
    }

    // -------------
    // MEMBER
    // -------------
    public TranslationHandler parseMemberNames(@Nullable Member member) {
        if (member == null)
            return parseNames(null, "member")
                    .parse("member_nickname", null);
        else
            return parseNames(member.getUser(), "member")
                    .parse("member_nickname", null);
    }

    public TranslationHandler parseMemberMention(@Nullable Member member) {
        return parseMention(member, "member");
    }

    public TranslationHandler parseMemberId(@Nullable Member member) {
        return parseId(member, "member");
    }

    public TranslationHandler parseMemberAvatars(@Nullable Member member) {
        if (member == null)
            return parseAvatars(null, "member");
        else
            return parseAvatars(member.getUser(), "member");
    }

    // ---------
    // USER
    // ---------
    public TranslationHandler parseUserNames(@Nullable User user) {
        return parseNames(user, "user");
    }

    public TranslationHandler parseUserMention(@Nullable User user) {
        return parseMention(user, "user");
    }

    public TranslationHandler parseUserId(@Nullable User user) {
        return parseId(user, "user");
    }

    public TranslationHandler parseUserAvatars(@Nullable User user) {
        return parseAvatars(user, "user");
    }

    // ---------
    // MODERATOR
    // ---------
    public TranslationHandler parseModNames(@Nullable User user) {
        return parseNames(user, "moderator");
    }

    public TranslationHandler parseModMention(@Nullable User user) {
        return parseMention(user, "moderator");
    }

    public TranslationHandler parseModId(@Nullable User user) {
        return parseId(user, "moderator");
    }

    public TranslationHandler parseModAvatars(@Nullable User user) {
        return parseAvatars(user, "moderator");
    }

    // --------
    // MUSIC
    // --------
    public TranslationHandler parseTrack(@NotNull AlkabotTrack track) {
        return parse("track_title", track.getTitle())
                .parse("track_click_title", "[" + track.getTitle() + "](" + track.getUrl() + ")")
                .parse("track_artists", track.getArtists())
                .parse("track_click_artists", "[" + track.getArtists() + "](" + track.getUrl() + ")")
                .parse("track_url", track.getUrl())
                .parse("track_query", track.getQuery())
                .parse("track_duration", MusicUtils.durationToString(track.getDuration(), false))
                .parse("track_added_by_mention", "<@" + track.getAddedByID() + ">")
                .parse("track_added_by_id", track.getAddedByID())
                .parseTrackThumbnail(track);
    }

    public TranslationHandler parseTrackThumbnail(@NotNull AlkabotTrack track) {
        return parse("track_thumbnail", track.getThumbUrl());
    }

    public TranslationHandler parsePlaylist(@NotNull AlkabotTrackPlaylist playlist) {
        return parse("playlist_title", playlist.getTitle())
                .parse("playlist_click_title", "[" + playlist.getTitle() + "](" + playlist.getUrl() + ")")
                .parse("playlist_url", playlist.getUrl())
                .parse("playlist_size", playlist.getTracks().size() + "")
                .parse("playlist_duration", MusicUtils.getPlaylistDuration(playlist))
                .parsePlaylistThumbnail(playlist);
    }

    public TranslationHandler parsePlaylistThumbnail(@NotNull AlkabotTrackPlaylist playlist) {
        return parse("playlist_thumbnail", playlist.getThumbnailUrl());
    }

    public TranslationHandler parseQueue(@NotNull MusicManager musicManager) {
        return parse("queue_duration", MusicUtils.durationToString(musicManager.getTrackScheduler().getQueueDuration(), true))
                .parse("queue_size", musicManager.getTrackScheduler().getQueue().size()+"");
    }

    public String getImage() {
        try {
            new URL(value).toURI();
        } catch (Exception exception) {
            value = Lang.getDefaultImageURL();
        }
        return value;
    }

    public Color getColor() {
        if (value.equalsIgnoreCase("none"))
            return null;

        Color color = Lang.getDefaultColor();
        try {
            color = Color.decode(value);
        } catch (Exception ignored) {}
        return color;
    }
}
