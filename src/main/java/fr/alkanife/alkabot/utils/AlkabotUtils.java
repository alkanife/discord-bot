package fr.alkanife.alkabot.utils;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.entities.Activity;

public class AlkabotUtils {

    public static boolean isDevBuild() {
        String version = Alkabot.getVersion().toLowerCase();

        return version.contains("beta")
                || version.contains("dev")
                || version.contains("snapshot")
                || version.contains("alpha");
    }

    public static Activity buildActivity() {
        Alkabot.debug("Building activity");
        Activity.ActivityType activityType = Activity.ActivityType.valueOf(Alkabot.getConfig().getPresence().getActivity().getType());
        return Activity.of(activityType, Alkabot.getConfig().getPresence().getActivity().getText());
    }

}
