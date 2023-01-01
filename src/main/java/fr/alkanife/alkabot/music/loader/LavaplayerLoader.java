package fr.alkanife.alkabot.music.loader;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.music.AbstractMusic;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class LavaplayerLoader extends AbstractMusic {

    private boolean retrying = false;

    public LavaplayerLoader(MusicManager musicManager) {
        super(musicManager);
    }

    // From command
    public void load(SlashCommandInteractionEvent slashCommandInteractionEvent, final String url, boolean priority, boolean force) {
        Alkabot.debug("Loading music from '" + url + "' (" + priority + ")...");

        getMusicManager().getAudioPlayerManager().loadItemOrdered(getMusicManager().getPlayer(), url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                retrying = false;

                String id = "";
                if (slashCommandInteractionEvent.getMember() != null)
                    id = slashCommandInteractionEvent.getMember().getId();

                AlkabotTrack alkabotTrack = new AlkabotTrack(track, Alkabot.t("command.music.play.source.url"), id, priority);

                getMusicManager().getTrackScheduler().queue(alkabotTrack, force);

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(Alkabot.t("command.music.play.title") + " " + (priority ? Alkabot.t("command.music.play.priority") : ""));
                embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ") " + Alkabot.t("command.music.generic.by")
                        + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ") " + StringUtils.durationToString(alkabotTrack.getDuration(), true, false) +
                        (priority ? "" : ("\n\n" + (Alkabot.t("command.music.play.position") + " `" + (getMusicManager().getTrackScheduler().getQueue().size() + 1) + "`"))));
                embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

                Alkabot.debug("Track loaded! Using URL: " + alkabotTrack.getUrl());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                retrying = false;

                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = playlist.getTracks().get(0);

                String id = "";
                if (slashCommandInteractionEvent.getMember() != null)
                    id = slashCommandInteractionEvent.getMember().getId();

                if (url.startsWith("ytsearch")) {
                    AlkabotTrack alkabotTrack = new AlkabotTrack(firstTrack, Alkabot.t("command.music.play.source.search"), id, priority);

                    getMusicManager().getTrackScheduler().queue(alkabotTrack, force);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("command.music.play.title") + " " + (priority ? Alkabot.t("command.music.play.priority") : ""));
                    embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ") " + Alkabot.t("command.music.generic.by")
                            + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ") " + StringUtils.durationToString(alkabotTrack.getDuration(), true, false) +
                            (priority ? "" : ("\n\n" + (Alkabot.t("command.music.play.position") + " `" + (getMusicManager().getTrackScheduler().getQueue().size() + 1) + "`"))));
                    embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

                    Alkabot.debug("Track loaded (youtube search)! Using URL: " + alkabotTrack.getUrl());
                } else {
                    List<AlkabotTrack> alkabotTrackList = new ArrayList<>();

                    String source = Alkabot.t("jukebox-command-play-source-yt-search-playlist") + " / \"" + playlist.getName() + "\"";

                    for (AudioTrack audioTrack : playlist.getTracks())
                        alkabotTrackList.add(new AlkabotTrack(audioTrack, source, id, priority));

                    AlkabotTrack firstAlkabotTrack = new AlkabotTrack(firstTrack, source, id, priority);

                    getMusicManager().getTrackScheduler().queuePlaylist(firstAlkabotTrack, alkabotTrackList, force);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("command.music.play.title_playlist") + " " + (priority ? Alkabot.t("command.music.play.priority") : ""));
                    embedBuilder.setDescription("[" + playlist.getName() + "](" + url + ")\n\n" +
                            Alkabot.t("command.music.play.entries") + " `" + playlist.getTracks().size() + "`\n" +
                            Alkabot.t("command.music.play.newtime") + " `" + StringUtils.durationToString(getMusicManager().getTrackScheduler().getQueueDuration(), true, true) + "`");

                    embedBuilder.setThumbnail(firstAlkabotTrack.getThumbUrl());

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

                    Alkabot.debug("Playlist '" + playlist.getName() + "' loaded! (" + playlist.getTracks().size() + " tracks)");
                }
            }

            @Override
            public void noMatches() {
                retrying = false;

                slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("command.music.play.error.no_matches")).queue();
                Alkabot.debug("No matches!");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                Alkabot.getLogger().warn("Load fail - retry = " + retrying);
                if (retrying) {
                    slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("command.music.play.error.generic")).queue();
                    Alkabot.debug("Failed to load!");
                    retrying = false;
                } else {
                    retrying = true;
                    load(slashCommandInteractionEvent, url, priority, force);
                    Alkabot.debug("Failed to load! Retrying...");
                }
            }
        });
    }
}