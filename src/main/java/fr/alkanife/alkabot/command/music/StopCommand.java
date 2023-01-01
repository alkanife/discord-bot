package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class StopCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.stop.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isStop();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(Alkabot.t("command.music.stop.done")).queue();
        Alkabot.getGuild().getAudioManager().closeAudioConnection();
        //Music.reset(); Disabled, it's not a bug it's a F E A T U R E
    }
}
