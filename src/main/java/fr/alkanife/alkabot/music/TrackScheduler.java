package fr.alkanife.alkabot.music;

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
            if (alkabotTrack.getDuration() < 72000000)
                duration += alkabotTrack.getDuration();

        return duration;
    }

    public void queue(AlkabotTrack track) {
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
    }

    public void queuePlaylist(AlkabotTrack firstTrack, List<AlkabotTrack> alkabotTrackList) {
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
    }

    public BlockingQueue<AlkabotTrack> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<AlkabotTrack> queue) {
        this.queue = queue;
    }
}
