package fr.alkanife.alkabot.utils;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlkabotUtils {



    public static Activity buildActivity() {
        Alkabot.debug("Building activity");

        Activity.ActivityType activityType = Activity.ActivityType.valueOf(Alkabot.getConfig().getGuild().getPresence().getActivity().getType());
        return Activity.of(activityType, Alkabot.getConfig().getGuild().getPresence().getActivity().getText());
    }

}
