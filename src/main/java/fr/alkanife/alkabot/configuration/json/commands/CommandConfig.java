package fr.alkanife.alkabot.configuration.json.commands;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandConfig {

    private boolean about;
    @SerializedName("music")
    private MusicCommandConfig musicCommandConfig;
    @SerializedName("utilities")
    private UtilsCommandConfig utilsCommandConfig;
}
