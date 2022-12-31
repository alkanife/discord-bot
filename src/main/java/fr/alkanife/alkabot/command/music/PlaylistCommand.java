package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "playlist";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.playlist.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().getPlaylist().isAdd()
                || Alkabot.getConfig().getCommands().getMusic().getPlaylist().isRemove()
                || Alkabot.getConfig().getCommands().getMusic().getPlaylist().isInfo()
                || Alkabot.getConfig().getCommands().getMusic().getPlaylist().isList();
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription());

        List<SubcommandData> subs = new ArrayList<>();

        if (Alkabot.getConfig().getCommands().getMusic().getPlaylist().isAdd())
            subs.add(new SubcommandData("add", Alkabot.t("command.music.playlist.add.description"))
                    .addOption(OptionType.STRING, "name", Alkabot.t("command.music.playlist.add.name_description"), true)
                    .addOption(OptionType.STRING, "url", Alkabot.t("command.music.playlist.add.url_description"), true));

        if (Alkabot.getConfig().getCommands().getMusic().getPlaylist().isRemove())
            subs.add(new SubcommandData("remove", Alkabot.t("command.music.playlist.remove.description"))
                    .addOption(OptionType.STRING, "name", Alkabot.t("command.music.playlist.remove.name_description"), true));

        if (Alkabot.getConfig().getCommands().getMusic().getPlaylist().isInfo())
            subs.add(new SubcommandData("info", Alkabot.t("command.music.playlist.info.description"))
                    .addOption(OptionType.STRING, "name", Alkabot.t("command.music.playlist.info.name_description"), true));

        if (Alkabot.getConfig().getCommands().getMusic().getPlaylist().isList())
            subs.add(new SubcommandData("list", Alkabot.t("command.music.playlist.list.description")));

        if (subs.size() > 0)
            commandData.addSubcommands(subs);

        return commandData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}
