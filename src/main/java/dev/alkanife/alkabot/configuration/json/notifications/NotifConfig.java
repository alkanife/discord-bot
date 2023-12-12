package dev.alkanife.alkabot.configuration.json.notifications;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifConfig {

    @SerializedName("self")
    private SelfNotifConfig selfNotifConfig;
    @SerializedName("message")
    private MessageNotifConfig messageNotifConfig;
    @SerializedName("member")
    private MemberNotifConfig memberNotifConfig;
    @SerializedName("moderator")
    private ModNotifConfig modNotifConfig;
    @SerializedName("voice")
    private VoiceNotifConfig voiceNotifConfig;
}
