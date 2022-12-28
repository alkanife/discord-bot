package fr.alkanife.alkabot.commands.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.commands.utils.Command;
import fr.alkanife.alkabot.playlists.Playlist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;

public class PlaylistCommand {

    @Command(name = "playlist")
    public void playlist(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        String subCommand = slashCommandInteractionEvent.getSubcommandName();

        switch (subCommand) {
            case "add" -> {
                String name = slashCommandInteractionEvent.getOption("name").getAsString();
                String url = slashCommandInteractionEvent.getOption("url").getAsString();

                Playlist playlist = Alkabot.getPlaylist(name);

                if (playlist != null) {
                    slashCommandInteractionEvent.reply(Alkabot.t("playlists-command-add-nope")).queue();
                    return;
                }

                playlist = new Playlist(name, url, slashCommandInteractionEvent.getUser().getId());

                try {
                    Alkabot.getPlaylists().add(playlist);
                    Alkabot.getPlaylistManager().write();

                    slashCommandInteractionEvent.reply(Alkabot.t("playlists-command-add-success", "`" + name + "`")).queue();
                } catch (IOException e) {
                    slashCommandInteractionEvent.reply(Alkabot.t("playlists-command-add-failed")).queue();
                    Alkabot.getLogger().error("Failed to add a playlist:");
                    e.printStackTrace();
                }
            }

            case "remove" -> {
                String name = slashCommandInteractionEvent.getOption("name").getAsString();

                Playlist playlist = Alkabot.getPlaylist(name);

                if (playlist == null) {
                    slashCommandInteractionEvent.reply(Alkabot.t("playlists-command-remove-nope")).queue();
                    return;
                }

                try {
                    Alkabot.getPlaylists().remove(playlist);
                    Alkabot.getPlaylistManager().write();

                    slashCommandInteractionEvent.reply(Alkabot.t("playlists-command-remove-success", "`" + name + "`")).queue();
                } catch (IOException e) {
                    slashCommandInteractionEvent.reply(Alkabot.t("playlists-command-remove-failed")).queue();
                    Alkabot.getLogger().error("Failed to remove a playlist:");
                    e.printStackTrace();
                }
            }

            case "list" -> {
                if (Alkabot.getPlaylists().size() == 0) {
                    slashCommandInteractionEvent.reply(Alkabot.t("playlists-command-list-noentries")).queue();
                    return;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(Alkabot.getPlaylists().size() + " " + Alkabot.t("playlists-command-list-title"));

                StringBuilder stringBuilder = new StringBuilder();
                for (Playlist p : Alkabot.getPlaylists())
                    stringBuilder.append("- [").append(p.getName()).append("](").append(p.getUrl()).append(") (")
                            .append(Alkabot.t("playlists-command-list-addedby")).append(" <@").append(p.getUser_id()).append(">)\n");

                embedBuilder.setDescription(stringBuilder);

                slashCommandInteractionEvent.replyEmbeds(embedBuilder.build()).queue();
            }
        }
    }
}
