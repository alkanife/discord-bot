package fr.alkanife.alkabot.commands.utilities;

import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.util.StringUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CopyCommand extends AbstractCommand {

    public CopyCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "copy";
    }

    @Override
    public String getDescription() {
        return alkabot.t("command.utilities.copy.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getUtilsCommandConfig().isCopy();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "input", alkabot.t("command.utilities.copy.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            OptionMapping copyOption = event.getOption("input");

            //noinspection DataFlowIssue
            String copyURL = copyOption.getAsString();

            if (!StringUtils.isURL(copyURL)) {
                event.reply(alkabot.t("command.utilities.copy.error.not_url")).setEphemeral(true).queue();
                return;
            }

            String[] args = copyURL.split("/");

            String serverId = args[4];
            String channelId = args[5];
            String messageId = args[6];

            Guild guild = event.getJDA().getGuildById(serverId);

            if (guild == null) {
                event.reply(alkabot.t("command.utilities.copy.error.guild")).setEphemeral(true).queue();
                return;
            }

            TextChannel textChannel = guild.getTextChannelById(channelId);

            if (textChannel == null) {
                event.reply(alkabot.t("command.utilities.copy.error.channel")).setEphemeral(true).queue();
                return;
            }

            textChannel.retrieveMessageById(messageId).queue(message -> {
                if (message == null) {
                    event.reply(alkabot.t("command.utilities.copy.error.message")).setEphemeral(true).queue();
                    return;
                }

                event.reply("``` " + message.getContentDisplay() + "```").setEphemeral(true).queue();
            });

        } catch (Exception exception) {
            event.reply(alkabot.t("command.utilities.copy.error.generic")).setEphemeral(true).queue();
        }
    }
}
