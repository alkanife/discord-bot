package fr.alkanife.alkabot.events;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.music.Music;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class Events extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        Alkabot.getLogger().info("Checking for Discord environment");

        try {
            // Check for guild
            Guild guild = readyEvent.getJDA().getGuildById(Alkabot.getConfig().getGuild_id());
            if (guild == null) {
                Alkabot.getLogger().error("The Discord guild '" + Alkabot.getConfig().getGuild_id() + "' was not found");
                readyEvent.getJDA().shutdownNow();
                System.exit(0);
            }
            Alkabot.setGuild(guild);

            Alkabot.getLogger().info("Updating commands");
            updateCommands();

            Alkabot.getLogger().info("Initializing music");
            Music.initialize();

            Alkabot.getLogger().info("Ready!");

            //
            // LOG SUCCESSFUL CONNECTION
            //
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-power-on-title"));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("`[Alkabot ").append(Alkabot.getVersion()).append("]`\n\n").append(Alkabot.t("logs-power-on-help"));
            embedBuilder.setDescription(stringBuilder.toString());
            String[] okMemes = Alkabot.t("ok-memes").split("\n");
            int random = new Random().nextInt(okMemes.length);
            String okmeme = okMemes[random];
            embedBuilder.setThumbnail(okmeme);
            embedBuilder.setColor(new Color(150, 224, 136));
            if (Alkabot.getConfig().getLogs().isAdmin())
                Alkabot.discordLog(embedBuilder.build());
        } catch (Exception exception) {
            exception.printStackTrace();
            readyEvent.getJDA().shutdownNow();
            System.exit(0);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent slashCommandInteractionEvent) {
        if (Alkabot.getConfig().isAdmin_only()) {
            Member member = slashCommandInteractionEvent.getMember();

            if (member != null)
                if (!Alkabot.getConfig().getAdministrators_id().contains(member.getId()))
                    return;
        }

        Alkabot.getCommandHandler().handleSlash(slashCommandInteractionEvent);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent messageReceivedEvent) {
        // eastereggs
        if (messageReceivedEvent.getChannelType().equals(ChannelType.TEXT)) {
            String content = messageReceivedEvent.getMessage().getContentDisplay().toLowerCase(Locale.ROOT).replaceAll(",", "");

            switch (content) {
                case "alexa play despacito" -> {
                    Alkabot.getAudioPlayerManager().loadItemOrdered(Alkabot.getAudioPlayer(), "https://www.youtube.com/watch?v=kJQP7kiw5Fk", new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack audioTrack) {
                            Music.connectAndPlay(messageReceivedEvent.getGuild(), messageReceivedEvent.getMember(), audioTrack, true);

                            messageReceivedEvent.getMessage().reply("Sí, sabes que ya llevo un rato mirándote\n" +
                                    "Tengo que bailar contigo hoy (DY)\n" +
                                    "Vi que tu mirada ya estaba llamándome\n" +
                                    "Muéstrame el camino que yo voy\n\n" +
                                    "Oh, tú, tú eres el imán y yo soy el metal\n" +
                                    "Me voy acercando y voy armando el plan\n" +
                                    "Solo con pensarlo se acelera el pulso (oh yeah)\n\n" +
                                    "Ya, ya me estás gustando más de lo normal\n" +
                                    "Todos mis sentidos van pidiendo más\n" +
                                    "Esto hay que tomarlo sin ningún apuro\n\n" +
                                    "Despacito\n" +
                                    "Quiero respirar tu cuello despacito\n" +
                                    "Deja que te diga cosas al oído\n" +
                                    "Para que te acuerdes si no estás conmigo\n" +
                                    "Despacito\n" +
                                    "Quiero desnudarte a besos despacito\n" +
                                    "Firmar las paredes de tu laberinto\n" +
                                    "Y hacer de tu cuerpo todo un manuscrito \uD83C\uDFB6").queue();
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist audioPlaylist) {}
                        @Override
                        public void noMatches() {}
                        @Override
                        public void loadFailed(FriendlyException e) {}
                    });
                }
            }
            return;
        }
        // ---------

        // If in DM, handle admin commands
        if (!messageReceivedEvent.getChannelType().equals(ChannelType.PRIVATE))
            return;

        // Deny if not administrator
        if (!Alkabot.getConfig().getAdministrators_id().contains(messageReceivedEvent.getAuthor().getId()))
            return;

        Alkabot.getCommandHandler().handleAdmin(messageReceivedEvent);
    }

    private void updateCommands() {
        //
        // MUSIC
        //
        SlashCommandData jukebox = Commands.slash("jukebox", Alkabot.t("jukebox-command-description"));

        SubcommandData jukeboxPlay = new SubcommandData("play", Alkabot.t("jukebox-command-play-description"))
                .addOption(OptionType.STRING, "input", Alkabot.t("jukebox-command-play-input-description"), true);

        SubcommandData jukeboxPlaynext = new SubcommandData("play_next", Alkabot.t("jukebox-command-play-priority-description"))
                .addOption(OptionType.STRING, "input", Alkabot.t("jukebox-command-play-input-description"), true);

        SubcommandData jukeboxSkip = new SubcommandData("skip", Alkabot.t("jukebox-command-skip-description"))
                .addOption(OptionType.INTEGER, "input", Alkabot.t("jukebox-command-skip-input-description"), false);

        SubcommandData jukeboxRemove = new SubcommandData("remove", Alkabot.t("jukebox-command-remove-description"))
                .addOption(OptionType.INTEGER, "input", Alkabot.t("jukebox-command-remove-input-description"), false);

        SubcommandData jukeboxQueue = new SubcommandData("queue", Alkabot.t("jukebox-command-queue-description"))
                .addOption(OptionType.INTEGER, "input", Alkabot.t("jukebox-command-queue-input-description"), false);

        SubcommandData jukeboxShuffle = new SubcommandData("shuffle", Alkabot.t("jukebox-command-shuffle-description"));
        SubcommandData jukeboxStop = new SubcommandData("stop", Alkabot.t("jukebox-command-stop-description"));
        SubcommandData jukeboxClear = new SubcommandData("clear", Alkabot.t("jukebox-command-clear-description"));
        jukebox.addSubcommands(jukeboxPlay, jukeboxPlaynext, jukeboxSkip, jukeboxShuffle, jukeboxQueue, jukeboxStop, jukeboxClear, jukeboxRemove);


        SlashCommandData playlists = Commands.slash("playlists", Alkabot.t("playlists-command-description"));

        SubcommandData playlistsAdd = new SubcommandData("add", Alkabot.t("playlists-command-add-description"))
                .addOption(OptionType.STRING, "name", Alkabot.t("playlists-command-add-name-description"), true)
                .addOption(OptionType.STRING, "url", Alkabot.t("playlists-command-add-url-description"), true);

        SubcommandData playlistsRemove = new SubcommandData("remove", Alkabot.t("playlists-command-remove-description"))
                .addOption(OptionType.STRING, "name", Alkabot.t("playlists-command-remove-name-description"), true);

        SubcommandData playlistsList = new SubcommandData("list", Alkabot.t("playlists-command-list-description"));

        playlists.addSubcommands(playlistsAdd, playlistsRemove, playlistsList);

        //
        // INFO
        //
        CommandData info = Commands.slash("info", Alkabot.t("info-command-description"));
        CommandData serverinfo = Commands.slash("serverinfo", Alkabot.t("serverinfo-command-description"));
        CommandData memberinfo = Commands.slash("memberinfo", Alkabot.t("memberinfo-command-description"))
                .addOption(OptionType.USER, "input", Alkabot.t("memberinfo-command-input-description"), true);
        CommandData emoteinfo = Commands.slash("emoteinfo", Alkabot.t("emoteinfo-command-description"))
                .addOption(OptionType.STRING, "input", Alkabot.t("emoteinfo-command-input-description"), true);

        //
        // UTILITIES
        //
        CommandData copy = Commands.slash("copy", Alkabot.t("copy-command-description"))
                .addOption(OptionType.STRING, "input", Alkabot.t("copy-command-input-description"), true);

        //
        // ADD ENABLED COMMANDS
        //
        Collection<CommandData> commandDataCollection = new ArrayList<>();

        if (Alkabot.getConfig().getCommands().isMusic()) {
            commandDataCollection.add(jukebox);
            commandDataCollection.add(playlists);
        }

        if (Alkabot.getConfig().getCommands().isInfo()) {
            commandDataCollection.add(serverinfo);
            commandDataCollection.add(memberinfo);
            commandDataCollection.add(emoteinfo);
            commandDataCollection.add(info);
        }

        if (Alkabot.getConfig().getCommands().isUtilities())
            commandDataCollection.add(copy);

        //push
        Alkabot.getGuild().updateCommands().addCommands(commandDataCollection).queue();
    }

}
