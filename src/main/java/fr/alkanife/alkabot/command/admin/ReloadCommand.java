package fr.alkanife.alkabot.command.admin;

import fr.alkanife.alkabot.command.AbstractAdminCommand;
import fr.alkanife.alkabot.command.AdminCommandExecution;
import fr.alkanife.alkabot.command.CommandManager;
import fr.alkanife.alkabot.configuration.ConfigLoader;
import fr.alkanife.alkabot.lang.TranslationsLoader;
import fr.alkanife.alkabot.lang.TranslationsManager;
import fr.alkanife.alkabot.music.MusicDataLoader;
import fr.alkanife.alkabot.music.MusicManager;

public class ReloadCommand extends AbstractAdminCommand {

    public ReloadCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getUsage() {
        return "reload <module>";
    }

    @Override
    public String getDescription() {
        return "Reload module";
    }

    @Override
    public boolean isDiscordOnly() {
        return false;
    }

    @Override
    public void execute(AdminCommandExecution execution) {
        String[] command = execution.command().split(" ");

        if (command.length <= 1) {
            execution.reply("Usage: reload <lang,config,music,musicdata>");
            return;
        }

        switch (command[1].toLowerCase()) {
            case "lang" -> {
                try {
                    alkabot.getLogger().info("Reloading translations");
                    alkabot.setTranslationsManager(new TranslationsManager(alkabot));
                    new TranslationsLoader(alkabot).load();
                    execution.reply("Reload complete");
                } catch (Exception exception) {
                    execution.reply("Reload failed");
                    alkabot.printFileError("translations", exception);
                }
            }

            case "config" -> {
                try {
                    alkabot.getLogger().info("Reloading config");

                    new ConfigLoader(alkabot).load();

                    alkabot.setupAutoRole();
                    alkabot.setupWelComeChannel();
                    alkabot.updateCommands();

                    execution.reply("Reload complete");
                } catch (Exception exception) {
                    execution.reply("Reload failed");
                    alkabot.printFileError("configuration", exception);
                }
            }

            case "music" -> {
                try {
                    alkabot.getMusicManager().disable();
                    alkabot.setMusicManager(new MusicManager(alkabot));
                    alkabot.getMusicManager().initialize();
                    execution.reply("Reload complete");
                } catch (Exception exception) {
                    execution.reply("Reload failed");
                    alkabot.getLogger().error("Failed to reload music", exception);
                }
            }

            case "musicdata" -> {
                try {
                    alkabot.getLogger().info("Reloading music data");
                    new MusicDataLoader(alkabot).load();
                    execution.reply("Reload complete");
                } catch (Exception exception) {
                    execution.reply("Reload failed");
                    alkabot.printFileError("music data", exception);
                }
            }

            default -> execution.reply("Unknown module");
        }

    }
}
