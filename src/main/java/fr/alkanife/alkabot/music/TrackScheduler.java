package fr.alkanife.alkabot.music;

import fr.alkanife.alkabot.Alkabot;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AbstractMusic {

    private BlockingQueue<AlkabotTrack> queue;

    public TrackScheduler(MusicManager musicManager) {
        super(musicManager);
        this.queue = new LinkedBlockingQueue<>();
    }

    public long getQueueDuration() {
        long duration = 0;

        for (AlkabotTrack alkabotTrack : queue)
            if (alkabotTrack.getDuration() < 72000000) // check if stream
                duration += alkabotTrack.getDuration();

        return duration;
    }

    public void queue(AlkabotTrack track, boolean force) {
        if (getMusicManager().getPlayer().getPlayingTrack() == null) {
            getMusicManager().getAlkabotTrackPlayer().play(track);
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

        if (force)
            Alkabot.getMusicManager().goNext();
    }

    public void queuePlaylist(AlkabotTrack firstTrack, List<AlkabotTrack> alkabotTrackList, boolean force) {
        BlockingQueue<AlkabotTrack> newQueue = new LinkedBlockingQueue<>();

        if (!firstTrack.isPriority())
            newQueue.addAll(queue);

        if (getMusicManager().getPlayer().getPlayingTrack() == null) {
            getMusicManager().getAlkabotTrackPlayer().play(firstTrack);

            for (AlkabotTrack a : alkabotTrackList)
                if (!a.getQuery().equals(firstTrack.getQuery()))
                    newQueue.offer(a);
        } else {
            newQueue.addAll(alkabotTrackList);
        }

        if (firstTrack.isPriority())
            newQueue.addAll(queue);

        queue = newQueue;

        if (force)
            Alkabot.getMusicManager().goNext();
    }

    public BlockingQueue<AlkabotTrack> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<AlkabotTrack> queue) {
        this.queue = queue;
    }
}
