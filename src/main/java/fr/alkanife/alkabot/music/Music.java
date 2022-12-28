package fr.alkanife.alkabot.music;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.Nullable;

public class Music {

    public static void connect(@Nullable Member member) {
        if (!Alkabot.getGuild().getAudioManager().isConnected()) {
            if (member != null) {
                if (member.getVoiceState() != null) {
                    if (member.getVoiceState().getChannel() != null) {
                        Alkabot.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(Alkabot.getAudioPlayer()));
                        Alkabot.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
                        return;
                    }
                }
            }

            connectToFirst();
        }
    }

    private static void connectToFirst() {
        for (VoiceChannel voiceChannel : Alkabot.getGuild().getVoiceChannels()) {
            Alkabot.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(Alkabot.getAudioPlayer()));
            Alkabot.getGuild().getAudioManager().openAudioConnection(voiceChannel);
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