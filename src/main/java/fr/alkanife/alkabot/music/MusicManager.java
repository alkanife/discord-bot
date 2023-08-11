package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.music.loader.LavaplayerLoader;
import fr.alkanife.alkabot.music.loader.SpotifyLoader;
import fr.alkanife.alkabot.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

public class MusicManager {

    @Getter
    private final Alkabot alkabot;

    @Getter @Setter
    private MessageChannelUnion lastMusicCommandChannel;
    @Getter @Setter
    private AudioPlayerManager audioPlayerManager;
    @Getter @Setter
    private AudioPlayer player;
    @Getter @Setter
    private TrackScheduler trackScheduler;
    @Getter @Setter
    private TrackListener trackListener;

    @Getter @Setter
    private LavaplayerLoader lavaplayerLoader;
    @Getter @Setter
    private SpotifyLoader spotifyLoader;
    @Getter @Setter
    private AlkabotTrackPlayer alkabotTrackPlayer;

    public MusicManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public void connect(@Nullable Member member) {
        if (!alkabot.getGuild().getAudioManager().isConnected()) {
            if (member != null) {
                if (member.getVoiceState() != null) {
                    if (member.getVoiceState().getChannel() != null) {
                        alkabot.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                        alkabot.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
                        return;
                    }
                }
            }

            connectToFirst();
        }
    }

    private void connectToFirst() {
        for (VoiceChannel voiceChannel : alkabot.getGuild().getVoiceChannels()) {
            alkabot.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
            alkabot.getGuild().getAudioManager().openAudioConnection(voiceChannel);
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

        alkabot.getGuild().getAudioManager().closeAudioConnection();
    }

    public void goNext() {
        alkabotTrackPlayer.play(trackScheduler.getQueue().poll());
    }

    public void initialize(boolean reload) {
        if(!reload)
            alkabot.getLogger().info("Initializing music...");

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

    public void play(SlashCommandInteractionEvent event, boolean priority, boolean force) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        event.deferReply().queue();

        alkabot.getMusicManager().connect(event.getMember());

        String input = event.getOption("input").getAsString();

        if (input.startsWith("https://open.spotify.com/playlist")) {
            if (alkabot.isSpotifySupport())
                alkabot.getMusicManager().getSpotifyLoader().load(event, input, priority, force);
            else
                event.reply("command.music.play.error.no_spotify_support").queue();
        } else {
            if (!StringUtils.isURL(input)) {
                Shortcut shortcut = alkabot.getShortcutManager().getShortcut(input);

                if (shortcut == null)
                    input = "ytsearch: " + input;
                else
                if (StringUtils.isURL(shortcut.getQuery()))
                    input = shortcut.getQuery();
                else
                    input = "ytsearch: " + shortcut.getQuery();
            }

            alkabot.getMusicManager().getLavaplayerLoader().load(event, input, priority, force);
        }
    }
}