package fr.alkanife.alkabot.commands;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.commands.utils.Command;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class UtilitiesCommands {

    @Command(name = "copy")
    public void copy(SlashCommandInteractionEvent slashCommandEvent) {
        try {
            OptionMapping copyOption = slashCommandEvent.getOption("input");

            String copyURL = copyOption.getAsString();

            if (!StringUtils.isURL(copyURL)) {
                slashCommandEvent.reply(Alkabot.t("copy-command-error-noturl")).setEphemeral(true).queue();
                return;
            } // 6

            String[] args = copyURL.split("/"); // 4 5 6

            String serverId = args[4];
            String channelId = args[5];
            String messageId = args[6];

            /*Satania.getLogger().info(serverId);
            Satania.getLogger().info(channelId);
            Satania.getLogger().info(messageId);*/

            Guild guild = slashCommandEvent.getJDA().getGuildById(serverId);

            if (guild == null) {
                slashCommandEvent.reply(Alkabot.t("copy-command-error-guild")).setEphemeral(true).queue();
                return;
            }

            TextChannel textChannel = guild.getTextChannelById(channelId);

            if (textChannel == null) {
                slashCommandEvent.reply(Alkabot.t("copy-command-error-channel")).setEphemeral(true).queue();
                return;
            }

            textChannel.retrieveMessageById(messageId).queue(message -> {
                if (message == null) {
                    slashCommandEvent.reply(Alkabot.t("copy-command-error-message")).setEphemeral(true).queue();
                    return;
                }

                /*Satania.getLogger().info(guild.getName());
                Satania.getLogger().info(textChannel.getName());
                Satania.getLogger().info(message.getAuthor().getName()); display*/

                slashCommandEvent.reply("``` " + message.getContentDisplay() + "```").setEphemeral(true).queue();
            });


        } catch (Exception exception) {
            slashCommandEvent.reply(Alkabot.t("copy-command-error")).setEphemeral(true).queue();
        }
    }

}
