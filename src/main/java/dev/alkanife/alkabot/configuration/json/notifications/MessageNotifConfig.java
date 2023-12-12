package dev.alkanife.alkabot.configuration.json.notifications;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageNotifConfig {

    @SerializedName("channel_id")
    private String channelId;

    private int cache;
    private boolean edit, delete;
}
