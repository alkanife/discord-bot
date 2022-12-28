package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private BlockingQueue<AlkabotTrack> queue;

    private List<String> retriedTracks = new ArrayList<>();

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public long getQueueDuration() {
        long duration = 0;

        for (AlkabotTrack alkabotTrack : queue)
            if (alkabotTrack.getDuration() < 72000000)
                duration += alkabotTrack.getDuration();

        return duration;
    }

    public void queue(AlkabotTrack track) {
        if (player.getPlayingTrack() == null) {
            MusicLoader.play(track);
            return;
        }

        if (track.isPriority()) {
            BlockingQueue<AlkabotTrack> newQueue = new LinkedBlockingQueue<>();
            newQueue.offer(track);
            newQueue.addAll(queue);
            queue = newQueue;
        } else {
            queue.offer(track);
        }
    }

    public void queuePlaylist(AlkabotTrack firstTrack, List<AlkabotTrack> alkabotTrackList) {
        BlockingQueue<AlkabotTrack> newQueue = new LinkedBlockingQueue<>();

        if (!firstTrack.isPriority())
            newQueue.addAll(queue);

        if (player.getPlayingTrack() == null) {
            MusicLoader.play(firstTrack);

            for (AlkabotTrack a : alkabotTrackList)
                if (!a.getQuery().equals(firstTrack.getQuery()))
                    newQueue.offer(a);
        } else {
            newQueue.addAll(alkabotTrackList);
        }

        if (firstTrack.isPriority())
            newQueue.addAll(queue);

        queue = newQueue;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<AlkabotTrack> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<AlkabotTrack> queue) {
        this.queue = queue;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioChannel voiceChannel = Alkabot.getGuild().getAudioManager().getConnectedChannel();

        if (voiceChannel != null) {
            if (voiceChannel.getMembers().size() == 1) {
                Music.reset();

                if (Alkabot.getLastCommandChannel() != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-nomembers-title"));
                    embedBuilder.setColor(Colors.BIG_RED);
                    embedBuilder.setDescription(Alkabot.t("jukebox-playing-error-nomembers-desc"));

                    Alkabot.getLastCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }
                return;
            }
        }

        if (endReason.mayStartNext)
            MusicLoader.play(Alkabot.getTrackScheduler().getQueue().poll());
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        if (Alkabot.getLastCommandChannel() != null) {

            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (retriedTracks.contains(track.getInfo().title)) {
                embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-title"));
                embedBuilder.setColor(Colors.BIG_RED);
                embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                        + " " + Alkabot.t("jukebox-by") + " [" + track.getInfo().author + "](" + track.getInfo().uri + ")\n\n" +
                        Alkabot.t("jukebox-playing-error-message"));
                embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");
            } else {
                embedBuilder.setTitle(Alkabot.t("jukebox-command-play-added-title") + " " + Alkabot.t("jukebox-command-priority"));
                embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ") " + Alkabot.t("jukebox-by")
                        + " [" + track.getInfo().author + "](" + track.getInfo().uri + ") " + Alkabot.musicDuration(track.getDuration()) +
                        "\n\n" + Alkabot.t("jukebox-playing-retrying"));
                embedBuilder.setColor(Colors.CYAN);
                embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");

                retriedTracks.add(track.getInfo().title);
            }

            if (Alkabot.getLastCommandChannel() != null)
                Alkabot.getLastCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
