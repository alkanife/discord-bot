package fr.alkanife.alkabot.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.commands.utils.Command;
import fr.alkanife.alkabot.music.Music;
import fr.alkanife.alkabot.playlists.Playlist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MusicCommands {

    @Command(name = "playlists")
    public void playlists(SlashCommandInteractionEvent slashCommandInteractionEvent) {
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

    @Command(name = "jukebox")
    public void music(SlashCommandInteractionEvent slashCommandEvent) {
        Alkabot.setLastCommandChannel(slashCommandEvent.getChannel());

        String subCommand = slashCommandEvent.getSubcommandName();

        switch (subCommand) {
            case "play" -> {
                //Satania.addPlayCommand();

                String url = slashCommandEvent.getOption("input").getAsString();

                if (!Alkabot.isURL(url)) {
                    Playlist playlist = Alkabot.getPlaylist(url);

                    if (playlist == null)
                        url = "ytsearch: " + url;
                    else
                        url = playlist.getUrl();
                }

                Music.loadAndPlay(slashCommandEvent, url, false);
            }
            case "play_next" -> {
                //Satania.addPlayNextCommand();

                String url = slashCommandEvent.getOption("input").getAsString();

                if (!Alkabot.isURL(url)) {
                    Playlist playlist = Alkabot.getPlaylist(url);

                    if (playlist == null)
                        url = "ytsearch: " + url;
                    else
                        url = playlist.getUrl();
                }

                Music.loadAndPlay(slashCommandEvent, url, true);
            }
            case "remove" -> {
                //Satania.addRemoveCommand();

                if (Alkabot.getAudioPlayer().getPlayingTrack() == null) {
                    slashCommandEvent.reply(Alkabot.t("jukebox-command-no-current")).queue();
                    return;
                }
                OptionMapping removeOption = slashCommandEvent.getOption("input");
                long remove = 1;
                if (removeOption != null) {
                    remove = removeOption.getAsLong();

                    if (remove >= Alkabot.getTrackScheduler().getQueue().size()) {
                        slashCommandEvent.reply(Alkabot.t("jukebox-command-notenough")).queue();
                        return;
                    }
                }
                List<AudioTrack> aTracks = new ArrayList<>(Alkabot.getTrackScheduler().getQueue());
                try {
                    AudioTrack t = aTracks.get(((int) remove) - 1);

                    aTracks.remove(t);

                    BlockingQueue<AudioTrack> newBlockingQueue = new LinkedBlockingQueue<>();

                    for (AudioTrack audioTrack : aTracks)
                        newBlockingQueue.offer(audioTrack);

                    Alkabot.getTrackScheduler().setQueue(newBlockingQueue);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-command-remove-title"));
                    embedBuilder.setDescription("[" + t.getInfo().title + "](" + t.getInfo().uri + ")"
                            + " " + Alkabot.t("jukebox-by") + " [" + t.getInfo().author + "](" + t.getInfo().uri + ")");
                    embedBuilder.setThumbnail("https://img.youtube.com/vi/" + t.getIdentifier() + "/0.jpg");

                    slashCommandEvent.replyEmbeds(embedBuilder.build()).queue();
                } catch (Exception e) {
                    slashCommandEvent.reply(Alkabot.t("jukebox-command-remove-error")).queue();
                }
            }

            case "skip" -> {
                //Satania.addSkipCommand();

                if (Alkabot.getAudioPlayer().getPlayingTrack() == null) {
                    slashCommandEvent.reply(Alkabot.t("jukebox-command-no-current")).queue();
                    return;
                }
                OptionMapping skipSize = slashCommandEvent.getOption("input");
                int skip = 0;
                if (skipSize != null) {
                    long skipLong = skipSize.getAsLong();

                    if (skipLong >= Alkabot.getTrackScheduler().getQueue().size()) {
                        slashCommandEvent.reply(Alkabot.t("jukebox-command-notenough")).queue();
                        return;
                    }

                    for (skip = 0; skip < skipLong; skip++)
                        Alkabot.getTrackScheduler().getQueue().remove();
                }
                Alkabot.getTrackScheduler().nextTrack();
                if (skipSize == null)
                    slashCommandEvent.reply(Alkabot.t("jukebox-command-skip-one")).queue();
                else
                    slashCommandEvent.reply(Alkabot.t("jukebox-command-skip-mult", String.valueOf(skip))).queue();
            }
            case "stop" -> {
                slashCommandEvent.reply(Alkabot.t("jukebox-command-stop")).queue();
                Alkabot.getGuild().getAudioManager().closeAudioConnection();
            }
            case "shuffle" -> {
                //Satania.addShuffleCommand();

                List<AudioTrack> audioTracks = new ArrayList<>(Alkabot.getTrackScheduler().getQueue());
                Collections.shuffle(audioTracks);
                BlockingQueue<AudioTrack> blockingQueue = new LinkedBlockingQueue<>();
                for (AudioTrack audioTrack : audioTracks)
                    blockingQueue.offer(audioTrack);
                Alkabot.getTrackScheduler().setQueue(blockingQueue);
                slashCommandEvent.reply(Alkabot.t("jukebox-command-shuffle")).queue();
            }
            case "clear" -> {
                //Satania.addClearCommand();

                Alkabot.getTrackScheduler().setQueue(new LinkedBlockingQueue<>());
                slashCommandEvent.reply(Alkabot.t("jukebox-command-clear")).queue();
            }
            case "queue" -> {
                //Satania.addQueueCommand();

                AudioTrack current = Alkabot.getAudioPlayer().getPlayingTrack();
                if (current == null) {
                    slashCommandEvent.reply(Alkabot.t("jukebox-command-no-current")).queue();
                    return;
                }
                List<AudioTrack> tracks = new ArrayList<>(Alkabot.getTrackScheduler().getQueue());
                int tracksSize = tracks.size();
                int pages = 0;
                if (!endsWithZero(tracksSize)) {
                    for (int i = 0; i < 11; i++) {
                        if (endsWithZero(tracksSize))
                            break;

                        tracksSize++;
                    }
                }
                pages = tracksSize / 10;
                OptionMapping pageOption = slashCommandEvent.getOption("input");
                int page = 0;
                if (pageOption != null)
                    page = ((int) pageOption.getAsLong()) - 1;
                if (page < 0)
                    page = 0;
                if ((page - 1) > pages) { //todo bug
                    slashCommandEvent.reply(Alkabot.t("jukebox-command-queue-outofrange")).queue();
                    return;
                }
                slashCommandEvent.deferReply().queue();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                String desc = "";
                if (tracks.size() == 0) {
                    embedBuilder.setTitle(Alkabot.t("jukebox-command-queue-now-playing"));
                    embedBuilder.setThumbnail("https://img.youtube.com/vi/" + current.getIdentifier() + "/0.jpg");
                    desc += "**[" + current.getInfo().title + "](" + current.getInfo().uri + ")** " + Alkabot.musicDuration(current.getDuration());
                } else {                                                                           // '~' because String.valueOf don't work?
                    embedBuilder.setTitle(Alkabot.t("jukebox-command-queue-queued-title", "~" + Alkabot.getTrackScheduler().getQueue().size()));
                    embedBuilder.setThumbnail(Alkabot.t("jukebox-command-plgif"));
                    desc = "__" + Alkabot.t("jukebox-command-queue-queued-now-playing") + "__\n" +
                            "**[" + current.getInfo().title + "](" + current.getInfo().uri + ")** " + Alkabot.musicDuration(current.getDuration()) + "\n" +
                            "\n" +
                            "__" + Alkabot.t("jukebox-command-queue-queued-incoming") + "__\n";

                    for (int i = (page * 10); i < ((page * 10) + 10); i++) {
                        try {
                            AudioTrack audioTrack = tracks.get(i);
                            desc += "`" + (i + 1) + ".` [" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ") " + Alkabot.musicDuration(audioTrack.getDuration()) + "\n";
                        } catch (Exception e) {
                            break;
                        }
                    }

                    desc += "\n__" + Alkabot.t("jukebox-command-queue-queued-time") + "__ `" + Alkabot.playlistDuration(Alkabot.getTrackScheduler().getQueueDuration()) + "`\n\n" +
                            "**PAGE " + (page + 1) + " / " + pages + "**\n\n";
                }
                embedBuilder.setDescription(desc);
                slashCommandEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            }
        }
    }

    private boolean endsWithZero(int i) { //what an ugly way
        return Integer.toString(i).endsWith("0");
    }

}
