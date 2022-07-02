package fr.alkanife.alkabot;

public class StatusMaker {

    public static String translations() { ////❌❓❔⚠️
        StringBuilder stringBuilder = new StringBuilder();

        if (Alkabot.getTranslations().size() == 0)
            stringBuilder.append("❌ No translations were loaded, check logs");
        else
            stringBuilder.append("✅ `").append(Alkabot.getTranslations().size()).append("` translations were loaded");

        return stringBuilder.toString();
    }

    /*
    if (Alkabot.getTranslations().size() == 0)
                stringBuilder.append("No translations were loaded");
            else
                stringBuilder.append("`").append(Alkabot.getTranslations().size()).append("` translations were loaded");

            stringBuilder.append("\n");

            if (Alkabot.getConfig().getAdministrators_id().size() == 0)
                stringBuilder.append("No administrators");
            else {
                stringBuilder.append("`").append(Alkabot.getConfig().getAdministrators_id().size()).append("` administrators: ");
                for (String admin : Alkabot.getConfig().getAdministrators_id())
                    stringBuilder.append("<@").append(admin).append("> ");
            }

            stringBuilder.append("\n");
            stringBuilder.append(Alkabot.getConfig().isAdmin_only() ? "Admin only enabled" : "Admin only disabled").append("\n");
            stringBuilder.append(Alkabot.isDebugging() ? "Debug mode" : "Standard mode").append("\n");
            stringBuilder.append(Alkabot.getConfig().isWelcome_message() ? "Welcome message enabled" : "Welcome message disabled").append("\n");
            stringBuilder.append(Alkabot.getConfig().getAuto_role().isEnable() ? "Auto-role enabled" : "Auto-role disabled")
                    .append(Alkabot.getConfig().getAuto_role().isEnable() ? (" (<@&" + Alkabot.getConfig().getAuto_role().getRole_id() + ">)") : "").append("\n");
            stringBuilder.append(Alkabot.getConfig().getCommands().isMusic() ? "Music commands enabled" : "Music commands disabled").append("\n");
            stringBuilder.append(Alkabot.getConfig().getCommands().isInfo() ? "Info commands enabled" : "Info commands disabled").append("\n");
            stringBuilder.append(Alkabot.getConfig().getCommands().isUtilities() ? "Utilities commands enabled" : "Utilities commands disabled").append("\n");

            if (Alkabot.getConfig().getPlaylists().size() == 0)
                stringBuilder.append("No playlists");
            else {
                stringBuilder.append("`").append(Alkabot.getConfig().getPlaylists().size()).append("` playlists: ");
                for (Configuration.Playlist playlist : Alkabot.getConfig().getPlaylists())
                    stringBuilder.append("[").append(playlist.getName()).append("](").append(playlist.getUrl()).append(")");
            }

            stringBuilder.append("\nLog channel: <#").append(Alkabot.getConfig().getLogs().getChannel_id()).append(">").append("\n");
            stringBuilder.append("Log cache: ").append(Alkabot.getConfig().getLogs().getMessage_cache()).append("\n");
            stringBuilder.append("Logging enabled for: ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isJoin() ? "'join' " : "");
            stringBuilder.append(Alkabot.getConfig().getLogs().isLeft() ? "'left' " : "");
            stringBuilder.append(Alkabot.getConfig().getLogs().isBan() ? "'ban' " : "");
            stringBuilder.append(Alkabot.getConfig().getLogs().isKick() ? "'kick' " : "");
            stringBuilder.append(Alkabot.getConfig().getLogs().isTimeout() ? "'timeout' " : "");
            stringBuilder.append(Alkabot.getConfig().getLogs().isEdit() ? "'edit' " : "");
            stringBuilder.append(Alkabot.getConfig().getLogs().isDelete() ? "'delete' " : "");
            stringBuilder.append("\nLogging disabled for: ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isJoin() ? "" : "'join' ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isLeft() ? "" : "'left' ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isBan() ? "" : "'ban' ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isKick() ? "" : "'kick' ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isTimeout() ? "" : "'timeout' ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isEdit() ? "" : "'edit' ");
            stringBuilder.append(Alkabot.getConfig().getLogs().isDelete() ? "" : "'delete' ");
     */

}
