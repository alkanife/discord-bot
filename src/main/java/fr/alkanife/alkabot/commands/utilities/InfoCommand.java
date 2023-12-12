package fr.alkanife.alkabot.commands.utilities;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.util.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand extends AbstractCommand {

    public InfoCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.utilities.info.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig().isEmote()
                || alkabot.getConfig().getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig().isServer()
                || alkabot.getConfig().getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig().isMember();
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription());

        List<SubcommandData> subs = new ArrayList<>();

        if (alkabot.getConfig().getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig().isServer())
            subs.add(new SubcommandData("server", Lang.get("command.utilities.info.server.description")));

        if (alkabot.getConfig().getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig().isMember())
            subs.add(new SubcommandData("member", Lang.get("command.utilities.info.member.description"))
                    .addOption(OptionType.USER, "input", Lang.get("command.utilities.info.member.input_description"), true));

        if (alkabot.getConfig().getCommandConfig().getUtilsCommandConfig().getInfoUtilsCommandConfig().isEmote())
            subs.add(new SubcommandData("emote", Lang.get("command.utilities.info.emote.description"))
                    .addOption(OptionType.STRING, "input", Lang.get("command.utilities.info.emote.input_description"), true));

        if (!subs.isEmpty())
            commandData.addSubcommands(subs);

        return commandData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        if (subCommand == null)
            return;

        switch (subCommand) {
            case "server" -> server(event);
            case "emote" -> emote(event);
            case "member" -> member(event);
        }
    }

    public void server(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        //noinspection DataFlowIssue
        embedBuilder.setTitle(guild.getName());

        StringBuilder description = new StringBuilder();
        description.append(Lang.get("command.utilities.info.server.members")).append(" `").append(guild.getMemberCount()).append("`\n");
        description.append(Lang.get("command.utilities.info.server.channels")).append(" `").append(guild.getChannels().size()).append("`\n");
        description.append(Lang.get("command.utilities.info.server.emotes")).append(" `").append(guild.getEmojis().size()).append("`\n");
        description.append(Lang.get("command.utilities.info.server.roles")).append(" `").append(guild.getRoles().size()).append("`\n");
        description.append(Lang.get("command.utilities.info.server.boosters")).append(" `").append(guild.getBoosters().size()).append("`\n");
        description.append(Lang.get("command.utilities.info.server.boosts")).append(" `").append(guild.getBoostCount()).append("`\n");

        if (guild.getOwner() != null)
            description.append(Lang.get("command.utilities.info.server.owner")).append(" ").append(guild.getOwner().getAsMention()).append("\n");

        description.append(Lang.get("command.utilities.info.generic.creation_date")).append(" `").append(Lang.formatDate(guild.getTimeCreated())).append("`\n");
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

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    @SuppressWarnings("ConstantValue")
    public void emote(SlashCommandInteractionEvent event) {
        OptionMapping optionMapping = event.getOption("input");
        //noinspection DataFlowIssue
        String input = optionMapping.getAsString();
        String[] args = input.split(":");

        if (args.length < 3) {
            event.reply(Lang.get("command.utilities.info.emote.error")).setEphemeral(true).queue();
            return;
        }

        String emoteID = args[2].replaceAll(">", "");

        RichCustomEmoji richCustomEmoji = event.getJDA().getEmojiById(emoteID);

        if (richCustomEmoji == null) {
            event.reply(Lang.get("command.utilities.info.emote.error")).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(richCustomEmoji.getName());
        embedBuilder.setThumbnail(richCustomEmoji.getImageUrl());

        StringBuilder stringBuilder = new StringBuilder();

        if (richCustomEmoji.getGuild() != null)
            stringBuilder.append(Lang.get("command.utilities.info.emote.guild")).append(" `").append(richCustomEmoji.getGuild().getName()).append("`\n");

        stringBuilder.append(Lang.get("command.utilities.info.generic.creation_date")).append(" `").append(Lang.formatDate(richCustomEmoji.getTimeCreated())).append("`\n");
        stringBuilder.append("\n[URL](").append(richCustomEmoji.getImageUrl()).append(")");

        embedBuilder.setDescription(stringBuilder);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public void member(SlashCommandInteractionEvent event) {
        OptionMapping optionMapping = event.getOption("input");
        //noinspection DataFlowIssue
        Member member = optionMapping.getAsMember();
        User user = optionMapping.getAsUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setTitle(user.getName());

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(user.getAsMention()).append("\n\n");
        stringBuilder.append(Lang.get("command.utilities.info.member.joined.discord")).append(" `").append(Lang.formatDate(user.getTimeCreated())).append("`\n");

        if (member != null)
            stringBuilder.append(Lang.get("command.utilities.info.member.joined.server")).append(" `").append(Lang.formatDate(member.getTimeJoined())).append("`\n");

        stringBuilder.append("\n");

        if (user.getAvatarUrl() != null)
            stringBuilder.append("[Avatar URL](").append(user.getAvatarUrl()).append(")\n");

        stringBuilder.append("[Default Avatar URL](").append(user.getDefaultAvatarUrl()).append(")\n");
        stringBuilder.append("[Effective Avatar URL](").append(user.getEffectiveAvatarUrl()).append(")");

        embedBuilder.setDescription(stringBuilder);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }
}