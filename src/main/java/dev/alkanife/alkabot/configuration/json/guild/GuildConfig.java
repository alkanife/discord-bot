package dev.alkanife.alkabot.configuration.json.guild;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuildConfig {

    @SerializedName("guild_id")
    private String guildId;
    @SerializedName("presence")
    private GuildPresenceConfig guildPresenceConfig;
}
