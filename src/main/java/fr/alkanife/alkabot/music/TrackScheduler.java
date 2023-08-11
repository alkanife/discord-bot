package fr.alkanife.alkabot.music;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AbstractMusic {

    @Getter @Setter
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
        if (musicManager.getPlayer().getPlayingTrack() == null) {
            musicManager.getAlkabotTrackPlayer().play(track);
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
            musicManager.getAlkabot().getMusicManager().goNext();
    }

    public void queuePlaylist(AlkabotTrack firstTrack, List<AlkabotTrack> alkabotTrackList, boolean force) {
        BlockingQueue<AlkabotTrack> newQueue = new LinkedBlockingQueue<>();

        if (!firstTrack.isPriority())
            newQueue.addAll(queue);

        if (musicManager.getPlayer().getPlayingTrack() == null) {
            musicManager.getAlkabotTrackPlayer().play(firstTrack);

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
            musicManager.goNext();
    }
}
