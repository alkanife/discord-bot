package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class DestroyCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "destroy";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.destroy.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isDestroy();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());
        Alkabot.getMusicManager().reset();
        event.reply(Alkabot.t("command.music.destroy.done")).queue();
    }
}
