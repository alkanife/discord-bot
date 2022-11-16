package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public long getQueueDuration() {
        long duration = 0;

        for (AudioTrack audioTrack : queue)
            if (audioTrack.getDuration() < 72000000)
                duration += audioTrack.getDuration();

        return duration;
    }

    public void queue(AudioTrack track, boolean priority) {
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

    public void queuePlaylist(AudioTrack firstTrack, AudioPlaylist audioPlaylist, boolean priority) {
        BlockingQueue<AudioTrack> newQueue = new LinkedBlockingQueue<>();

        if (!priority)
            newQueue.addAll(queue);

        if (player.startTrack(firstTrack, true)) {
            for (AudioTrack audioTrack : audioPlaylist.getTracks())
                if (!audioTrack.getIdentifier().equals(firstTrack.getIdentifier()))
                    newQueue.offer(audioTrack);
        } else {
            newQueue.addAll(audioPlaylist.getTracks());
        }

        if (priority)
            newQueue.addAll(queue);

        queue = newQueue;
    }

    public void nextTrack() {
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
                embedBuilder.setColor(Colors.BIG_RED);
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
            embedBuilder.setColor(Colors.BIG_RED);
            embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                    + " " + Alkabot.t("jukebox-by") + " [" + track.getInfo().author + "](" + track.getInfo().uri + ")\n\n" +
                    Alkabot.t("jukebox-playing-error-message"));
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");

            Alkabot.getLastCommandChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
