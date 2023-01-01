package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.music.playlist.Playlist;
import fr.alkanife.alkabot.music.playlist.PlaylistManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;
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
                    .addOption(OptionType.STRING, "input", Alkabot.t("command.music.playlist.add.input_description"), true));

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
        String subCommand = event.getSubcommandName();
        PlaylistManager playlistManager = Alkabot.getPlaylistManager();

        switch (subCommand) {
            case "add" -> {
                String name = event.getOption("name").getAsString();
                String url = event.getOption("url").getAsString();

                Playlist playlist = playlistManager.getPlaylist(name);

                if (playlist != null) {
                    event.reply(Alkabot.t("command.music.playlist.add.nope")).queue();
                    return;
                }

                playlist = new Playlist(name, url, event.getUser().getId());

                try {
                    playlistManager.getPlaylists().add(playlist);
                    Alkabot.getPlaylistManager().write();

                    event.reply(Alkabot.t("command.music.playlist.add.success", name)).queue();
                } catch (IOException e) {
                    event.reply(Alkabot.t("command.music.playlist.add.fail")).queue();
                    Alkabot.getLogger().error("Failed to add a playlist:");
                    e.printStackTrace();
                }
            }

            case "remove" -> {
                String name = event.getOption("name").getAsString();

                Playlist playlist = playlistManager.getPlaylist(name);

                if (playlist == null) {
                    event.reply(Alkabot.t("command.music.playlist.remove.nope")).queue();
                    return;
                }

                try {
                    playlistManager.getPlaylists().remove(playlist);
                    Alkabot.getPlaylistManager().write();

                    event.reply(Alkabot.t("command.music.playlist.remove.success", name)).queue();
                } catch (IOException e) {
                    event.reply(Alkabot.t("command.music.playlist.remove.fail")).queue();
                    Alkabot.getLogger().error("Failed to remove a playlist:");
                    e.printStackTrace();
                }
            }

            case "list" -> {
                if (playlistManager.getPlaylists().size() == 0) {
                    event.reply(Alkabot.t("command.music.playlist.list.no_entries")).queue();
                    return;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(playlistManager.getPlaylists().size() + " " + Alkabot.t("command.music.playlist.list.title"));

                StringBuilder stringBuilder = new StringBuilder();
                for (Playlist p : playlistManager.getPlaylists())
                    stringBuilder.append("- [").append(p.getName()).append("](").append(p.getUrl()).append(") (")
                            .append(Alkabot.t("command.music.playlist.list.added_by")).append(" <@").append(p.getUser_id()).append(">)\n");

                embedBuilder.setDescription(stringBuilder);

                event.replyEmbeds(embedBuilder.build()).queue();
            }

            case "info" -> {
                String name = event.getOption("name").getAsString();

                Playlist playlist = playlistManager.getPlaylist(name);

                if (playlist == null) {
                    event.reply(Alkabot.t("command.music.playlist.info.nope")).queue();
                    return;
                }

                // Todo
                event.reply("TODO").queue();
            }
        }
    }
}
