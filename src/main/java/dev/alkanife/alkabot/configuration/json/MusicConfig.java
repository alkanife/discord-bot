package dev.alkanife.alkabot.configuration.json;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicConfig {

    @SerializedName("auto_stop")
    private boolean autoStop;
}
