package fr.alkanife.alkabot.configuration.json.guild;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuildPresenceActivityConfig {

    @SerializedName("show")
    private boolean showing;
    private String type;
    private String text;
}
