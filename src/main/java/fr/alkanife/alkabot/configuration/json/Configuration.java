package fr.alkanife.alkabot.configuration.json;

import com.google.gson.annotations.SerializedName;
import fr.alkanife.alkabot.configuration.json.commands.CommandConfig;
import fr.alkanife.alkabot.configuration.json.guild.GuildConfig;
import fr.alkanife.alkabot.configuration.json.notifications.NotifConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {

    @SerializedName("lang_path")
    private String langFilePath;

    @SerializedName("musicdata_path")
    private String musicDataPath;

    @SerializedName("administrator_ids")
    private List<String> adminIds;

    @SerializedName("guild")
    private GuildConfig guildConfig;

    @SerializedName("welcome_message")
    private WelcomeMessageConfig welcomeMessageConfig;

    @SerializedName("auto_role")
    private AutoRoleConfig autoRoleConfig;

    @SerializedName("music")
    private MusicConfig musicConfig;

    @SerializedName("commands")
    private CommandConfig commandConfig;

    @SerializedName("notifications")
    private NotifConfig notifConfig;

}
