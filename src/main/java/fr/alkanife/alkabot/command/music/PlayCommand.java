package fr.alkanife.alkabot.command.music;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.command.AbstractCommand;
import fr.alkanife.alkabot.music.shortcut.Shortcut;
import fr.alkanife.alkabot.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@SuppressWarnings("DataFlowIssue")
public class PlayCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return Alkabot.t("command.music.play.description");
    }

    @Override
    public boolean isEnabled() {
        return Alkabot.getConfig().getCommands().getMusic().isPlay();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "input", Alkabot.t("command.music.play.input_description"), true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        play(event, false, false);
    }

    public static void play(SlashCommandInteractionEvent event, boolean priority, boolean force) {
        Alkabot.getMusicManager().setLastMusicCommandChannel(event.getChannel());

        event.deferReply().queue();

        Alkabot.getMusicManager().connect(event.getMember());

        String input = event.getOption("input").getAsString();

        if (input.startsWith("https://open.spotify.com/playlist")) {
            if (Alkabot.supportSpotify())
                Alkabot.getMusicManager().getSpotifyLoader().load(event, input, priority, force);
            else
                event.reply("command.music.play.error.no_spotify_support").queue();
        } else {
            if (!StringUtils.isURL(input)) {
                Shortcut shortcut = Alkabot.getShortcutManager().getShortcut(input);

                if (shortcut == null)
                    input = "ytsearch: " + input;
                else
                    if (StringUtils.isURL(shortcut.getQuery()))
                        input = shortcut.getQuery();
                    else
                        input = "ytsearch: " + shortcut.getQuery();
            }

            Alkabot.getMusicManager().getLavaplayerLoader().load(event, input, priority, force);
        }
    }
}
