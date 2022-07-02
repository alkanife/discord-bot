package fr.alkanife.alkabot.commands;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.commands.utils.Command;
import fr.alkanife.alkabot.configuration.Configuration;
import fr.alkanife.alkabot.configuration.ConfigurationLoader;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.music.Music;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.Presence;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class AdminCommands {

    public static void help(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().reply("Administrative commands: `stop`, `info`, `reload`").queue();
    }

    /*@Command(name = "test", administrative = true)
    public void test(MessageReceivedEvent messageReceivedEvent) {

    }*/

    @Command(name = "reload", administrative = true)
    public void reload(MessageReceivedEvent messageReceivedEvent) {
        String content = messageReceivedEvent.getMessage().getContentDisplay().toLowerCase(Locale.ROOT);

        String[] args = content.split(" ");

        //TODO bug quand on fait juste reload (out of bound)

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
                        if (Alkabot.getConfig().getPresence().getActivity().isShow()) {
                            Alkabot.debug("Building activity");
                            Activity.ActivityType activityType = Activity.ActivityType.valueOf(Alkabot.getConfig().getPresence().getActivity().getType());
                            presence.setActivity(Activity.of(activityType, Alkabot.getConfig().getPresence().getActivity().getText()));
                        }

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

            case "music":;
                Music.reset();
                messageReceivedEvent.getMessage().reply("OK").queue();
                break;

            default:
                messageReceivedEvent.getMessage().reply("Reload: `configuration`, `translations`, `music`").queue();
                break;

        }

    }

    @Command(name = "stop", administrative = true)
    public void stop(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().reply("Stopping (may take a moment!)").queue(message -> {
            // Log
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Alkabot.t("logs-power-off-title"));
            embedBuilder.setThumbnail(message.getJDA().getSelfUser().getAvatarUrl());
            embedBuilder.setColor(new Color(236, 111, 33));
            embedBuilder.setDescription(messageReceivedEvent.getAuthor().getAsMention() + " " + Alkabot.t("logs-power-off-description"));
            if (Alkabot.getConfig().getLogs().isAdmin())
                Alkabot.discordLog(embedBuilder.build());

            // Shutdown
            messageReceivedEvent.getJDA().shutdown();
        });
    }

    @Command(name = "info", administrative = true)
    public void info(MessageReceivedEvent messageReceivedEvent) {
        // Display everything about the current configuration
        EmbedBuilder embedBuilder = new EmbedBuilder();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Translations: ").append(Alkabot.getTranslations().size()).append("\n");

        stringBuilder.append("Administrators: (").append(Alkabot.getConfig().getAdministrators_id().size()).append(")");
        if (Alkabot.getConfig().getAdministrators_id().size() > 0)
            for (String admin : Alkabot.getConfig().getAdministrators_id())
                stringBuilder.append(" <@").append(admin).append(">");
        stringBuilder.append("\n");

        stringBuilder.append("Admin only: ").append(Alkabot.getConfig().isAdmin_only()).append("\n");
        stringBuilder.append("Debug mode: ").append(Alkabot.isDebugging()).append("\n");

        stringBuilder.append("Welcome-message(s): ").append(Alkabot.getConfig().getWelcome_message().isEnable());
        if (Alkabot.getConfig().getWelcome_message().isEnable()) {
            TextChannel textChannel = Alkabot.getGuild().getTextChannelById(Alkabot.getConfig().getWelcome_message().getChannel_id());
            if (textChannel != null)
                stringBuilder.append(" (#").append(textChannel.getName()).append(")");
        }
        stringBuilder.append("\n");

        stringBuilder.append("Auto-role: ").append(Alkabot.getConfig().getAuto_role().isEnable());
        if (Alkabot.getConfig().getAuto_role().isEnable()) {
            Role role = Alkabot.getGuild().getRoleById(Alkabot.getConfig().getAuto_role().getRole_id());
            if (role != null)
                stringBuilder.append(" (@").append(role.getName()).append(")");
        }
        stringBuilder.append("\n");

        stringBuilder.append("Music commands: ").append(Alkabot.getConfig().getCommands().isMusic()).append("\n");
        stringBuilder.append("Info commands: ").append(Alkabot.getConfig().getCommands().isInfo()).append("\n");
        stringBuilder.append("Utilities commands: ").append(Alkabot.getConfig().getCommands().isUtilities()).append("\n");
        stringBuilder.append("Playlists: ").append(Alkabot.getPlaylists().size()).append("\n");

        stringBuilder.append("Log channel: ");
        TextChannel textChannel = Alkabot.getGuild().getTextChannelById(Alkabot.getConfig().getLogs().getChannel_id());
        if (textChannel == null)
            stringBuilder.append("null");
        else
            stringBuilder.append("#").append(textChannel.getName());
        stringBuilder.append("\n");

        stringBuilder.append("Log message cache: ").append(Alkabot.getConfig().getLogs().getMessage_cache()).append("\n");
        stringBuilder.append("Logging for 'admin': ").append(Alkabot.getConfig().getLogs().isAdmin()).append("\n");
        stringBuilder.append("Logging for 'join': ").append(Alkabot.getConfig().getLogs().isJoin()).append("\n");
        stringBuilder.append("Logging for 'left': ").append(Alkabot.getConfig().getLogs().isLeft()).append("\n");
        stringBuilder.append("Logging for 'join_voice': ").append(Alkabot.getConfig().getLogs().isJoin_voice()).append("\n");
        stringBuilder.append("Logging for 'left_voice': ").append(Alkabot.getConfig().getLogs().isLeft_voice()).append("\n");
        stringBuilder.append("Logging for 'move_voice': ").append(Alkabot.getConfig().getLogs().isMove_voice()).append("\n");
        stringBuilder.append("Logging for 'voice_deafen': ").append(Alkabot.getConfig().getLogs().isVoice_deafen()).append("\n");
        stringBuilder.append("Logging for 'voice_undeafen': ").append(Alkabot.getConfig().getLogs().isVoice_undeafen()).append("\n");
        stringBuilder.append("Logging for 'voice_mute': ").append(Alkabot.getConfig().getLogs().isVoice_mute()).append("\n");
        stringBuilder.append("Logging for 'voice_unmute': ").append(Alkabot.getConfig().getLogs().isVoice_unmute()).append("\n");
        stringBuilder.append("Logging for 'ban': ").append(Alkabot.getConfig().getLogs().isBan()).append("\n");
        stringBuilder.append("Logging for 'unban': ").append(Alkabot.getConfig().getLogs().isUnban()).append("\n");
        stringBuilder.append("Logging for 'kick': ").append(Alkabot.getConfig().getLogs().isKick()).append("\n");
        stringBuilder.append("Logging for 'edit': ").append(Alkabot.getConfig().getLogs().isEdit()).append("\n");
        stringBuilder.append("Logging for 'delete': ").append(Alkabot.getConfig().getLogs().isDelete()).append("\n");

        embedBuilder.setDescription(stringBuilder.toString());

        messageReceivedEvent.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }
}
