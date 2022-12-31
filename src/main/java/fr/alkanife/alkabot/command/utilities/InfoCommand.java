package fr.alkanife.alkabot.command.utilities;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.info.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getUtilities().getInfo().isEmote()
                || Alkabot.getConfig().getCommands().getUtilities().getInfo().isServer()
                || Alkabot.getConfig().getCommands().getUtilities().getInfo().isMember();
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription());

        List<SubcommandData> subs = new ArrayList<>();

        if (Alkabot.getConfig().getCommands().getUtilities().getInfo().isServer())
            subs.add(new SubcommandData("server", Alkabot.t("command.info.server.description")));

        if (Alkabot.getConfig().getCommands().getUtilities().getInfo().isMember())
            subs.add(new SubcommandData("member", Alkabot.t("command.info.member.description"))
                    .addOption(OptionType.USER, "input", Alkabot.t("command.info.member.input_description"), true));

        if (Alkabot.getConfig().getCommands().getUtilities().getInfo().isEmote())
            subs.add(new SubcommandData("emote", Alkabot.t("command.info.emote.description"))
                    .addOption(OptionType.STRING, "input", Alkabot.t("command.info.member.input_description"), true));

        if (subs.size() > 0)
            commandData.addSubcommands(subs);

        return commandData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}