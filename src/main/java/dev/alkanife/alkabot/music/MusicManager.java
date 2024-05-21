package dev.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.data.music.Shortcut;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.music.loader.LavaplayerLoader;
import dev.alkanife.alkabot.music.loader.SpotifyLoader;
import dev.alkanife.alkabot.util.StringUtils;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Nullable;

@Getter
public class MusicManager {

    private final Alkabot alkabot;

    @Setter
    private MessageChannelUnion lastMusicCommandChannel;
    @Setter
    private AudioPlayerManager audioPlayerManager;
    @Setter
    private AudioPlayer player;
    @Setter
    private TrackScheduler trackScheduler;
    @Setter
    private TrackListener trackListener;

    @Setter
    private LavaplayerLoader lavaplayerLoader;
    @Setter
    private SpotifyLoader spotifyLoader;
    @Setter
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
        initialize();
    }

    public void disable() {
        player.stopTrack();
        player.destroy();

        alkabot.getGuild().getAudioManager().closeAudioConnection();
    }

    public void goNext() {
        alkabotTrackPlayer.play(trackScheduler.getQueue().poll());
    }

    public void initialize() {
        lavaplayerLoader = new LavaplayerLoader(this);
        spotifyLoader = new SpotifyLoader(this);
        alkabotTrackPlayer = new AlkabotTrackPlayer(this);

        YoutubeAudioSourceManager youtubeSource = new YoutubeAudioSourceManager(true);
        youtubeSource.setPlaylistPageCount(10);

        audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.registerSourceManager(youtubeSource);
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
        audioPlayerManager.registerSourceManager(new NicoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);

        player = audioPlayerManager.createPlayer();
        trackScheduler = new TrackScheduler(this);
        trackListener = new TrackListener(this);
        player.addListener(trackListener);
        player.setVolume(alkabot.getDataManager().getMusicDataManager().getMusicData().getVolume());
    }

    public void nowPlaying(SlashCommandInteractionEvent event) {
        AlkabotTrack current = trackScheduler.getNowPlaying();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(
                Lang.t("command.music.nowplaying.success.title")
                        .parseTrack(current)
                        .getValue()
        );
        embed.setThumbnail(
                Lang.t("command.music.nowplaying.success.icon")
                        .parseMemberAvatars(event.getMember())
                        .parseGuildAvatar(event.getGuild())
                        .parseBotAvatars(alkabot)
                        .parseTrackThumbnail(current)
                        .getValue()
        );
        embed.setColor(Lang.getColor("command.music.nowplaying.success.color"));
        embed.setDescription(
                Lang.t("command.music.nowplaying.success.description")
                        .parseTrack(current)
                        .getValue()
        );

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }

    public void playCommand(SlashCommandInteractionEvent event, final String commandSource, int position, boolean skipCurrent) {
        alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        alkabot.getMusicManager().connect(event.getMember());

        // Parse query
        OptionMapping queryOption = event.getOption("query");

        if (queryOption == null) {
            event.reply(Lang.get("command.music" + commandSource + ".error.invalid_query")).queue();
            return;
        }

        String query = queryOption.getAsString();

        if (query.isBlank() || query.isEmpty()) {
            event.reply(Lang.get("command.music" + commandSource + ".error.invalid_query")).queue();
            return;
        }

        if (query.startsWith("https://open.spotify.com/playlist")) {
            if (alkabot.isSpotifySupport()) {
                event.deferReply().queue();
                alkabot.getMusicManager().getSpotifyLoader().load(event, commandSource, query, position, skipCurrent);
            } else {
                event.reply(Lang.get("command.music." + commandSource + ".error.no_spotify_support")).queue();
            }
        } else {
            if (!StringUtils.isURL(query)) {
                Shortcut shortcut = alkabot.getDataManager().getMusicDataManager().getShortcutByName(query);

                if (shortcut == null) {
                    query = "ytsearch: " + query;
                } else {
                    if (StringUtils.isURL(shortcut.getQuery()))
                        query = shortcut.getQuery();
                    else
                        query = "ytsearch: " + shortcut.getQuery();
                }
            }

            event.deferReply().queue();
            alkabot.getMusicManager().getLavaplayerLoader().load(event, commandSource, query, position, skipCurrent);
        }
    }
}