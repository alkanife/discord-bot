package dev.alkanife.alkabot.configuration.json;

import com.google.gson.annotations.SerializedName;
import dev.alkanife.alkabot.configuration.json.commands.CommandConfig;
import dev.alkanife.alkabot.configuration.json.guild.GuildConfig;
import dev.alkanife.alkabot.configuration.json.notifications.NotifConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlkabotConfig {

    @SerializedName("lang")
    private String langFile;

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
