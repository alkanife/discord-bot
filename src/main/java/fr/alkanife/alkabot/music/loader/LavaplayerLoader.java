package fr.alkanife.alkabot.music.loader;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.music.AbstractMusic;
import fr.alkanife.alkabot.music.AlkabotTrack;
import fr.alkanife.alkabot.music.MusicManager;
import fr.alkanife.alkabot.util.StringUtils;
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
        musicManager.getAlkabot().verbose("Loading music from '" + url + "' (" + priority + ")...");

        musicManager.getAudioPlayerManager().loadItemOrdered(musicManager.getPlayer(), url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                retrying = false;

                String id = "";
                if (slashCommandInteractionEvent.getMember() != null)
                    id = slashCommandInteractionEvent.getMember().getId();

                AlkabotTrack alkabotTrack = new AlkabotTrack(track, Lang.get("command.music.play.source.url"), id, priority);

                musicManager.getAlkabot().getMusicManager().getTrackScheduler().queue(alkabotTrack, force);

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(Lang.get("command.music.play.title") + " " + (priority ? Lang.get("command.music.play.priority") : ""));
                embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ") " + Lang.get("command.music.generic.by")
                        + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ") " + StringUtils.durationToString(alkabotTrack.getDuration(), true, false) +
                        (priority ? "" : ("\n\n" + (Lang.get("command.music.play.position") + " `" + (musicManager.getTrackScheduler().getQueue().size() + 1) + "`"))));
                embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

                musicManager.getAlkabot().verbose("Track loaded! Using URL: " + alkabotTrack.getUrl());
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
                    AlkabotTrack alkabotTrack = new AlkabotTrack(firstTrack, Lang.get("command.music.play.source.search"), id, priority);

                    musicManager.getTrackScheduler().queue(alkabotTrack, force);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Lang.get("command.music.play.title") + " " + (priority ? Lang.get("command.music.play.priority") : ""));
                    embedBuilder.setDescription("[" + alkabotTrack.getTitle() + "](" + alkabotTrack.getUrl() + ") " + Lang.get("command.music.generic.by")
                            + " [" + alkabotTrack.getArtists() + "](" + alkabotTrack.getUrl() + ") " + StringUtils.durationToString(alkabotTrack.getDuration(), true, false) +
                            (priority ? "" : ("\n\n" + (Lang.get("command.music.play.position") + " `" + (musicManager.getTrackScheduler().getQueue().size() + 1) + "`"))));
                    embedBuilder.setThumbnail(alkabotTrack.getThumbUrl());

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

                    musicManager.getAlkabot().verbose("Track loaded (youtube search)! Using URL: " + alkabotTrack.getUrl());
                } else {
                    List<AlkabotTrack> alkabotTrackList = new ArrayList<>();

                    String source = Lang.get("command.music.play.source.url_playlist") + " / \"" + playlist.getName() + "\"";

                    for (AudioTrack audioTrack : playlist.getTracks())
                        alkabotTrackList.add(new AlkabotTrack(audioTrack, source, id, priority));

                    AlkabotTrack firstAlkabotTrack = new AlkabotTrack(firstTrack, source, id, priority);

                    musicManager.getTrackScheduler().queuePlaylist(firstAlkabotTrack, alkabotTrackList, force);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Lang.get("command.music.play.title_playlist") + " " + (priority ? Lang.get("command.music.play.priority") : ""));
                    embedBuilder.setDescription("[" + playlist.getName() + "](" + url + ")\n\n" +
                            Lang.get("command.music.play.entries") + " `" + playlist.getTracks().size() + "`\n" +
                            Lang.get("command.music.play.newtime") + " `" + StringUtils.durationToString(musicManager.getTrackScheduler().getQueueDuration(), false, true) + "`");

                    embedBuilder.setThumbnail(firstAlkabotTrack.getThumbUrl());

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

                    musicManager.getAlkabot().verbose("Playlist '" + playlist.getName() + "' loaded! (" + playlist.getTracks().size() + " tracks)");
                }
            }

            @Override
            public void noMatches() {
                retrying = false;

                slashCommandInteractionEvent.getHook().sendMessage(Lang.get("command.music.play.error.no_matches")).queue();
                musicManager.getAlkabot().verbose("No matches!");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                musicManager.getAlkabot().getLogger().warn("Load fail - retry = " + retrying);
                if (retrying) {
                    slashCommandInteractionEvent.getHook().sendMessage(Lang.get("command.music.play.error.generic")).queue();
                    musicManager.getAlkabot().verbose("Failed to load!");
                    retrying = false;
                } else {
                    retrying = true;
                    load(slashCommandInteractionEvent, url, priority, force);
                    musicManager.getAlkabot().verbose("Failed to load! Retrying...");
                }
            }
        });
    }
}