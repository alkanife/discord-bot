package fr.alkanife.alkabot.music;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AbstractMusic {

    @Getter @Setter
    private BlockingQueue<AlkabotTrack> queue;

    @Getter @Setter
    private AlkabotTrack nowPlaying;

    public TrackScheduler(MusicManager musicManager) {
        super(musicManager);
        this.queue = new LinkedBlockingQueue<>();
    }

    public long getQueueDuration() {
        long duration = 0;

        for (AlkabotTrack alkabotTrack : queue)
            duration += alkabotTrack.getDuration();

        return duration;
    }

    // Return the position the track has been added
    // Todo: optimise this
    public int queue(AlkabotTrack track, int position, boolean skipAfter) {
        if (musicManager.getPlayer().getPlayingTrack() == null) {
            musicManager.getAlkabotTrackPlayer().play(track);
            return 1;
        }

        BlockingQueue<AlkabotTrack> newQueue = new LinkedBlockingQueue<>();
        int positioned = 1;

        if (position <= 0) { // place to first position
            newQueue.offer(track);
            newQueue.addAll(queue);
        } else { // else place to position
            if (position > queue.size()+1) {
                newQueue.addAll(queue);
                newQueue.offer(track);
                positioned = queue.size()+1;
            } else {
                int index = 1;
                for (AlkabotTrack t : queue) {
                    if (index == position) {
                        newQueue.offer(track);
                        positioned = index;
                    }

                    newQueue.offer(t);
                    index++;
                }
            }
        }

        queue = newQueue;

        if (skipAfter)
            musicManager.getAlkabot().getMusicManager().goNext();

        return positioned;
    }

    // Todo: optimise this
    public int queuePlaylist(AlkabotTrackPlaylist playlist, int position, boolean skipAfter) {
        BlockingQueue<AlkabotTrack> newQueue = new LinkedBlockingQueue<>();

        if (musicManager.getPlayer().getPlayingTrack() == null) {
            musicManager.getAlkabotTrackPlayer().play(playlist.getFirstTrack());

            for (AlkabotTrack a : playlist.getTracks())
                if (!a.getQuery().equals(playlist.getFirstTrack().getQuery()))
                    newQueue.offer(a);

            queue = newQueue;
            return 1;
        }

        int positioned = 1;

        if (position <= 0) { // place to first position
            newQueue.addAll(playlist.getTracks());
            newQueue.addAll(queue);
        } else { // else place to position
            if (position > queue.size()+1) {
                newQueue.addAll(queue);
                newQueue.addAll(playlist.getTracks());
                positioned = queue.size()+1;
            } else {
                int index = 1;
                for (AlkabotTrack t : queue) {
                    if (index == position) {
                        newQueue.addAll(playlist.getTracks());
                        positioned = index;
                    }

                    newQueue.offer(t);
                    index++;
                }
            }
        }

        queue = newQueue;

        if (skipAfter)
            musicManager.getAlkabot().getMusicManager().goNext();

        return positioned;
    }
}
