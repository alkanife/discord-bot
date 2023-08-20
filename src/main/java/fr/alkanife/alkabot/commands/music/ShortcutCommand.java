package fr.alkanife.alkabot.commands.music;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.music.data.Shortcut;
import fr.alkanife.alkabot.util.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShortcutCommand extends AbstractCommand {

    public ShortcutCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "shortcut";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.shortcut.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isBind()
                || alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isUnbind()
                || alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isInfo()
                || alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isList();
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription());

        List<SubcommandData> subs = new ArrayList<>();

        if (alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isBind())
            subs.add(new SubcommandData("bind", Lang.get("command.music.shortcut.bind.description"))
                    .addOption(OptionType.STRING, "name", Lang.get("command.music.shortcut.bind.input.name"), true)
                    .addOption(OptionType.STRING, "query", Lang.get("command.music.shortcut.bind.input.query"), true));

        if (alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isUnbind())
            subs.add(new SubcommandData("unbind", Lang.get("command.music.shortcut.unbind.description"))
                    .addOption(OptionType.STRING, "name", Lang.get("command.music.shortcut.unbind.input.name"), true));

        if (alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isInfo())
            subs.add(new SubcommandData("info", Lang.get("command.music.shortcut.info.description"))
                    .addOption(OptionType.STRING, "name", Lang.get("command.music.shortcut.info.input.name"), true));

        if (alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isList())
            subs.add(new SubcommandData("list", Lang.get("command.music.shortcut.list.description"))
                    .addOption(OptionType.INTEGER, "page", Lang.get("command.music.shortcut.list.input.page"), false));

        if (subs.size() > 0)
            commandData.addSubcommands(subs);

        return commandData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        event.deferReply().queue();

        switch (subCommand) {
            case "bind" -> {
                String name = event.getOption("name").getAsString();
                String query = event.getOption("query").getAsString();

                Shortcut shortcut = alkabot.getShortcut(name);

                if (shortcut != null) {
                    event.getHook().sendMessage(Lang.get("command.music.shortcut.bind.error.already_existing")).queue();
                    return;
                }

                shortcut = new Shortcut(name, query, event.getUser().getId(), new Date());

                try {
                    alkabot.getMusicData().getShortcutList().add(shortcut);
                    alkabot.updateMusicData();

                    event.getHook().sendMessage(
                            Lang.t("command.music.shortcut.bind.success")
                                    .parseShortcut(shortcut)
                                    .getValue()
                    ).queue();
                } catch (Exception e) {
                    event.getHook().sendMessage(Lang.get("command.music.shortcut.bind.error.failed")).queue();
                    alkabot.getLogger().error("Failed to bind a shortcut " + name + " to " + query + ":", e);
                }
            }

            case "unbind" -> {
                String name = event.getOption("name").getAsString();

                Shortcut shortcut = alkabot.getShortcut(name);

                if (shortcut == null) {
                    event.getHook().sendMessage(Lang.get("command.music.shortcut.unbind.error.not_existing")).queue();
                    return;
                }

                try {
                    alkabot.getMusicData().getShortcutList().remove(shortcut);
                    alkabot.updateMusicData();

                    event.getHook().sendMessage(
                            Lang.t("command.music.shortcut.unbind.success")
                                    .parseShortcut(shortcut)
                                    .getValue()
                    ).queue();
                } catch (Exception e) {
                    event.getHook().sendMessage(Lang.get("command.music.shortcut.unbind.error.failed")).queue();
                    alkabot.getLogger().error("Failed to remove a shortcut " + name + ":", e);
                }
            }

            case "list" -> {
                if (alkabot.getMusicData().getShortcutList().size() == 0) {
                    event.getHook().sendMessage(Lang.get("command.music.shortcut.list.error.no_entries")).queue();
                    return;
                }

                int shortcutsSize = alkabot.getMusicData().getShortcutList().size();
                int pages;
                if (!StringUtils.endsWithZero(shortcutsSize)) {
                    for (int i = 0; i < 11; i++) {
                        if (StringUtils.endsWithZero(shortcutsSize))
                            break;

                        shortcutsSize++;
                    }
                }
                pages = shortcutsSize / 10;
                OptionMapping pageOption = event.getOption("page");
                int page = 0;
                if (pageOption != null)
                    page = ((int) pageOption.getAsLong()) - 1;
                if (page < 0)
                    page = 0;
                if ((page - 1) > pages) {
                    event.getHook().sendMessage(Lang.get("command.music.shortcut.list.error.out_of_range")).queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(
                        Lang.t("command.music.shortcut.list.success.title")
                                .parseShortcutCount(alkabot.getMusicData())
                                .parseGuildName(alkabot.getGuild())
                                .getValue()
                );
                embed.setColor(Lang.getColor("command.music.shortcut.list.success.color"));
                embed.setThumbnail(
                        Lang.t("command.music.shortcut.list.success.icon")
                                .parseBotAvatars(alkabot)
                                .parseMemberAvatars(event.getMember())
                                .parseGuildAvatar(event.getGuild())
                                .getImage()
                );

                StringBuilder shortcuts = new StringBuilder();

                for (int i = (page * 10); i < ((page * 10) + 10); i++) {
                    try {
                        Shortcut shortcut = alkabot.getMusicData().getShortcutList().get(i);

                        shortcuts.append(
                                Lang.t("command.music.shortcut.list.success.shortcuts")
                                        .parseShortcut(shortcut)
                                        .parse("index", String.valueOf(i+1))
                                        .getValue()
                        ).append("\n");
                    } catch (Exception e) {
                        break;
                    }
                }

                embed.setDescription(
                        Lang.t("command.music.shortcut.list.success.description")
                                .parse("shortcuts", shortcuts.toString())
                                .parseShortcutCount(alkabot.getMusicData())
                                .parse("page", String.valueOf(page+1))
                                .parse("page_count", String.valueOf(pages))
                                .getValue()
                );

                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

            case "info" -> {
                String name = event.getOption("name").getAsString();

                Shortcut shortcut = alkabot.getShortcut(name);

                if (shortcut == null) {
                    event.getHook().sendMessage(Lang.get("command.music.shortcut.info.error.not_existing")).queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(
                        Lang.t("command.music.shortcut.info.success.title")
                                .parseShortcut(shortcut)
                                .parseGuildName(alkabot.getGuild())
                                .getValue()
                );
                embed.setColor(Lang.getColor("command.music.shortcut.info.success.color"));
                embed.setThumbnail(
                        Lang.t("command.music.shortcut.info.success.icon")
                                .parseBotAvatars(alkabot)
                                .parseMemberAvatars(event.getMember())
                                .parseGuildAvatar(event.getGuild())
                                .getImage()
                );
                embed.setDescription(
                        Lang.t("command.music.shortcut.info.success.description")
                                .parseShortcut(shortcut)
                                .getValue()
                );

                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}
