package dev.alkanife.alkabot.commands;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class AboutCommand extends AbstractCommand {

    public AboutCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.about.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().isAbout();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        //Todo
        event.reply(
                Lang.t("command.about.content")
                        .parseBot(alkabot)
                        .parseAdmins(alkabot)
                        .getValue()
        ).queue();
    }
}
