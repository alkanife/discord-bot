package fr.alkanife.alkabot.lang;

import fr.alkanife.alkabot.Alkabot;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
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

public class TranslationHandler {

    @Getter
    private String value;

    private final String NULL = "[null]";

    public TranslationHandler(String key) {
        Object obj = Lang.getTranslations().get(key);

        if (obj == null) {
            //translationsManager.getAlkabot().getLogger().warn("Missing translation at " + key); todo: logs
            value = "{" + key + "}";
            return;
        }

        value = "";

        // If it's a list, call a random value
        if (obj instanceof List list) {
            value = list.get(new Random().nextInt(list.size())).toString();
        } else {
            value = obj.toString();
        }
    }

    private void replaceTag(@NotNull String tag, @Nullable String value) {
        if (value == null)
            value = NULL;

        this.value = value.replaceAll("<" + tag.toLowerCase() + ">", value);
    }

    public void replaceDate(@NotNull String tag, @Nullable Date date) {
        if (date == null) {
            replaceTag(tag, NULL);
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Lang.getDateFormat(), Lang.getDateLocale());
        replaceTag(tag, simpleDateFormat.format(date));
    }

    public void replaceOffsetDateTime(@NotNull String tag, @Nullable OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            replaceTag(tag, NULL);
            return;
        }

        replaceDate(tag, new Date(offsetDateTime.toInstant().toEpochMilli()));
    }

    // ---------------
    //     PARSE
    // ---------------
    public TranslationHandler parse(@NotNull String tag, @Nullable String value) {
        replaceTag(tag, value);
        return this;
    }

    public TranslationHandler parseError(Exception exception) {
        return this;
    }

    // BOT
    public TranslationHandler parseBot(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotClientNames(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotMention(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotAvatars(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotId(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotVersion(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotBuildDate(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotFullVersion(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseBotGithub(@NotNull Alkabot alkabot) {
        return this;
    }

    // BOT -- ADMIN
    public TranslationHandler parseAdmins(@NotNull Alkabot alkabot) {
        return this;
    }

    public TranslationHandler parseAdmin(@Nullable Object object) {
        return this;
    }

    // COMMAND
    public TranslationHandler parseCommand(@Nullable SlashCommandInteractionEvent event) {
        return this;
    }

    // GUILD
    public TranslationHandler parseGuild(@Nullable Guild guild) {
        return this;
    }

    public TranslationHandler parseGuildName(@Nullable Guild guild) {
        return this;
    }

    public TranslationHandler parseGuildAvatar(@Nullable Guild guild) {
        return this;
    }

    // CHANNEL
    public TranslationHandler parseChannel(@Nullable MessageChannelUnion channel) {
        return this;
    }

    public TranslationHandler parseChannelName(@Nullable MessageChannelUnion channel) {
        return this;
    }
    public TranslationHandler parseChannel(@Nullable AudioChannelUnion channel) {
        return this;
    }

    // MEMBER
    public TranslationHandler parseMember(@Nullable Member member) {
        return this;
    }

    public TranslationHandler parseMemberNames(@Nullable Member member) {
        return this;
    }

    public TranslationHandler parseMemberMention(@Nullable Member member) {
        return this;
    }

    public TranslationHandler parseMemberId(@Nullable Member member) {
        return this;
    }

    public TranslationHandler parseMemberAvatars(@Nullable Member member) {
        return this;
    }

    // USER
    public TranslationHandler parseUser(@Nullable User user) {
        return this;
    }

    public TranslationHandler parseUserNames(@Nullable User user) {
        return this;
    }

    public TranslationHandler parseUserMention(@Nullable User user) {
        return this;
    }

    public TranslationHandler parseUserId(@Nullable User user) {
        return this;
    }

    public TranslationHandler parseUserAvatars(@Nullable User user) {
        return this;
    }

    /*public TranslationHandler placeAlkabot(@NotNull Alkabot alkabot) {
        replaceTag("bot_version", alkabot.getVersion());
        replaceTag("bot_build_date", alkabot.getBuild());
        replaceTag("bot_full_version", alkabot.getFullVersion());
        replaceTag("bot_github", alkabot.getGithub());
        return this;
    }

    public TranslationHandler placeBotNames(@NotNull Alkabot alkabot) {
        SelfUser selfUser = alkabot.getJda().getSelfUser();
        replaceTag("bot_name", selfUser.getName());
        replaceTag("bot_effective_name", selfUser.getEffectiveName());
        replaceTag("bot_global_name", selfUser.getGlobalName());
        replaceTag("bot_nickname", alkabot.getGuild().getSelfMember().getNickname());
        return this;
    }

    public TranslationHandler placeBotAvatars(@NotNull Alkabot alkabot) {
        replaceTag("bot_avatar_url", alkabot.getJda().getSelfUser().getAvatarUrl());
        replaceTag("bot_default_avatar_url", alkabot.getJda().getSelfUser().getDefaultAvatarUrl());
        replaceTag("bot_effective_avatar_url", alkabot.getJda().getSelfUser().getEffectiveAvatarUrl());
        return this;
    }

    public TranslationHandler placeUser(@NotNull User user) {
        replaceTag("user_name", user.getName());
        replaceTag("user_effective_name", user.getEffectiveName());
        replaceTag("user_global_name", user.getGlobalName());
        replaceTag("user_mention", user.getAsMention());
        replaceTag("user_id", user.getId());
        replaceTag("user_avatar_url", user.getAvatarUrl());
        replaceTag("user_default_avatar_url", user.getDefaultAvatarUrl());
        replaceTag("user_effective_avatar_url", user.getEffectiveAvatarUrl());
        replaceOffsetDateTime("user_time_created", user.getTimeCreated());
        return this;
    }

    public TranslationHandler placeMember(@Nullable Member member) {
        if (member == null) {
            replaceTag("member_name", NULL);
            replaceTag("member_nickname", NULL);
            replaceTag("member_effective_name", NULL);
            replaceTag("member_global_name", NULL);
            replaceTag("member_mention", NULL);
            replaceTag("member_id", NULL);
            replaceTag("member_avatar_url", NULL);
            replaceTag("member_default_avatar_url", NULL);
            replaceTag("member_effective_avatar_url", NULL);
            replaceTag("member_time_created", NULL);
            replaceTag("member_time_joined", NULL);
            replaceTag("member_time_boosted", NULL);
            return this;
        }

        replaceTag("member_name", member.getUser().getName());
        replaceTag("member_nickname", member.getNickname());
        replaceTag("member_effective_name", member.getEffectiveName());
        replaceTag("member_global_name", member.getUser().getGlobalName());
        replaceTag("member_mention", member.getAsMention());
        replaceTag("member_id", member.getId());
        replaceTag("member_avatar_url", member.getAvatarUrl());
        replaceTag("member_default_avatar_url", member.getDefaultAvatarUrl());
        replaceTag("member_effective_avatar_url", member.getEffectiveAvatarUrl());
        replaceOffsetDateTime("member_time_created", member.getTimeCreated());
        replaceOffsetDateTime("member_time_joined", member.getTimeJoined());
        replaceOffsetDateTime("member_time_boosted", member.getTimeBoosted());

        member.getVoiceState();
        member.getRoles();

        return this;
    }

    public TranslationHandler placeGuildName(Alkabot alkabot) {
        replaceTag("guild_name", alkabot.getGuild().getName());
        return this;
    }

    public TranslationHandler placeGuildAvatar(Alkabot alkabot) {
        replaceTag("guild_icon", alkabot.getGuild().getIconUrl());
        return this;
    }

    public TranslationHandler placeAdminNames(@NotNull Object object) {
        if (object instanceof User user) {
            replaceTag("admin_name", user.getName());
            replaceTag("admin_effective_name", user.getEffectiveName());
            replaceTag("admin_global_name", user.getGlobalName());
        } else {
            replaceTag("admin_name", "`Terminal`");
            replaceTag("admin_effective_name", "`Terminal`");
            replaceTag("admin_global_name", "`Terminal`");
        }

        return this;
    }

    public TranslationHandler placeAdmins(Alkabot alkabot) {
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
            admins = Lang.t("notification.generic.none").getValue();
        }

        replaceTag("admins", admins);
        replaceTag("admin_count", alkabot.getConfig().getAdminIds().size()+"");
        return this;
    }

    public TranslationHandler placeDate(@Nullable Date date) {
        replaceDate("date", date);
        return this;
    }

    public TranslationHandler placeOffsetDateTime(@Nullable OffsetDateTime offsetDateTime) {
        replaceOffsetDateTime("date", offsetDateTime);
        return this;
    }

    public TranslationHandler placeCommand(@NotNull SlashCommandInteractionEvent event) {
        return this;
    }

    public TranslationHandler parseChannel(MessageChannelUnion channel) {
        return this;
    }*/

    public String getImage() {
        try {
            new URL(value).toURI();
        } catch (Exception exception) {
            value = Lang.getDefaultImageURL();
        }
        return value;
    }

    public Color getColor() {
        Color color = Lang.getDefaultColor();
        try {
            color = Color.decode(value);
        } catch (Exception ignored) {}
        return color;
    }
}
