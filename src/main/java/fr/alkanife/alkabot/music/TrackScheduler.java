package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> queue;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public long getQueueDuration() {
        long duration = 0;

        for (AudioTrack audioTrack : queue)
            duration += audioTrack.getDuration();

        return duration;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track, boolean priority) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            if (priority) {
                BlockingQueue<AudioTrack> newQueue = new LinkedBlockingQueue<>();
                newQueue.offer(track);
                newQueue.addAll(queue);
                queue = newQueue;
            } else {
                queue.offer(track);
            }
        }
    }

    public void queuePrioritizePlaylist(AudioPlaylist audioPlaylist) {
        BlockingQueue<AudioTrack> newQueue = new LinkedBlockingQueue<>();
        for (AudioTrack track : audioPlaylist.getTracks())
            if (track != audioPlaylist.getTracks().get(0))
                newQueue.offer(track);

        newQueue.addAll(queue);
        queue = newQueue;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
        //Satania.addPlayedMusics();
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<AudioTrack> queue) {
        this.queue = queue;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioChannel voiceChannel = Alkabot.getGuild().getAudioManager().getConnectedChannel();

        if (voiceChannel != null) {
            if (voiceChannel.getMembers().size() == 1) {
                Music.reset();

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-nomembers-title"));
                embedBuilder.setColor(new Color(193, 0, 0));
                embedBuilder.setDescription(Alkabot.t("jukebox-playing-error-nomembers-desc"));

                Alkabot.getLastCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                return;
            }
        }

        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        //Satania.addFailedToPlay();

        if (Alkabot.getLastCommandChannel() != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("jukebox-playing-error-title"));
            embedBuilder.setColor(new Color(193, 0, 0));
            embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                    + " " + Alkabot.t("jukebox-by") + " [" + track.getInfo().author + "](" + track.getInfo().uri + ")\n\n" +
                    Alkabot.t("jukebox-playing-error-message"));
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");

            Alkabot.getLastCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
