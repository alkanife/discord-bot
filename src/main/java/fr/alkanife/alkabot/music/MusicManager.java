package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.music.loader.LavaplayerLoader;
import fr.alkanife.alkabot.music.loader.SpotifyLoader;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.jetbrains.annotations.Nullable;

public class MusicManager {

    private MessageChannelUnion lastMusicCommandChannel;
    private AudioPlayerManager audioPlayerManager;
    private AudioPlayer player;
    private TrackScheduler trackScheduler;
    private TrackListener trackListener;

    private LavaplayerLoader lavaplayerLoader;
    private SpotifyLoader spotifyLoader;
    private AlkabotTrackPlayer alkabotTrackPlayer;

    public MusicManager() {}

    public void connect(@Nullable Member member) {
        if (!Alkabot.getGuild().getAudioManager().isConnected()) {
            if (member != null) {
                if (member.getVoiceState() != null) {
                    if (member.getVoiceState().getChannel() != null) {
                        Alkabot.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                        Alkabot.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
                        return;
                    }
                }
            }

            connectToFirst();
        }
    }

    private void connectToFirst() {
        for (VoiceChannel voiceChannel : Alkabot.getGuild().getVoiceChannels()) {
            Alkabot.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
            Alkabot.getGuild().getAudioManager().openAudioConnection(voiceChannel);
            break;
        }
    }

    public void reset() {
        disable();
        initialize(true);
    }

    public void disable() {
        player.stopTrack();
        player.destroy();

        Alkabot.getGuild().getAudioManager().closeAudioConnection();
    }

    public void goNext() {
        alkabotTrackPlayer.play(trackScheduler.getQueue().poll());
    }

    public void initialize(boolean reload) {
        if(!reload)
            Alkabot.getLogger().info("Initializing music...");

        lavaplayerLoader = new LavaplayerLoader(this);
        spotifyLoader = new SpotifyLoader(this);
        alkabotTrackPlayer = new AlkabotTrackPlayer(this);

        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);

        player = audioPlayerManager.createPlayer();
        trackScheduler = new TrackScheduler(this);
        trackListener = new TrackListener(this);
        player.addListener(trackListener);
        player.setVolume(70);
    }

    public MessageChannelUnion getLastMusicCommandChannel() {
        return lastMusicCommandChannel;
    }

    public void setLastMusicCommandChannel(MessageChannelUnion lastMusicCommandChannel) {
        this.lastMusicCommandChannel = lastMusicCommandChannel;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public void setAudioPlayerManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public void setTrackScheduler(TrackScheduler trackScheduler) {
        this.trackScheduler = trackScheduler;
    }

    public AlkabotTrackPlayer getAlkabotTrackPlayer() {
        return alkabotTrackPlayer;
    }

    public void setAlkabotTrackPlayer(AlkabotTrackPlayer alkabotTrackPlayer) {
        this.alkabotTrackPlayer = alkabotTrackPlayer;
    }

    public LavaplayerLoader getLavaplayerLoader() {
        return lavaplayerLoader;
    }

    public void setLavaplayerLoader(LavaplayerLoader lavaplayerLoader) {
        this.lavaplayerLoader = lavaplayerLoader;
    }

    public SpotifyLoader getSpotifyLoader() {
        return spotifyLoader;
    }

    public void setSpotifyLoader(SpotifyLoader spotifyLoader) {
        this.spotifyLoader = spotifyLoader;
    }
}