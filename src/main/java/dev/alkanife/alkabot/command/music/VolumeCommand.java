package dev.alkanife.alkabot.command.music;

import dev.alkanife.alkabot.command.AbstractCommand;
import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.lang.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class VolumeCommand extends AbstractCommand {

    public VolumeCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getDescription() {
        return Lang.get("command.music.volume.description");
    }

    @Override
    public boolean isEnabled() {
        return alkabot.getConfig().getCommandConfig().getMusicCommandConfig().isNowplaying();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "volume", Lang.get("command.music.volume.input"), false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping volumeOption = event.getOption("volume");

        /*if (volumeOption == null) { TODO
            event.reply(
                    Lang.t("command.music.volume.message")
                            .parse("volume", String.valueOf(alkabot.getMusicData().getVolume()))
                            .getValue()
            ).queue();
            return;
        }

        int volume = volumeOption.getAsInt();

        if (volume > 100 || volume < 0) {
            event.reply(Lang.get("command.music.volume.error")).queue();
            return;
        }

        try {
            alkabot.getMusicData().setVolume(volume);
            alkabot.getMusicManager().getPlayer().setVolume(volume);
            alkabot.updateMusicData();

            event.reply(
                    Lang.t("command.music.volume.message")
                            .parse("volume", String.valueOf(volume))
                            .getValue()
            ).queue();
        } catch (Exception e) {
            alkabot.getLogger().info("");
            event.reply(
                    Lang.t("command.music.volume.failed")
                            .parse("volume", String.valueOf(volume))
                            .getValue()
            ).queue();
        }*/
    }
}
