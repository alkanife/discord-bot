package fr.alkanife.alkabot.commands;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.AlkabotUtils;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.MemoryUtils;
import fr.alkanife.alkabot.commands.utils.Command;
import fr.alkanife.alkabot.configuration.Configuration;
import fr.alkanife.alkabot.configuration.ConfigurationLoader;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.music.Music;
import fr.alkanife.alkabot.music.playlists.Playlist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.Presence;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Locale;

public class AdminCommands {

    public static void help(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().reply("Administrative commands:\n" +
                "- `stop`: Shutdown the bot\n" +
                "- `config`: Displays the current configuration\n" +
                "- `status`: Displays bot uptime and system memory usage\n" +
                "- `welcome_messages`: Displays welcome messages\n" +
                "- `play_lists`: Displays playlists\n" +
                "- `reload translations`: Reload the lang.yml file\n" +
                "- `reload configuration`: Reload the configuration from file\n" +
                "- `reload playlists`: Reload playlists from file\n" +
                "- `reload music`: Reset music player").queue();
    }

    @Command(name = "test", administrative = true)
    public void test(MessageReceivedEvent messageReceivedEvent) {

    }

    @Command(name = "welcome_messages", administrative = true)
    public void welcomeMessages(MessageReceivedEvent messageReceivedEvent) {
        String[] welcomeMessages = Alkabot.t("welcome-messages", "member").split("\n");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("```yaml\n[WELCOME MESSAGES]\n\n");

        stringBuilder.append("Size: ").append(welcomeMessages.length).append("\n\n");

        for (String w : welcomeMessages)
            stringBuilder.append("- ").append(w).append("\n");

        stringBuilder.append("\n```");

        messageReceivedEvent.getMessage().reply(stringBuilder.toString()).queue();
    }

    @Command(name = "play_lists", administrative = true)
    public void play_lists(MessageReceivedEvent messageReceivedEvent) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("```yaml\n[PLAYLISTS]\n\n");

        stringBuilder.append("Size: ").append(Alkabot.getPlaylists().size()).append("\n\n");

        for (Playlist playlist : Alkabot.getPlaylists())
            stringBuilder.append("\"").append(playlist.getName()).append("\":\n")
                    .append(" - Added by: ").append(playlist.getUser_id()).append("\n")
                    .append(" - URL: ").append(playlist.getUrl()).append("\n");

        stringBuilder.append("```");

        messageReceivedEvent.getMessage().reply(stringBuilder.toString()).queue();
    }

    @Command(name = "status", administrative = true)
    public void status(MessageReceivedEvent messageReceivedEvent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("```yaml\n[STATUS]\n\n");

        SelfUser selfUser = messageReceivedEvent.getJDA().getSelfUser();
        stringBuilder.append("Client: ").append(selfUser.getAsTag()).append(" [").append(selfUser.getId()).append("]\n");

        Duration duration = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        String formattedElapsedTime = String.format("%d days, %02d hours, %02d minutes, %02d seconds",
                duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
        stringBuilder.append("Uptime: ").append(formattedElapsedTime).append("\n\n");

        stringBuilder.append("Memory usage:\n")
                .append(" - Max: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getMaxMemory())).append("\n")
                .append(" - Used: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getUsedMemory())).append("\n")
                .append(" - Total: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getTotalMemory())).append("\n")
                .append(" - Free: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getFreeMemory()));

        stringBuilder.append("\n```");

        messageReceivedEvent.getMessage().reply(stringBuilder.toString()).queue();
    }

    @Command(name = "reload", administrative = true)
    public void reload(MessageReceivedEvent messageReceivedEvent) {
        String content = messageReceivedEvent.getMessage().getContentDisplay().toLowerCase(Locale.ROOT);

        String[] args = content.split(" ");

        if (args.length == 0)
            return;

        switch (args[1]) {
            case "configuration":
                messageReceivedEvent.getMessage().reply("Reloading configuration").queue(message -> {
                    try {
                        ConfigurationLoader configurationLoader = new ConfigurationLoader(true);

                        if (configurationLoader.getConfiguration() == null)
                            return;

                        Alkabot.setConfig(configurationLoader.getConfiguration());

                        Presence presence = Alkabot.getJDA().getPresence();

                        presence.setStatus(OnlineStatus.valueOf(Alkabot.getConfig().getPresence().getStatus()));
                        if (Alkabot.getConfig().getPresence().getActivity().isShow())
                            presence.setActivity(AlkabotUtils.buildActivity());

                        message.reply("The configuration was successfully reloaded").queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                        message.reply("Failed to reload configuration, check logs").queue();
                    }
                });
                break;

            case "translations":
                messageReceivedEvent.getMessage().reply("Reloading translations").queue(message -> {
                    try {
                        TranslationsLoader translationsLoader = new TranslationsLoader(false);

                        if (translationsLoader.getTranslations() == null)
                            return;

                        Alkabot.setTranslations(translationsLoader.getTranslations());

                        message.reply("Success, " + Alkabot.getTranslations().size() + " loaded translations").queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                        message.reply("Failed to reload translations, check logs").queue();
                    }
                });
                break;

            case "playlists":
                messageReceivedEvent.getMessage().reply("Reloading playlists").queue(message -> {
                    try {
                        Alkabot.getPlaylistManager().read(true);

                        if (Alkabot.getPlaylists() == null)
                            return;

                        message.reply("Success, " + Alkabot.getPlaylists().size() + " loaded playlists").queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                        message.reply("Failed to reload playlists, check logs").queue();
                    }
                });
                break;

            case "music":
                Music.reset();
                messageReceivedEvent.getMessage().reply("OK").queue();
                break;

            default:
                messageReceivedEvent.getMessage().reply("Reload: `configuration`, `translations`, `music`, `playlists`").queue();
                break;

        }

    }

    @Command(name = "shutdown", administrative = true)
    public void shutdown(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().reply("Stopping (may take a moment!)").queue(message -> {
            // Log
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-power-off-title"));
            embedBuilder.setThumbnail(message.getJDA().getSelfUser().getAvatarUrl());
            embedBuilder.setColor(Colors.ORANGE);
            embedBuilder.setDescription(messageReceivedEvent.getAuthor().getAsMention() + " " + Alkabot.t("logs-power-off-description"));
            if (Alkabot.getConfig().getLogs().isAdmin())
                Alkabot.discordLog(embedBuilder.build());

            // Shutdown
            messageReceivedEvent.getJDA().shutdown();
        });
    }

    @Command(name = "config", administrative = true)
    public void config(MessageReceivedEvent messageReceivedEvent) {
        Configuration configuration = Alkabot.getConfig();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("```yaml\n[CONFIGURATION]\n\n");

        stringBuilder.append("Administrators: ").append(configuration.getAdministrators_id().size());
        if (configuration.getAdministrators_id().size() > 0)
            for (String admin : Alkabot.getConfig().getAdministrators_id())
                stringBuilder.append("\n - <@").append(admin).append(">");

        stringBuilder.append("\nGuild: ").append(Alkabot.getGuild().getName()).append(" [").append(Alkabot.getGuild().getId()).append("]\n");
        stringBuilder.append("Admin-only: ").append(b(configuration.isAdmin_only())).append("\n");
        stringBuilder.append("Debug: ").append(b(configuration.isDebug())).append("\n");

        Configuration.Presence presence = configuration.getPresence();
        stringBuilder.append("Presence:\n").append(" - Status: ").append(presence.getStatus()).append("\n");
        stringBuilder.append(" - Activity:\n").append("    - Show: ").append(b(presence.getActivity().isShow())).append("\n");
        stringBuilder.append("    - Type: ").append(presence.getActivity().getType()).append("\n");
        stringBuilder.append("    - Text: \"").append(presence.getActivity().getText()).append("\"\n");

        stringBuilder.append("Welcome-messages:\n").append(" - Enable: ").append(b(configuration.getWelcome_message().isEnable())).append("\n");
        stringBuilder.append(" - Channel: ");
        TextChannel welcomeChannel = Alkabot.getGuild().getTextChannelById(configuration.getWelcome_message().getChannel_id());
        if (welcomeChannel == null)
            stringBuilder.append("null\n");
        else
            stringBuilder.append(welcomeChannel.getName()).append(" [").append(welcomeChannel.getId()).append("]\n");

        stringBuilder.append("Auto-role:\n").append(" - Enable: ").append(b(configuration.getAuto_role().isEnable())).append("\n");
        stringBuilder.append(" - Channel: ");
        Role role = Alkabot.getGuild().getRoleById(configuration.getAuto_role().getRole_id());
        if (role == null)
            stringBuilder.append("null\n");
        else
            stringBuilder.append(role.getName()).append(" [").append(role.getId()).append("]\n");
        
        stringBuilder.append("Commands:\n").append(" - Music: ").append(b(configuration.getCommands().isMusic())).append("\n");
        stringBuilder.append(" - Info: ").append(b(configuration.getCommands().isInfo())).append("\n");
        stringBuilder.append(" - Utils: ").append(b(configuration.getCommands().isUtilities())).append("\n");

        Configuration.Logs logs = configuration.getLogs();
        stringBuilder.append("Logs:\n").append(" - Channel: ");
        TextChannel logChannel = Alkabot.getGuild().getTextChannelById(logs.getChannel_id());
        if (logChannel == null)
            stringBuilder.append("null\n");
        else
            stringBuilder.append(logChannel.getName()).append(" [").append(logChannel.getId()).append("]\n");
        stringBuilder.append(" - Message cache: ").append(logs.getMessage_cache()).append("\n");
        stringBuilder.append(" - Admin: ").append(b(logs.isAdmin())).append("\n");
        stringBuilder.append(" - Join: ").append(b(logs.isJoin())).append("\n");
        stringBuilder.append(" - Left: ").append(b(logs.isLeft())).append("\n");
        stringBuilder.append(" - Voice:\n");
        stringBuilder.append("    - Join: ").append(b(logs.isJoin_voice())).append("\n");
        stringBuilder.append("    - Left: ").append(b(logs.isLeft_voice())).append("\n");
        stringBuilder.append("    - Move: ").append(b(logs.isMove_voice())).append("\n");
        stringBuilder.append("    - Deafen: ").append(b(logs.isVoice_deafen())).append("\n");
        stringBuilder.append("    - Undeafen: ").append(b(logs.isVoice_undeafen())).append("\n");
        stringBuilder.append("    - Mute: ").append(b(logs.isVoice_mute())).append("\n");
        stringBuilder.append("    - Unmute: ").append(b(logs.isVoice_unmute())).append("\n");
        stringBuilder.append(" - Ban: ").append(b(logs.isBan())).append("\n");
        stringBuilder.append(" - Unban: ").append(b(logs.isUnban())).append("\n");
        stringBuilder.append(" - Kick: ").append(b(logs.isKick())).append("\n");
        stringBuilder.append(" - Edit: ").append(b(logs.isEdit())).append("\n");
        stringBuilder.append(" - Delete: ").append(b(logs.isDelete())).append("\n");
        stringBuilder.append("\n```");

        messageReceivedEvent.getMessage().reply(stringBuilder.toString()).queue();
    }

    private String b(boolean boo) {
        return boo ? "yes" : "no";
    }
}
