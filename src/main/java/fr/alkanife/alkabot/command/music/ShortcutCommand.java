package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.music.Shortcut;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;
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
        return alkabot.t("command.music.shortcut.description");
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
            subs.add(new SubcommandData("bind", alkabot.t("command.music.shortcut.bind.description"))
                    .addOption(OptionType.STRING, "name", alkabot.t("command.music.shortcut.bind.name_description"), true)
                    .addOption(OptionType.STRING, "query", alkabot.t("command.music.shortcut.bind.query_description"), true));

        if (alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isUnbind())
            subs.add(new SubcommandData("unbind", alkabot.t("command.music.shortcut.unbind.description"))
                    .addOption(OptionType.STRING, "name", alkabot.t("command.music.shortcut.unbind.name_description"), true));

        if (alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isInfo())
            subs.add(new SubcommandData("info", alkabot.t("command.music.shortcut.info.description"))
                    .addOption(OptionType.STRING, "name", alkabot.t("command.music.shortcut.info.name_description"), true));

        if (alkabot.getConfig().getCommandConfig().getMusicCommandConfig().getShortcutCommandConfig().isList())
            subs.add(new SubcommandData("list", alkabot.t("command.music.shortcut.list.description"))
                    .addOption(OptionType.INTEGER, "page", alkabot.t("command.music.shortcut.list.page_description"), false));

        if (subs.size() > 0)
            commandData.addSubcommands(subs);

        return commandData;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();
        ShortcutManager shortcutManager = alkabot.getShortcutManager();
        List<Shortcut> shortcuts = shortcutManager.getShortcuts();

        event.deferReply().queue();

        switch (subCommand) {
            case "bind" -> {
                String name = event.getOption("name").getAsString();
                String query = event.getOption("query").getAsString();

                Shortcut shortcut = shortcutManager.getShortcut(name);

                if (shortcut != null) {
                    event.reply(alkabot.t("command.music.shortcut.add.nope")).queue();
                    return;
                }

                shortcut = new Shortcut(name, query, event.getUser().getId(), new Date());

                try {
                    shortcuts.add(shortcut);
                    shortcutManager.write();

                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.bind.success", name)).queue();
                } catch (IOException e) {
                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.bind.fail")).queue();
                    alkabot.getLogger().error("Failed to bind a shortcut " + name + " to " + query + ":");
                    e.printStackTrace();
                }
            }

            case "unbind" -> {
                String name = event.getOption("name").getAsString();

                Shortcut shortcut = shortcutManager.getShortcut(name);

                if (shortcut == null) {
                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.unbind.nope")).queue();
                    return;
                }

                try {
                    shortcuts.remove(shortcut);
                    shortcutManager.write();

                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.unbind.success", name)).queue();
                } catch (IOException e) {
                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.unbind.fail")).queue();
                    alkabot.getLogger().error("Failed to remove a shortcut " + name + ":");
                    e.printStackTrace();
                }
            }

            case "list" -> {
                if (shortcuts.size() == 0) {
                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.list.no_entries")).queue();
                    return;
                }

                int shortcutsSize = shortcuts.size();
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
                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.list.out_of_range")).queue();
                    return;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(alkabot.t("command.music.shortcut.list.title", "" + shortcuts.size()));
                StringBuilder desc = new StringBuilder();

                for (int i = (page * 10); i < ((page * 10) + 10); i++) {
                    try {
                        Shortcut shortcut = shortcuts.get(i);
                        desc.append("`").append(i + 1).append(".` ");

                        boolean url = StringUtils.isURL(shortcut.getQuery());

                        if (url)
                            desc.append(" [");

                        desc.append(shortcut.getName());

                        if (url)
                            desc.append("](").append(shortcut.getQuery()).append(")");

                        desc.append(" [<@").append(shortcut.getCreator_id()).append(">]\n");
                    } catch (Exception e) {
                        break;
                    }
                }

                desc.append("\n").append("**PAGE ").append(page + 1).append(" / ").append(pages).append("**\n\n");
                embedBuilder.setDescription(desc.toString());
                event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

            }

            case "info" -> {
                String name = event.getOption("name").getAsString();

                Shortcut shortcut = shortcutManager.getShortcut(name);

                if (shortcut == null) {
                    event.getHook().sendMessage(alkabot.t("command.music.shortcut.info.nope")).queue();
                    return;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(shortcut.getName());
                embedBuilder.setDescription("**" + alkabot.t("command.music.shortcut.info.creation_date") + "** " + StringUtils.dateToString(shortcut.getCreation_date()) + "\n" +
                        "**" + alkabot.t("command.music.shortcut.info.by") + "** <@" + shortcut.getCreator_id() + ">\n" +
                        "**" + alkabot.t("command.music.shortcut.info.query") + "** " + shortcut.getQuery());

                event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            }
        }
    }
}
