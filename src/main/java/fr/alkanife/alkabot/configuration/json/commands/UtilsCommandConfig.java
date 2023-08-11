package fr.alkanife.alkabot.configuration.json.commands;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilsCommandConfig {

    @SerializedName("info")
    private InfoUtilsCommandConfig infoUtilsCommandConfig;
    private boolean copy;
}
