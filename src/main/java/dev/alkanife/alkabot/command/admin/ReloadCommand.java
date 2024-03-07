package dev.alkanife.alkabot.command.admin;

import dev.alkanife.alkabot.command.CommandManager;
import dev.alkanife.alkabot.file.FileManipulation;

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
            execution.reply("Usage: reload <lang, config, music, musicdata, tokens>");
            return;
        }

        switch (command[1].toLowerCase()) {
            /*case "lang" -> { TODO fix this
                alkabot.getLogger().debug("Reloading language");
                TranslationsLoader translationsLoader = new TranslationsLoader(alkabot);
                translationsLoader.reload();

                sendResponse(execution, translationsLoader);
            }

            case "tokens" -> {
                alkabot.getLogger().debug("Reloading tokens - Please note that the Discord token will not be reloaded");
                TokenManager tokenLoader = new TokenManager(alkabot);
                tokenLoader.reload();

                sendResponse(execution, tokenLoader);
            }

            case "config" -> {
                alkabot.getLogger().debug("Reloading configuration (are you crazy?)");
                ConfigLoader configLoader = new ConfigLoader(alkabot);
                configLoader.load();

                if (configLoader.success) {
                    alkabot.setupAutoRole();
                    alkabot.setupWelComeChannel();
                    alkabot.updateCommands();
                }

                sendResponse(execution, configLoader);
            }

            case "music" -> {
                alkabot.getLogger().debug("Reloading music");
                try {
                    alkabot.getMusicManager().disable();
                    alkabot.setMusicManager(new MusicManager(alkabot));
                    alkabot.getMusicManager().initialize();
                    execution.reply("Reload complete!");
                } catch (Exception exception) {
                    execution.reply("Reload failed");
                    alkabot.getLogger().error("Failed to reload music", exception);
                }
            }

            case "musicdata" -> {
                alkabot.getLogger().debug("Reloading music data");
                MusicDataLoader musicDataLoader = new MusicDataLoader(alkabot);
                musicDataLoader.load();

                sendResponse(execution, musicDataLoader);
            }*/

            default -> execution.reply("Unknown module");
        }

    }

    public void sendResponse(AdminCommandExecution execution, FileManipulation jsonLoader) {
        /*if (jsonLoader.success)
            execution.reply("Reload complete!");
        else
            execution.reply("Reload failed");*/
    }
}
