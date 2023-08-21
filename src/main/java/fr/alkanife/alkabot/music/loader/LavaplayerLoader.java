package fr.alkanife.alkabot.music.loader;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.lang.Lang;
import fr.alkanife.alkabot.music.*;
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

    // If position = 0, the track will go to the end of the queue
    public void load(SlashCommandInteractionEvent event, final String commandSource, final String query, final int position, boolean skipCurrent) {
        musicManager.getAlkabot().getLogger().debug("Loading audio from query '" + query + "' (pos=" + position + ", skipCurrent=" + skipCurrent + ")");

        musicManager.getAudioPlayerManager().loadItemOrdered(musicManager.getPlayer(), query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                retrying = false;

                String id = "";
                if (event.getMember() != null)
                    id = event.getMember().getId();

                AlkabotTrack alkabotTrack = new AlkabotTrack(track, id);
                int pos = musicManager.getAlkabot().getMusicManager().getTrackScheduler().queue(alkabotTrack, position, skipCurrent);

                EmbedBuilder embed = MusicUtils.createTackAddedEmbed(commandSource, event, alkabotTrack, musicManager, pos);
                event.getHook().sendMessageEmbeds(embed.build()).queue();

                musicManager.getAlkabot().getLogger().debug("Track loaded: " + alkabotTrack.toString());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                retrying = false;

                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = playlist.getTracks().get(0);

                String id = "";
                if (event.getMember() != null)
                    id = event.getMember().getId();

                if (query.startsWith("ytsearch")) {
                    AlkabotTrack alkabotTrack = new AlkabotTrack(firstTrack, id);
                    int pos = musicManager.getTrackScheduler().queue(alkabotTrack, position, skipCurrent);

                    EmbedBuilder embed = MusicUtils.createTackAddedEmbed(commandSource, event, alkabotTrack, musicManager, pos);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();

                    musicManager.getAlkabot().getLogger().debug("Track loaded (youtube search) pos " + pos + ": " + alkabotTrack.toString());
                } else {
                    List<AlkabotTrack> alkabotTrackList = new ArrayList<>();

                    for (AudioTrack audioTrack : playlist.getTracks())
                        alkabotTrackList.add(new AlkabotTrack(audioTrack, id));

                    AlkabotTrack firstAlkabotTrack = new AlkabotTrack(firstTrack, id);

                    AlkabotTrackPlaylist alkabotTrackPlaylist = new AlkabotTrackPlaylist();
                    alkabotTrackPlaylist.setTitle(playlist.getName());
                    alkabotTrackPlaylist.setUrl(firstAlkabotTrack.getUrl());
                    alkabotTrackPlaylist.setThumbnailUrl(firstAlkabotTrack.getThumbUrl());
                    alkabotTrackPlaylist.setFirstTrack(firstAlkabotTrack);
                    alkabotTrackPlaylist.setTracks(alkabotTrackList);

                    int pos = musicManager.getTrackScheduler().queuePlaylist(alkabotTrackPlaylist, position, skipCurrent);

                    EmbedBuilder embed = MusicUtils.createPlaylistAddedEmbed(commandSource, event, alkabotTrackPlaylist, musicManager, pos);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();

                    musicManager.getAlkabot().getLogger().debug("Playlist '" + playlist.getName() + "' loaded! (" + playlist.getTracks().size() + " tracks) pos " + pos);
                }
            }

            @Override
            public void noMatches() {
                retrying = false;

                event.getHook().sendMessage(Lang.get("command.music." + commandSource + ".error.no_matches")).queue();
                musicManager.getAlkabot().getLogger().debug("No matches!");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                musicManager.getAlkabot().getLogger().warn("Load fail - retry = " + retrying);
                if (retrying) {
                    event.getHook().sendMessage(Lang.get("command.music." + commandSource + ".error.generic")).queue();
                    musicManager.getAlkabot().getLogger().debug("Failed to load!");
                    retrying = false;
                } else {
                    retrying = true;
                    load(event, commandSource, query, position, skipCurrent);
                    musicManager.getAlkabot().getLogger().debug("Failed to load! Retrying...");
                }
            }
        });
    }
}