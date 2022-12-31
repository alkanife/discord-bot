package fr.alkanife.alkabot.commands;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class InfoCommands {

    public void info(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.reply(Alkabot.t("info-command")).setEphemeral(true).queue();
    }

    public void serverinfo(SlashCommandInteractionEvent slashCommandEvent) {
        Guild guild = slashCommandEvent.getGuild();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(guild.getName());

        StringBuilder description = new StringBuilder();
        description.append(Alkabot.t("serverinfo-command-members")).append(" `").append(guild.getMemberCount()).append("`\n");
        description.append(Alkabot.t("serverinfo-command-channels")).append(" `").append(guild.getChannels().size()).append("`\n");
        description.append(Alkabot.t("serverinfo-command-emotes")).append(" `").append(guild.getEmojis().size()).append("`\n");
        description.append(Alkabot.t("serverinfo-command-roles")).append(" `").append(guild.getRoles().size()).append("`\n");
        description.append(Alkabot.t("serverinfo-command-boosters")).append(" `").append(guild.getBoosters().size()).append("`\n");
        description.append(Alkabot.t("serverinfo-command-boosts")).append(" `").append(guild.getBoostCount()).append("`\n");

        if (guild.getOwner() != null)
            description.append(Alkabot.t("serverinfo-command-owner")).append(" ").append(guild.getOwner().getAsMention()).append("\n");

        description.append(Alkabot.t("serverinfo-command-creation-date")).append(" `").append(offsetToString(guild.getTimeCreated())).append("`\n");
        description.append("\n");

        if (guild.getIconUrl() != null) {
            embedBuilder.setThumbnail(guild.getIconUrl());
            description.append("[Icon url](").append(guild.getIconUrl()).append(")\n");
        }

        if (guild.getBannerUrl() != null)
            description.append("[Banner url](").append(guild.getBannerUrl()).append(")\n");

        if (guild.getSplashUrl() != null)
            description.append("[Splash url](").append(guild.getSplashUrl()).append(")\n");

        if (guild.getVanityUrl() != null)
            description.append("[Vanity url](").append(guild.getVanityUrl()).append(")");

        embedBuilder.setDescription(description);
        embedBuilder.setThumbnail(guild.getIconUrl());

        slashCommandEvent.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public void emoteinfo(SlashCommandInteractionEvent slashCommandEvent) {
        OptionMapping optionMapping = slashCommandEvent.getOption("input");
        String input = optionMapping.getAsString();
        String[] args = input.split(":");

        if (args.length < 3) {
            slashCommandEvent.reply(Alkabot.t("emoteinfo-command-error")).setEphemeral(true).queue();
            return;
        }

        String emoteID = args[2].replaceAll(">", "");

        RichCustomEmoji richCustomEmoji = slashCommandEvent.getJDA().getEmojiById(emoteID);

        if (richCustomEmoji == null) {
            slashCommandEvent.reply(Alkabot.t("emoteinfo-command-error")).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(richCustomEmoji.getName());
        embedBuilder.setThumbnail(richCustomEmoji.getImageUrl());

        StringBuilder stringBuilder = new StringBuilder();

        if (richCustomEmoji.getGuild() != null)
            stringBuilder.append(Alkabot.t("emoteinfo-command-guild")).append(" `").append(richCustomEmoji.getGuild().getName()).append("`\n");

        stringBuilder.append(Alkabot.t("emoteinfo-command-creation-date")).append(" `").append(offsetToString(richCustomEmoji.getTimeCreated())).append("`\n");
        stringBuilder.append("\n[URL](").append(richCustomEmoji.getImageUrl()).append(")");

        embedBuilder.setDescription(stringBuilder);

        slashCommandEvent.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public void memberinfo(SlashCommandInteractionEvent slashCommandEvent) {
        OptionMapping optionMapping = slashCommandEvent.getOption("input");
        Member member = optionMapping.getAsMember();
        User user = optionMapping.getAsUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setTitle(user.getName());

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(user.getAsMention()).append("\n\n");
        stringBuilder.append(Alkabot.t("memberinfo-command-joineddiscord")).append(" `").append(offsetToString(user.getTimeCreated())).append("`\n");

        if (member != null)
            stringBuilder.append(Alkabot.t("memberinfo-command-joinedserver")).append(" `").append(offsetToString(member.getTimeJoined())).append("`\n");

        stringBuilder.append("\n");

        if (user.getAvatarUrl() != null)
            stringBuilder.append("[Avatar URL](").append(user.getAvatarUrl()).append(")\n");

        stringBuilder.append("[Default Avatar URL](").append(user.getDefaultAvatarUrl()).append(")\n");
        stringBuilder.append("[Effective Avatar URL](").append(user.getEffectiveAvatarUrl()).append(")");

        embedBuilder.setDescription(stringBuilder);

        slashCommandEvent.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    private String offsetToString(OffsetDateTime offsetDateTime) {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(offsetDateTime.toInstant().toEpochMilli()));
    }

}
