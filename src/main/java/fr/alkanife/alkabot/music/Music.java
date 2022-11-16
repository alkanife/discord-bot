package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

public class Music {

    public static void loadAndPlay(SlashCommandInteractionEvent slashCommandInteractionEvent, final String url, boolean priority) {
        slashCommandInteractionEvent.deferReply().queue();
        Alkabot.getAudioPlayerManager().loadItemOrdered(Alkabot.getAudioPlayer(), url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                //Satania.addAddedMusics();

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(Alkabot.t("jukebox-command-play-added-title") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
                embedBuilder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ") " + Alkabot.t("jukebox-by")
                        + " [" + track.getInfo().author + "](" + track.getInfo().uri + ") " + Alkabot.musicDuration(track.getDuration()) +
                        (priority ? "" : ("\n\n" + (Alkabot.t("jukebox-command-play-added-position") + " `" + (Alkabot.getTrackScheduler().getQueue().size() + 1) + "`"))));
                embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");

                slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

                connect(slashCommandInteractionEvent.getGuild(), slashCommandInteractionEvent.getMember());
                Alkabot.getTrackScheduler().queue(track, priority);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = playlist.getTracks().get(0);

                connect(slashCommandInteractionEvent.getGuild(), slashCommandInteractionEvent.getMember());

                if (url.startsWith("ytsearch")) {
                    //Satania.addAddedMusics();

                    Alkabot.getTrackScheduler().queue(firstTrack, priority);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-command-play-added-title") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
                    embedBuilder.setDescription("[" + firstTrack.getInfo().title + "](" + firstTrack.getInfo().uri + ") " + Alkabot.t("jukebox-by")
                            + " [" + firstTrack.getInfo().author + "](" + firstTrack.getInfo().uri + ") " + Alkabot.musicDuration(firstTrack.getDuration()) +
                            (priority ? "" : ("\n\n" + (Alkabot.t("jukebox-command-play-added-position") + " `" + (Alkabot.getTrackScheduler().getQueue().size() + 1) + "`"))));
                    embedBuilder.setThumbnail("https://img.youtube.com/vi/" + firstTrack.getIdentifier() + "/0.jpg");

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                } else {
                    //Satania.addAddedPlaylists();

                    Alkabot.getTrackScheduler().queuePlaylist(firstTrack, playlist, priority);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Alkabot.t("jukebox-command-play-playlist-added") + " " + (priority ? Alkabot.t("jukebox-command-priority") : ""));
                    embedBuilder.setDescription("[" + playlist.getName() + "](" + url + ")\n\n" +
                            Alkabot.t("jukebox-command-play-playlist-entries") + " `" + playlist.getTracks().size() + "`\n" +
                            Alkabot.t("jukebox-command-play-playlist-newtime") + " `" + Alkabot.playlistDuration(Alkabot.getTrackScheduler().getQueueDuration()) + "`");

                    embedBuilder.setThumbnail("https://img.youtube.com/vi/" + firstTrack.getIdentifier() + "/0.jpg");

                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("jukebox-command-play-nomatches")).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                slashCommandInteractionEvent.getHook().sendMessage(Alkabot.t("jukebox-command-play-error")).queue();
                //Amiria.getLogger().error("could not play", exception);
            }
        });
    }

    public static void connect(@Nullable Guild guild, Member member) {
        if (guild == null)
            return;

        if (!guild.getAudioManager().isConnected()) {
            if (member != null) {
                if (member.getVoiceState() != null) {
                    if (member.getVoiceState().getChannel() != null) {
                        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(Alkabot.getAudioPlayer()));
                        guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
                    } else {
                        connectToFirst(guild);
                    }
                } else {
                    connectToFirst(guild);
                }
            } else {
                connectToFirst(guild);
            }
        }
    }

    private static void connectToFirst(Guild guild) {
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(Alkabot.getAudioPlayer()));
            guild.getAudioManager().openAudioConnection(voiceChannel);
            break;
        }
    }

    public static void reset() {
        disable();
        initialize();
    }

    public static void disable() {
        Alkabot.getAudioPlayer().stopTrack();
        Alkabot.getAudioPlayer().destroy();

        Alkabot.getGuild().getAudioManager().closeAudioConnection();
    }

    public static void initialize() {
        Alkabot.setAudioPlayerManager(new DefaultAudioPlayerManager());
        AudioSourceManagers.registerRemoteSources(Alkabot.getAudioPlayerManager());
        AudioSourceManagers.registerLocalSource(Alkabot.getAudioPlayerManager());

        Alkabot.setAudioPlayer(Alkabot.getAudioPlayerManager().createPlayer());
        Alkabot.setTrackScheduler(new TrackScheduler(Alkabot.getAudioPlayer()));
        Alkabot.getAudioPlayer().addListener(Alkabot.getTrackScheduler());
    }

}